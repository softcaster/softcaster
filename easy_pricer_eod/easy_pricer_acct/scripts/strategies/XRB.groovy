import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase
import java.math.MathContext
import java.math.RoundingMode

// ============================================================================
// 1. UTILITIES
// ============================================================================

MathContext MC =
new MathContext(18, RoundingMode.HALF_UP)


def bd = { value ->

    if (value == null)
    return BigDecimal.ZERO

    if (value instanceof BigDecimal)
    return value

    return new BigDecimal(value.toString())
}


def money = { BigDecimal value, int scale ->

    if (value == null)
    return BigDecimal.ZERO.setScale(
        scale,
        RoundingMode.HALF_UP)

    return value.setScale(
        scale,
        RoundingMode.HALF_UP)
}


// ============================================================================
// 2. CURRENCY
// ============================================================================
//
// Il bond viene contabilizzato esclusivamente nella propria valuta.
//
// Esempi:
//
//     BTP      -> EUR
//     US T-Bond -> USD
//     Gilt     -> GBP
//     Swiss Bond -> CHF
//
// La System Currency NON entra nello script.
//
// La conversione verso la System Currency è responsabilità
// del Risk Engine Forex.
// ============================================================================

int settlementCcy =
ctx.settlementCurrency


int currencyScale =
ctx.getCurrencyScale(false)


// ============================================================================
// 3. TRANSACTION DATA
// ============================================================================

BigDecimal quantity =
bd(ctx.txn?.quantity)

BigDecimal price =
bd(ctx.txn?.price)

BigDecimal multiplier =
bd(ctx.multiplier)

BigDecimal accruedInterest =
bd(ctx.bondAccruedInterest)


if (multiplier.compareTo(BigDecimal.ZERO) == 0) {
    multiplier = BigDecimal.ONE
}


TxnSide side =
ctx.txn?.txnSide ?: TxnSide.BUY


boolean isBuy =
side == TxnSide.BUY

boolean isSell =
side == TxnSide.SELL


// ============================================================================
// 4. ACCOUNTS
// ============================================================================
//
// Tutti gli account sono risolti nella valuta del bond.
// ============================================================================

String accBondAsset =
accountResolver.resolve(
                "BOND_ASSET",
    settlementCcy)


String accAccruedInterest =
accountResolver.resolve(
                "ACCRUED_INTEREST",
    settlementCcy)


String accInterestIncome =
accountResolver.resolve(
                "INTEREST_INCOME",
    settlementCcy)


String accPositionControl =
accountResolver.resolve(
                "POSITION_CONTROL",
    settlementCcy)


String accCash =
accountResolver.resolve(
                "CASH_ACCOUNT",
    settlementCcy)


// ============================================================================
// 5. MEMO ACCOUNTS
// ============================================================================

String accCommitment =
accountResolver.resolve(
                "BOND_COMMITMENT",
    settlementCcy)


String accObsClearing =
accountResolver.resolve(
                "OBS_CLEARING",
    settlementCcy)


// ============================================================================
// 6. TRADE AMOUNTS
// ============================================================================
//
// Clean value:
//
//     quantity × price × multiplier
//
// Nessun rounding intermedio.
// ============================================================================

BigDecimal cleanAmount =
quantity
.multiply(price, MC)
.multiply(multiplier, MC)


BigDecimal totalAmount =
cleanAmount.add(
    accruedInterest,
    MC)


// Rounding esclusivamente sul risultato monetario finale.

BigDecimal cleanMoney =
money(
    cleanAmount,
    currencyScale)


BigDecimal accruedMoney =
money(
    accruedInterest,
    currencyScale)


BigDecimal totalMoney =
money(
    totalAmount,
    currencyScale)


// ============================================================================
// 7. ACCOUNTING EVENTS
// ============================================================================

