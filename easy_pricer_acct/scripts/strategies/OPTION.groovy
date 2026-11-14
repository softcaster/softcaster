import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase
import org.softcaster.engine.enums.PremiumStyle
import org.softcaster.core.data.OptionMasterData
import java.math.RoundingMode


// ============================================================================
// OPZIONI E ACCUMULATOR (asset_class_script_mapping -> OPTION)
//
//   opzioni:      FFO, BFO (su future), FXO (su cambio), BDO (su titolo)
//   accumulator:  FXA, CMA (OTC, un contratto per anagrafica)
//
// Tutto nella settlement currency dello strumento; la controvalutazione nella
// valuta di sistema e' un processo a valle, come per gli altri script.
//
// Premio "equity-style" (opzioni CME / OTC, accumulator): al fair value.
//
//   - Il premio pagato o incassato e' il valore di carico del contratto, su un
//     unico conto con segno (OPTION_PREMIUM): saldo dare per i contratti
//     acquistati, avere per quelli scritti. Un unico conto serve perche' acquisti
//     e vendite della stessa serie si compensano: la vendita che chiude un
//     acquisto storna il carico allo stesso conto, come per i titoli.
//   - A fine giornata (MTM) il non realizzato va su DERIV_UNREALIZED_GAIN/LOSS,
//     contropartita OPTION_FV_ADJUSTMENT; il realizzato delle chiusure su
//     DERIV_REALIZED_GAIN/LOSS, contropartita OPTION_PREMIUM, che resta cosi' al
//     costo della quantita' residua. Il conto di utile o perdita segue il segno
//     del cumulato (postResultBySign): un deal cancellato riporta a zero anche
//     i singoli conti.
//   - Margine iniziale (solo opzioni quotate scritte, se il prodotto ne ha uno)
//     come per i future, sull'esecuzione.
//
// Premio "futures-style" (Eurex): nessun premio, il MtM giornaliero e' margine
// di variazione realizzato, come per i future (script FUT).
//
// Scadenza (step MATURITY del life-cycle): l'opzione si chiude con un deal EOD
// al valore intrinseco (0 se fuori dal denaro), con consegna fisica il
// sottostante entra con un deal EOD al prezzo di mercato; l'evento MATURITY
// rilascia il margine iniziale. I regolamenti periodici degli accumulator non
// generano ancora eventi.
// ============================================================================

def txn   = ctx.txn
def event = ctx.event

int settlementCcy = ctx.settlementCurrency
int currencyScale = ctx.getCurrencyScale(false)

def money = { value ->
    new BigDecimal((value ?: 0).toString()).setScale(currencyScale, RoundingMode.HALF_UP)
}

def md = ctx.masterData
boolean futuresStyle = (md instanceof OptionMasterData) && md.premiumStyle == PremiumStyle.FUTURES_STYLE


// ============================================================================
// ACCOUNT RESOLUTION
// ============================================================================

String accPremium         = accountResolver.resolve("OPTION_PREMIUM", settlementCcy)
String accFvAdjustment    = accountResolver.resolve("OPTION_FV_ADJUSTMENT", settlementCcy)
String accPositionControl = accountResolver.resolve("POSITION_CONTROL", settlementCcy)
String accCash            = accountResolver.resolve("CASH_ACCOUNT", settlementCcy)
String accInitialMargin   = accountResolver.resolve("INITIAL_MARGIN", settlementCcy)
String accSettlementLiab  = accountResolver.resolve("SETTLEMENT_LIAB", settlementCcy)


BigDecimal initialMargin = money(ctx.getFutureInitialMargin())
BigDecimal premium       = money(ctx.getOptionPremium())

// chi compra paga il premio; un premio negativo (accumulator) lo incassa
boolean premiumPaid = txn != null && ((txn.txnSide == TxnSide.BUY) == (txn.price >= 0))


// ============================================================================
// EVENT PROCESSING
// ============================================================================

