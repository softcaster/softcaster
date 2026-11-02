import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase
import java.math.RoundingMode


// ============================================================================
// FUTURE ACCOUNTING (BFU, FFU: asset_class_script_mapping -> FUT)
//
// Il Future viene contabilizzato esclusivamente nella propria
// settlement currency.
//
// Esempio CME EUR/USD:
//
//     underlying currency = EUR
//     settlement currency = USD
//
// Tutta la contabilità del Future è quindi in USD.
//
// La conversione nella System Currency è demandata al
// Risk Engine Forex, come per i titoli (XRB).
// ============================================================================

def txn   = ctx.txn
def event = ctx.event

int settlementCcy = ctx.settlementCurrency
int currencyScale = ctx.getCurrencyScale(false)

def money = { value ->
    new BigDecimal((value ?: 0).toString()).setScale(currencyScale, RoundingMode.HALF_UP)
}


// ============================================================================
// ACCOUNT RESOLUTION
// ============================================================================

String accInitialMargin       = accountResolver.resolve("INITIAL_MARGIN", settlementCcy)
String accVariationMargin     = accountResolver.resolve("VARIATION_MARGIN", settlementCcy)
String accFutureRealizedLoss  = accountResolver.resolve("FUT_REALIZED_LOSS", settlementCcy)
String accFutureRealizedGain  = accountResolver.resolve("FUT_REALIZED_GAIN", settlementCcy)
String accSettlementLiability = accountResolver.resolve("SETTLEMENT_LIAB", settlementCcy)


// Initial Margin della transazione (componente INITIAL_MARGIN), 0 se assente
BigDecimal initialMargin = money(ctx.getFutureInitialMargin())


// ============================================================================
// EVENT PROCESSING
// ============================================================================

switch (event.eventType) {

    // ========================================================================
    // TRADE EXECUTED
    // ========================================================================

case EventType.TRADE_EXECUTED:

    /*
     * L'apertura del Future non contabilizza il nozionale.
     * Viene contabilizzato solamente l'Initial Margin:
     *
     *     DR INITIAL_MARGIN
     *        CR SETTLEMENT_LIABILITY
     *
     * Il Future non ha un settlement T+N: va subito a bilancio.
     */

    if (initialMargin.signum() > 0) {
        ctx.journal.debit(accInitialMargin, initialMargin, settlementCcy)
        ctx.journal.credit(accSettlementLiability, initialMargin, settlementCcy)
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break


    // ========================================================================
    // MTM
    // ========================================================================

case EventType.MTM:

    /*
     * Evento di posizione del MtM di fine giornata (nessuna transazione): il
     * Daily Mark-to-Market viene regolato tramite Variation Margin, il P&L è
     * quindi REALIZED.
     *
     * ctx.getMtmVariation() = variazione del risultato della posizione
     * (realizzato + non realizzato) dall'ultima fotografia contabilizzata:
     * è il margine della giornata, compreso l'ultimo giorno di una posizione
     * chiusa (prezzo di chiusura). Nella settlement currency del Future.
     *
     * Perdita:   DR FUT_REALIZED_LOSS   CR VARIATION_MARGIN
     * Profitto:  DR VARIATION_MARGIN    CR FUT_REALIZED_GAIN
     */

    BigDecimal dailyPnl = money(ctx.getMtmVariation())

    if (dailyPnl.signum() < 0) {
        ctx.journal.debit(accFutureRealizedLoss, dailyPnl.abs(), settlementCcy)
        ctx.journal.credit(accVariationMargin, dailyPnl.abs(), settlementCcy)
    } else if (dailyPnl.signum() > 0) {
        ctx.journal.debit(accVariationMargin, dailyPnl, settlementCcy)
        ctx.journal.credit(accFutureRealizedGain, dailyPnl, settlementCcy)
    }
    break


    // ========================================================================
    // SETTLEMENT
    // ========================================================================

case EventType.SETTLEMENT:

    // Nessuna scrittura: la fase contabile della transazione resta invariata
    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // MATURITY
    // ========================================================================

case EventType.MATURITY:

    /*
     * Chiusura della posizione: la Clearing House rilascia l'Initial Margin.
     *
     *     DR SETTLEMENT_LIABILITY
     *        CR INITIAL_MARGIN
     */

    if (initialMargin.signum() > 0) {
        ctx.journal.debit(accSettlementLiability, initialMargin, settlementCcy)
        ctx.journal.credit(accInitialMargin, initialMargin, settlementCcy)
    }

    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // TRADE AMENDED / TRADE CANCELED / ROLLOVER
    // ========================================================================

case EventType.TRADE_AMENDED:
case EventType.TRADE_CANCELED:
case EventType.ROLLOVER:

    /*
     * Lo storno è centralizzato nel Context. Senza Initial Margin il
     * TRADE_EXECUTED non ha scritto righe: non c'è nulla da stornare.
     */

    if (initialMargin.signum() > 0) {
        ctx.reverseJournal()
    }

    ctx.accountingPhase = txn.txnAcctPhase
    break


    // ========================================================================
    // UNSUPPORTED EVENT
    // ========================================================================

default:
    throw new IllegalArgumentException("Unsupported FUT accounting event: " + event.eventType)
}
