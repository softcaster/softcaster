import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.TxnOrigin
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase
import java.math.MathContext
import java.math.RoundingMode

// ============================================================================
// MUTUI (XRM tasso fisso, FRM tasso variabile), al costo ammortizzato
// ============================================================================
//
// Deal dell'utente:
//     BUY  = la banca presta    -> attivo LOAN_ASSET, spese incassate
//     SELL = la banca prende a prestito -> passivo LOAN_LIABILITY, spese pagate
//
// Deal EOD (LoanRepaymentService): rimborso della quota capitale di una rata,
// alla pari, di segno opposto alla posizione:
//     SELL = rimborso ricevuto, riduce LOAN_ASSET
//     BUY  = rimborso pagato, riduce LOAN_LIABILITY
//
// La cessione di un mutuo a terzi (SELL dell'utente su una posizione attiva)
// non e' gestita: un SELL dell'utente e' sempre un finanziamento ricevuto.
//
// Interessi: ratei (ACCRUAL) e incasso / pagamento della rata (COUPON) con il
// segno della posizione, come i titoli. MtM non contabilizzato.
//
// Tutto nella valuta del mutuo.
// ============================================================================

MathContext MC = new MathContext(18, RoundingMode.HALF_UP)

def bd = { value ->
    if (value == null)
    return BigDecimal.ZERO
    if (value instanceof BigDecimal)
    return value
    return new BigDecimal(value.toString())
}

def money = { BigDecimal value, int scale ->
    if (value == null)
    return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP)
    return value.setScale(scale, RoundingMode.HALF_UP)
}

int ccy = ctx.settlementCurrency
int currencyScale = ctx.getCurrencyScale(false)

def txn = ctx.txn
def event = ctx.event

BigDecimal quantity = bd(txn ? txn.quantity : 0)
BigDecimal price = bd(txn ? txn.price : 0)
BigDecimal accruedInterest = bd(ctx.bondAccruedInterest)
BigDecimal fees = bd(ctx.loanFees)

TxnSide side = txn ? txn.txnSide : TxnSide.BUY
boolean isBuy = side == TxnSide.BUY
boolean isSell = side == TxnSide.SELL
boolean isRepayment = txn != null && txn.txnOrigin == TxnOrigin.EOD

// ============================================================================
// ACCOUNTS
// ============================================================================
String accLoanAsset = accountResolver.resolve("LOAN_ASSET", ccy)
String accLoanLiability = accountResolver.resolve("LOAN_LIABILITY", ccy)
String accAccruedInterest = accountResolver.resolve("ACCRUED_INTEREST", ccy)
String accInterestIncome = accountResolver.resolve("LOAN_INTEREST_INCOME", ccy)
String accInterestExpense = accountResolver.resolve("LOAN_INTEREST_EXPENSE", ccy)
String accFeeIncome = accountResolver.resolve("LOAN_FEE_INCOME", ccy)
String accFeeExpense = accountResolver.resolve("LOAN_FEE_EXPENSE", ccy)
String accCash = accountResolver.resolve("CASH_ACCOUNT", ccy)

String accCommitment = accountResolver.resolve("LOAN_COMMITMENT", ccy)
String accObsClearing = accountResolver.resolve("OBS_CLEARING", ccy)

// ============================================================================
// AMOUNTS
// ============================================================================
//
// Prezzo in percentuale del debito residuo (multiplier dei mutui = 1).
// ============================================================================
BigDecimal cleanMoney = money(quantity.multiply(price, MC).divide(new BigDecimal(100), MC), currencyScale)
BigDecimal accruedMoney = money(accruedInterest, currencyScale)
BigDecimal totalMoney = cleanMoney.add(accruedMoney)
BigDecimal feeMoney = money(fees, currencyScale)