switch (event.eventType) {

    // ========================================================================
    // TRADE EXECUTED
    // ========================================================================

case EventType.TRADE_EXECUTED:

    /*
     * Margine iniziale, se presente (opzioni scritte quotate, futures-style):
     *
     *     DR INITIAL_MARGIN
     *        CR SETTLEMENT_LIABILITY
     *
     * Il premio entra a bilancio al regolamento.
     */

    if (initialMargin.signum() > 0) {
        ctx.journal.debit(accInitialMargin, initialMargin, settlementCcy)
        ctx.journal.credit(accSettlementLiab, initialMargin, settlementCcy)
    }

    ctx.accountingPhase = futuresStyle ? AccountingPhase.OFFICIAL_POSTED : AccountingPhase.MEMO_POSTED
    break


    // ========================================================================
    // SETTLEMENT
    // ========================================================================

case EventType.SETTLEMENT:

    /*
     * Regolamento del premio.
     *
     * Pagato (acquisto):
     *
     *     DR OPTION_PREMIUM       CR POSITION_CONTROL
     *     DR POSITION_CONTROL     CR CASH
     *
     * Incassato (vendita, o acquisto di un accumulator a premio negativo):
     *
     *     DR POSITION_CONTROL     CR OPTION_PREMIUM
     *     DR CASH                 CR POSITION_CONTROL
     */

    if (!futuresStyle && premium.signum() > 0) {
        if (premiumPaid) {
            ctx.journal.debit(accPremium, premium, settlementCcy)
            ctx.journal.credit(accPositionControl, premium, settlementCcy)
            ctx.journal.debit(accPositionControl, premium, settlementCcy)
            ctx.journal.credit(accCash, premium, settlementCcy)
        } else {
            ctx.journal.debit(accPositionControl, premium, settlementCcy)
            ctx.journal.credit(accPremium, premium, settlementCcy)
            ctx.journal.debit(accCash, premium, settlementCcy)
            ctx.journal.credit(accPositionControl, premium, settlementCcy)
        }
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break


    // ========================================================================
    // MTM
    // ========================================================================

case EventType.MTM:

    if (futuresStyle) {

        /*
         * Margine di variazione: variazione del risultato complessivo della
         * posizione dall'ultima fotografia contabilizzata, realizzato.
         *
         * Perdita:   DR FUT_REALIZED_LOSS   CR VARIATION_MARGIN
         * Profitto:  DR VARIATION_MARGIN    CR FUT_REALIZED_GAIN
         */

        BigDecimal dailyPnl = money(ctx.getMtmVariation())
        String accVariationMargin = accountResolver.resolve("VARIATION_MARGIN", settlementCcy)

        if (dailyPnl.signum() < 0) {
            ctx.journal.debit(accountResolver.resolve("FUT_REALIZED_LOSS", settlementCcy), dailyPnl.abs(), settlementCcy)
            ctx.journal.credit(accVariationMargin, dailyPnl.abs(), settlementCcy)
        } else if (dailyPnl.signum() > 0) {
            ctx.journal.debit(accVariationMargin, dailyPnl, settlementCcy)
            ctx.journal.credit(accountResolver.resolve("FUT_REALIZED_GAIN", settlementCcy), dailyPnl, settlementCcy)
        }
        break
    }

    ctx.postUnrealizedResult(accFvAdjustment,
            accountResolver.resolve("DERIV_UNREALIZED_GAIN", settlementCcy),
            accountResolver.resolve("DERIV_UNREALIZED_LOSS", settlementCcy))
    ctx.postRealizedResult(accPremium,
            accountResolver.resolve("DERIV_REALIZED_GAIN", settlementCcy),
            accountResolver.resolve("DERIV_REALIZED_LOSS", settlementCcy))
    break


    // ========================================================================
    // MATURITY
    // ========================================================================

case EventType.MATURITY:

    /*
     * Scadenza della serie (step MATURITY): rilascio del margine iniziale del
     * deal. La chiusura della posizione e l'eventuale sottostante sono deal
     * EOD, contabilizzati come gli altri.
     *
     *     DR SETTLEMENT_LIABILITY
     *        CR INITIAL_MARGIN
     */

    if (initialMargin.signum() > 0) {
        ctx.journal.debit(accSettlementLiab, initialMargin, settlementCcy)
        ctx.journal.credit(accInitialMargin, initialMargin, settlementCcy)
    }

    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // TRADE AMENDED / TRADE CANCELED
    // ========================================================================

case EventType.TRADE_AMENDED:
case EventType.TRADE_CANCELED:

    /*
     * Storno di tutte le righe del deal (margine e premio). Un deal senza
     * premio e senza margine non ha scritto nulla.
     */

    if (ctx.hasJournalLines()) {
        ctx.reverseJournal()
    }

    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // UNSUPPORTED EVENT
    // ========================================================================

default:
    throw new IllegalArgumentException("Unsupported OPTION accounting event: " + event.eventType)
}
