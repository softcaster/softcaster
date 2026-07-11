import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType

def txn = ctx.txn
def event = ctx.event

int tradeCcy = txn.masterData.currency?.idCurrency 
int eurCcy   = 1 // ID fisso dell'Euro

// =========================================================================
// STRATEGIA 1: FUTURE IN EURO (es. Contratti Eurex o Euro-denominati)
// =========================================================================
if (tradeCcy == eurCcy) {
    String accInitMargin  = accountResolver.resolve("INITIAL_MARGIN", eurCcy)
    String accVarMargin   = accountResolver.resolve("VARIATION_MARGIN", eurCcy)
    String accFutLoss     = accountResolver.resolve("FUT_REALIZED_LOSS", eurCcy)
    String accFutGain     = accountResolver.resolve("FUT_REALIZED_GAIN", eurCcy)
    String accSettlement  = accountResolver.resolve("SETTLEMENT_LIAB", eurCcy)

    switch(event.eventType) {
        case EventType.TRADE_EXECUTED:
            // L'apertura del Future non carica il nozionale a bilancio, ma versa il deposito cauzionale
            double marginAmt = ctx.getFutureInitialMargin()
            if (marginAmt > 0) {
                ctx.journal.debit(accInitMargin, marginAmt, eurCcy)
                ctx.journal.credit(accSettlement, marginAmt, eurCcy)
            }
            // Future ha 0 business days, va subito a bilancio
            ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
            break

        case EventType.MTM:
            // Il Mark-to-Market quotidiano del CME/Eurex si liquida CASH (Variation Margin) -> P&L Realizzato
            double dailyPnl = ctx.getRealizedPnl() 
            if (dailyPnl < 0) {
                ctx.journal.debit(accFutLoss, Math.abs(dailyPnl), eurCcy)
                ctx.journal.credit(accVarMargin, Math.abs(dailyPnl), eurCcy)
            } else if (dailyPnl > 0) {
                ctx.journal.debit(accVarMargin, dailyPnl, eurCcy)
                ctx.journal.credit(accFutGain, dailyPnl, eurCcy)
            }
            break

        case EventType.SETTLEMENT:
            ctx.accountingPhase = txn.txnAcctPhase
            break
            
        case EventType.MATURITY:
            // Chiusura della posizione: la Clearing House rilascia l'Initial Margin originario
            double releaseMargin = ctx.getFutureInitialMargin()
            if (releaseMargin > 0) {
                ctx.journal.debit(accSettlement, releaseMargin, eurCcy)
                ctx.journal.credit(accInitMargin, releaseMargin, eurCcy)
            }
            ctx.accountingPhase = txn.txnAcctPhase
            break

        case EventType.TRADE_AMENDED:
        case EventType.TRADE_CANCELED:
        case EventType.ROLLOVER:
            ctx.reverseJournal()
            ctx.accountingPhase = txn.txnAcctPhase
            break
    }
    return // Fine esecuzione per contratti in Euro
}

// =========================================================================
// STRATEGIA 2: FUTURE IN VALUTA ESTERA (CME - USD, CHF, CAD, GBP, ecc.)
// =========================================================================
String accInitMargin   = accountResolver.resolve("INITIAL_MARGIN", tradeCcy)
String accVarMargin    = accountResolver.resolve("VARIATION_MARGIN", tradeCcy)
String accFutLoss      = accountResolver.resolve("FUT_REALIZED_LOSS", eurCcy) // Ricavo/Costo sempre in EUR a CE
String accFutGain      = accountResolver.resolve("FUT_REALIZED_GAIN", eurCcy) // Ricavo/Costo sempre in EUR a CE
String accSettlement   = accountResolver.resolve("SETTLEMENT_LIAB", eurCcy)
String accPosCcy       = accountResolver.resolve("CURRENCY_POSITION", tradeCcy) 
String accCtrlEUR      = accountResolver.resolve("POSITION_CONTROL", eurCcy)    

switch(event.eventType) {

    case EventType.TRADE_EXECUTED:
        double marginCcy = ctx.getFutureInitialMargin()
        double marginEUR = marginCcy / txn.fxRate

        if (marginCcy > 0) {
            // Spostamento della garanzia nel blocco in valuta del contratto
            ctx.journal.debit(accInitMargin, marginCcy, tradeCcy)
            ctx.journal.credit(accPosCcy, marginCcy, tradeCcy)
            
            // Regolamento finanziario del margine convertito in Euro
            ctx.journal.debit(accCtrlEUR, marginEUR, eurCcy)
            ctx.journal.credit(accSettlement, marginEUR, eurCcy)
            // Future ha 0 business days, va subito a bilancio
            ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
        }
        break

    case EventType.MTM:
        // Liquidazione quotidiana del Variation Margin in valuta, specchiata sul CE in Euro
        double dailyPnlCcy = ctx.getRealizedPnl() 
        double dailyPnlEUR = Math.abs(dailyPnlCcy) / ctx.getFxRate()

        if (dailyPnlCcy < 0) {
            // Perdita della giornata: esce liquidità dal conto dei margini di variazione
            ctx.journal.debit(accFutLoss, dailyPnlEUR, eurCcy)
            ctx.journal.credit(accCtrlEUR, dailyPnlEUR, eurCcy)
            
            ctx.journal.debit(accPosCcy, Math.abs(dailyPnlCcy), tradeCcy)
            ctx.journal.credit(accVarMargin, Math.abs(dailyPnlCcy), tradeCcy)
        } 
        else if (dailyPnlCcy > 0) {
            // Profitto della giornata: entra liquidità sul conto dei margini di variazione
            ctx.journal.debit(accVarMargin, dailyPnlCcy, tradeCcy)
            ctx.journal.credit(accPosCcy, dailyPnlCcy, tradeCcy)
            
            ctx.journal.debit(accCtrlEUR, dailyPnlEUR, eurCcy)
            ctx.journal.credit(accFutGain, dailyPnlEUR, eurCcy)
        }
        break

    case EventType.SETTLEMENT:
        ctx.accountingPhase = txn.txnAcctPhase
        break
        
    case EventType.MATURITY:
        // Chiusura definitiva della posizione: rilascio e sblocco dell'Initial Margin in valuta
        double releaseMarginCcy = ctx.getFutureInitialMargin()
        double releaseMarginEUR = releaseMarginCcy / ctx.getFxRate()

        if (releaseMarginCcy > 0) {
            ctx.journal.debit(accPosCcy, releaseMarginCcy, tradeCcy)
            ctx.journal.credit(accInitMargin, releaseMarginCcy, tradeCcy)
            
            ctx.journal.debit(accSettlement, releaseMarginEUR, eurCcy)
            ctx.journal.credit(accCtrlEUR, releaseMarginEUR, eurCcy)
        }
        ctx.accountingPhase = txn.txnAcctPhase
        break

    case EventType.TRADE_AMENDED:
    case EventType.TRADE_CANCELED:
    case EventType.ROLLOVER:
        ctx.accountingPhase = txn.txnAcctPhase
        // Gestione nativa dello storno in nero a importo negativo accentrata nel Context Java
        ctx.reverseJournal()
        break
}