switch (event.eventType) {

    // ========================================================================
    // TRADE EXECUTED: impegno fuori bilancio fino al regolamento
    // ========================================================================
case EventType.TRADE_EXECUTED:

    if (isBuy) {
        ctx.journal.debit(accCommitment, totalMoney, ccy)
        ctx.journal.credit(accObsClearing, totalMoney, ccy)
    } else if (isSell) {
        ctx.journal.debit(accObsClearing, totalMoney, ccy)
        ctx.journal.credit(accCommitment, totalMoney, ccy)
    } else {
        throw new IllegalStateException("Unsupported transaction side: " + side)
    }
    ctx.accountingPhase = AccountingPhase.MEMO_POSTED
    break

    // ========================================================================
    // SETTLEMENT
    // ========================================================================
case EventType.SETTLEMENT:

    ctx.reverseJournal()

    if (isRepayment) {
        if (isSell) {
            /*
             * Rimborso ricevuto
             *     DR CASH
             *        CR LOAN_ASSET
             */
            ctx.journal.debit(accCash, cleanMoney, ccy)
            ctx.journal.credit(accLoanAsset, cleanMoney, ccy)
        } else {
            /*
             * Rimborso pagato
             *     DR LOAN_LIABILITY
             *        CR CASH
             */
            ctx.journal.debit(accLoanLiability, cleanMoney, ccy)
            ctx.journal.credit(accCash, cleanMoney, ccy)
        }
    } else if (isBuy) {
        /*
         * Erogazione (o acquisto) del mutuo
         *     DR LOAN_ASSET
         *     DR ACCRUED_INTEREST
         *        CR CASH
         * Spese incassate
         *     DR CASH
         *        CR LOAN_FEE_INCOME
         */
        ctx.journal.debit(accLoanAsset, cleanMoney, ccy)
        if (accruedMoney.signum() != 0) {
            ctx.journal.debit(accAccruedInterest, accruedMoney, ccy)
        }
        ctx.journal.credit(accCash, totalMoney, ccy)
        if (feeMoney.signum() != 0) {
            ctx.journal.debit(accCash, feeMoney, ccy)
            ctx.journal.credit(accFeeIncome, feeMoney, ccy)
        }
    } else if (isSell) {
        /*
         * Finanziamento ricevuto
         *     DR CASH
         *        CR LOAN_LIABILITY
         *        CR ACCRUED_INTEREST
         * Spese pagate
         *     DR LOAN_FEE_EXPENSE
         *        CR CASH
         */
        ctx.journal.debit(accCash, totalMoney, ccy)
        ctx.journal.credit(accLoanLiability, cleanMoney, ccy)
        if (accruedMoney.signum() != 0) {
            ctx.journal.credit(accAccruedInterest, accruedMoney, ccy)
        }
        if (feeMoney.signum() != 0) {
            ctx.journal.debit(accFeeExpense, feeMoney, ccy)
            ctx.journal.credit(accCash, feeMoney, ccy)
        }
    } else {
        throw new IllegalStateException("Unsupported transaction side for SETTLEMENT: " + side)
    }
    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break

    // ========================================================================
    // ACCRUAL: importo con il segno della posizione
    // ========================================================================
case EventType.ACCRUAL:

    BigDecimal accrualAmount = money(bd(ctx.getAccruedInterestAmount()), currencyScale)
    if (accrualAmount.signum() == 0) {
        ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
        break
    }
    if (ctx.getPositionSide() == TxnSide.BUY) {
        // DR ACCRUED_INTEREST  CR LOAN_INTEREST_INCOME
        ctx.journal.debit(accAccruedInterest, accrualAmount, ccy)
        ctx.journal.credit(accInterestIncome, accrualAmount, ccy)
    } else {
        // DR LOAN_INTEREST_EXPENSE  CR ACCRUED_INTEREST
        ctx.journal.debit(accInterestExpense, accrualAmount.negate(), ccy)
        ctx.journal.credit(accAccruedInterest, accrualAmount.negate(), ccy)
    }
    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break

    // ========================================================================
    // COUPON: interessi della rata, con il segno della posizione
    // ========================================================================
case EventType.COUPON:

    BigDecimal interestAmount = money(bd(ctx.getCouponAmount()), currencyScale)
    if (interestAmount.signum() == 0) {
        ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
        break
    }
    if (ctx.getPositionSide() == TxnSide.BUY) {
        // incasso: DR CASH  CR ACCRUED_INTEREST
        ctx.journal.debit(accCash, interestAmount, ccy)
        ctx.journal.credit(accAccruedInterest, interestAmount, ccy)
    } else {
        // pagamento: DR ACCRUED_INTEREST  CR CASH
        ctx.journal.debit(accAccruedInterest, interestAmount.negate(), ccy)
        ctx.journal.credit(accCash, interestAmount.negate(), ccy)
    }
    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break

    // ========================================================================
    // MTM: mutui al costo ammortizzato, nessuna registrazione
    // ========================================================================
case EventType.MTM:
    break

case EventType.TRADE_AMENDED:
case EventType.TRADE_CANCELED:

    ctx.reverseJournal()
    ctx.accountingPhase = txn.txnAcctPhase
    break

default:
    throw new IllegalArgumentException("Unsupported LOAN accounting event: " + event.eventType)
}