switch (ctx.event.eventType) {


    // ========================================================================
    // TRADE EXECUTED
    // ========================================================================

case EventType.TRADE_EXECUTED:

    /*
     * Il trade del bond viene registrato off-balance.
     *
     * BUY:
     *
     *     DR BOND_COMMITMENT
     *     CR OBS_CLEARING
     *
     * SELL:
     *
     *     DR OBS_CLEARING
     *     CR BOND_COMMITMENT
     *
     * Tutto nella valuta del bond.
     */

    if (isBuy) {

        ctx.journal.debit(
            accCommitment,
            totalMoney,
            settlementCcy)

        ctx.journal.credit(
            accObsClearing,
            totalMoney,
            settlementCcy)

    } else if (isSell) {

        ctx.journal.debit(
            accObsClearing,
            totalMoney,
            settlementCcy)

        ctx.journal.credit(
            accCommitment,
            totalMoney,
            settlementCcy)
    }

    ctx.accountingPhase = AccountingPhase.MEMO_POSTED
    break


    // ========================================================================
    // SETTLEMENT
    // ========================================================================

case EventType.SETTLEMENT:

    /*
     * Prima eliminiamo il memorandum accounting
     * generato da TRADE_EXECUTED.
     */

    ctx.reverseJournal()


    if (isBuy) {

        /*
         * 1. Acquisizione del bond
         *
         *     DR BOND_ASSET
         *     DR ACCRUED_INTEREST
         *     CR POSITION_CONTROL
         */

        ctx.journal.debit(
            accBondAsset,
            cleanMoney,
            settlementCcy)


        if (accruedMoney.compareTo(
                BigDecimal.ZERO) != 0) {

            ctx.journal.debit(
                accAccruedInterest,
                accruedMoney,
                settlementCcy)
        }


        ctx.journal.credit(
            accPositionControl,
            totalMoney,
            settlementCcy)


        /*
         * 2. Regolamento cash
         *
         *     DR POSITION_CONTROL
         *     CR CASH
         */

        ctx.journal.debit(
            accPositionControl,
            totalMoney,
            settlementCcy)

        ctx.journal.credit(
            accCash,
            totalMoney,
            settlementCcy)


    } else if (isSell) {

        /*
         * 1. Eliminazione del bond dalla posizione
         *
         *     DR POSITION_CONTROL
         *     CR BOND_ASSET
         *     CR ACCRUED_INTEREST
         */

        ctx.journal.debit(
            accPositionControl,
            totalMoney,
            settlementCcy)


        ctx.journal.credit(
            accBondAsset,
            cleanMoney,
            settlementCcy)


        if (accruedMoney.compareTo(
                BigDecimal.ZERO) != 0) {

            ctx.journal.credit(
                accAccruedInterest,
                accruedMoney,
                settlementCcy)
        }


        /*
         * 2. Incasso cash
         *
         *     DR CASH
         *     CR POSITION_CONTROL
         */

        ctx.journal.debit(
            accCash,
            totalMoney,
            settlementCcy)

        ctx.journal.credit(
            accPositionControl,
            totalMoney,
            settlementCcy)
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
    break


    // ========================================================================
    // ACCRUAL
    // ========================================================================

case EventType.ACCRUAL:

    /*
     * L'accrual è interamente nella valuta del bond.
     *
     *     DR ACCRUED_INTEREST
     *     CR INTEREST_INCOME
     */

    BigDecimal accrualAmount =
    bd(ctx.getAccruedInterestAmount())


    accrualAmount =
    money(
        accrualAmount,
        currencyScale)


    if (accrualAmount.compareTo(
            BigDecimal.ZERO) != 0) {

        ctx.journal.debit(
            accAccruedInterest,
            accrualAmount,
            settlementCcy)

        ctx.journal.credit(
            accInterestIncome,
            accrualAmount,
            settlementCcy)
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
    break


    // ========================================================================
    // COUPON
    // ========================================================================

case EventType.COUPON:

    /*
     * Coupon cash flow nella valuta del bond.
     *
     *     DR CASH
     *     CR ACCRUED_INTEREST
     *
     * Nessuna conversione nella System Currency.
     *
     * Il Risk Engine Forex genererà successivamente
     * l'operazione FX necessaria.
     */

    BigDecimal couponAmount =
    bd(ctx.getCouponAmount())


    couponAmount =
    money(
        couponAmount,
        currencyScale)


    if (couponAmount.compareTo(
            BigDecimal.ZERO) != 0) {

        ctx.journal.debit(
            accCash,
            couponAmount,
            settlementCcy)

        ctx.journal.credit(
            accAccruedInterest,
            couponAmount,
            settlementCcy)
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
    break


    // ========================================================================
    // MATURITY
    // ========================================================================

case EventType.MATURITY:

    /*
     * Non utilizziamo txn.quantity.
     *
     * Il lifecycle engine deve fornire il nominale
     * effettivamente outstanding alla maturity.
     */

    BigDecimal outstandingNominal =
    bd(ctx.getOutstandingNominal())


    BigDecimal maturityAmount =
    outstandingNominal
    .multiply(multiplier, MC)


    maturityAmount =
    money(
        maturityAmount,
        currencyScale)


    if (maturityAmount.compareTo(
            BigDecimal.ZERO) != 0) {

        /*
         *     DR CASH
         *     CR BOND_ASSET
         */

        ctx.journal.debit(
            accCash,
            maturityAmount,
            settlementCcy)

        ctx.journal.credit(
            accBondAsset,
            maturityAmount,
            settlementCcy)
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
    break


    // ========================================================================
    // TRADE AMENDED
    // ========================================================================

case EventType.TRADE_AMENDED:

    ctx.reverseJournal()
    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // TRADE CANCELED
    // ========================================================================

case EventType.TRADE_CANCELED:

    ctx.reverseJournal()
    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // UNSUPPORTED EVENT
    // ========================================================================

default:

    throw new IllegalArgumentException(
                "Unsupported XRB accounting event: "
        + ctx.event.eventType)
}