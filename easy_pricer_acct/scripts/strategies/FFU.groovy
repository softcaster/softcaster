import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase


def txn   = ctx.txn
def event = ctx.event


// ============================================================================
// FUTURE ACCOUNTING
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
// Risk Engine Forex, che genera una successiva operazione FX.
// ============================================================================


int settlementCcy = ctx.settlementCurrency


// ============================================================================
// ACCOUNT RESOLUTION
// ============================================================================
//
// Tutti gli account del Future sono risolti nella settlement currency.
//
// Nessun riferimento alla System Currency.
// Nessun riferimento a EUR.
// ============================================================================


String accInitialMargin = accountResolver.resolve("INITIAL_MARGIN", settlementCcy)
String accVariationMargin = accountResolver.resolve("VARIATION_MARGIN", settlementCcy)
String accFutureRealizedLoss = accountResolver.resolve("FUT_REALIZED_LOSS",settlementCcy)
String accFutureRealizedGain = accountResolver.resolve("FUT_REALIZED_GAIN", settlementCcy)
String accSettlementLiability =accountResolver.resolve("SETTLEMENT_LIAB", settlementCcy)

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
     *
     * Viene contabilizzato solamente l'Initial Margin.
     *
     *
     *     DR INITIAL_MARGIN
     *     CR SETTLEMENT_LIABILITY
     *
     *
     * Tutto nella settlement currency del Future.
     */


    BigDecimal initialMargin = ctx.getFutureInitialMargin()

    if (initialMargin != null &&
        initialMargin.compareTo(BigDecimal.ZERO) > 0) {

        ctx.journal.debit(
            accInitialMargin,
            initialMargin,
            settlementCcy)

        ctx.journal.credit(
            accSettlementLiability,
            initialMargin,
            settlementCcy)
    }


    /*
     * Il Future è cash-settled / marginato e non ha
     * un normale settlement T+N come un bond o uno spot FX.
     *
     * L'Initial Margin è quindi contabilizzato
     * direttamente.
     */

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED

    break


    // ========================================================================
    // MTM
    // ========================================================================

case EventType.MTM:

    /*
     * Il Daily Mark-to-Market del Future viene regolato
     * tramite Variation Margin.
     *
     * Il P&L è quindi REALIZED.
     *
     * IMPORTANTE:
     *
     * ctx.getRealizedPnl() deve restituire il P&L
     * nella settlement currency del Future.
     *
     * Esempio:
     *
     *     CME EUR/USD
     *     Daily P&L = +1,250 USD
     *
     * La contabilità rimane completamente in USD.
     */


    BigDecimal dailyPnl = ctx.getRealizedPnl()

    if (dailyPnl == null) {
        dailyPnl = BigDecimal.ZERO
    }


    // --------------------------------------------------------------------
    // LOSS
    // --------------------------------------------------------------------

    if (dailyPnl.compareTo(BigDecimal.ZERO) < 0) {

        BigDecimal loss =
        dailyPnl.abs()


        /*
         * Perdita giornaliera:
         *
         *     DR FUT_REALIZED_LOSS
         *     CR VARIATION_MARGIN
         */

        ctx.journal.debit(
            accFutureRealizedLoss,
            loss,
            settlementCcy)

        ctx.journal.credit(
            accVariationMargin,
            loss,
            settlementCcy)
    }


    // --------------------------------------------------------------------
    // GAIN
    // --------------------------------------------------------------------

    else if (dailyPnl.compareTo(BigDecimal.ZERO) > 0) {

        /*
         * Profitto giornaliero:
         *
         *     DR VARIATION_MARGIN
         *     CR FUT_REALIZED_GAIN
         */

        ctx.journal.debit(
            accVariationMargin,
            dailyPnl,
            settlementCcy)

        ctx.journal.credit(
            accFutureRealizedGain,
            dailyPnl,
            settlementCcy)
    }

    break


    // ========================================================================
    // SETTLEMENT
    // ========================================================================

case EventType.SETTLEMENT:
    /*
    Nessun evento di settlement per i Futures
    */
    break


    // ========================================================================
    // TRADE AMENDED
    // ========================================================================

case EventType.TRADE_AMENDED:

    /*
     * Lo storno è centralizzato nel Context.
     */

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
    // ROLLOVER
    // ========================================================================

case EventType.ROLLOVER:

    /*
     * Il rollover chiude il vecchio Future.
     *
     * L'eventuale P&L della posizione viene già realizzato
     * tramite la marginatura.
     *
     * Lo storno della parte contabile del vecchio contratto
     * viene gestito dal Context.
     */

    ctx.reverseJournal()

    ctx.accountingPhase = txn.txnAcctPhase
    break
}