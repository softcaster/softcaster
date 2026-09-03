import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase

def txn = ctx.txn
def event = ctx.event

int baseCcy  = ctx.masterDataCurrency
int quoteCcy = ctx.settlementCurrency

double baseAmount  = txn? txn.quantity : 0
double quoteAmount = txn? txn.quantity * txn.price : 0

// RISOLUZIONE CONTI IN TESTATA
// Conti Memorandum (Serie 600000 - Conto unico universale per divisa)
String accCommitBase   = accountResolver.resolve("FXSPOT_COMMITMENT", baseCcy)     
String accObsClearingBase = accountResolver.resolve("OBS_CLEARING", baseCcy)       
String accCommitQuote  = accountResolver.resolve("FXSPOT_COMMITMENT", quoteCcy)
String accObsClearingQuote = accountResolver.resolve("OBS_CLEARING", quoteCcy)

// Conti Reali di Stato Patrimoniale (Serie 120000 / 130000 / 240000)
String accPositionBase = accountResolver.resolve("POSITION_CONTROL", baseCcy) 
String accSpotBase = accountResolver.resolve("FX_SPOT_ASSET", baseCcy)     
String accSpotQuote = accountResolver.resolve("FX_SPOT_ASSET", quoteCcy)    
String accPositionQuote = accountResolver.resolve("POSITION_CONTROL", quoteCcy) 

switch(event.eventType) {

case EventType.TRADE_EXECUTED:
    // =========================================================================
    // GIORNO T: SOLO CONTI MEMORANDUM (Fuori Bilancio)
    // =========================================================================
    if (txn.txnSide == TxnSide.BUY) { 
        // Compro Valuta Base (EUR) -> Entra l'impegno in Dare
        ctx.journal.debit(accCommitBase, baseAmount, baseCcy)
        ctx.journal.credit(accObsClearingBase, baseAmount, baseCcy)
            
        // Vendo Valuta Quote (USD) -> Esce l'impegno in Avere
        ctx.journal.debit(accObsClearingQuote, quoteAmount, quoteCcy)
        ctx.journal.credit(accCommitQuote, quoteAmount, quoteCcy)
    } else if (txn.txnSide == TxnSide.SELL) { 
        // Vendo Valuta Base (EUR) -> Esce l'impegno in Avere
        ctx.journal.debit(accObsClearingBase, baseAmount, baseCcy)
        ctx.journal.credit(accCommitBase, baseAmount, baseCcy)
            
        // Compro Valuta Quote (USD) -> Entra l'impegno in Dare
        ctx.journal.debit(accCommitQuote, quoteAmount, quoteCcy)
        ctx.journal.credit(accObsClearingQuote, quoteAmount, quoteCcy)
    }
        
    ctx.accountingPhase = AccountingPhase.MEMO_POSTED
    break

case EventType.SETTLEMENT:
    // =========================================================================
    // GIORNO T+2: STORNO MEMORANDUM + ACCENSIONE REGISTRI REALI 
    // =========================================================================
    if (txn.txnSide == TxnSide.BUY) {
        // 1. STORNO IN NERO DEI CONTI MEMORANDUM (* -1.0) -> Gli impegni muoiono
        ctx.journal.debit(accCommitBase, baseAmount * (-1.0), baseCcy)
        ctx.journal.credit(accObsClearingBase, baseAmount * (-1.0), baseCcy)
        ctx.journal.debit(accObsClearingQuote, quoteAmount * (-1.0), quoteCcy)
        ctx.journal.credit(accCommitQuote, quoteAmount * (-1.0), quoteCcy)

        // 2. ACCENSIONE DELLA CONTABILITÀ REALE PATRIMONIALE
        ctx.journal.debit(accSpotBase, baseAmount, baseCcy)
        ctx.journal.credit(accPositionBase, baseAmount, baseCcy)

        ctx.journal.debit(accPositionQuote, quoteAmount, quoteCcy)
        ctx.journal.credit(accSpotQuote, quoteAmount, quoteCcy)
    } 
    else if (txn.txnSide == TxnSide.SELL) {
        // 1. STORNO IN NERO DEI CONTI MEMORANDUM (* -1.0)
        ctx.journal.debit(accObsClearingBase, baseAmount * (-1.0), baseCcy)
        ctx.journal.credit(accCommitBase, baseAmount * (-1.0), baseCcy)
        ctx.journal.debit(accCommitQuote, quoteAmount * (-1.0), quoteCcy)
        ctx.journal.credit(accObsClearingQuote, quoteAmount * (-1.0), quoteCcy)

        ctx.journal.debit(accPositionBase, baseAmount, baseCcy)     
        ctx.journal.credit(accSpotBase, baseAmount, baseCcy)           

        ctx.journal.debit(accSpotQuote, quoteAmount, quoteCcy)         
        ctx.journal.credit(accPositionQuote, quoteAmount, quoteCcy)          
    }
        
    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
    break

case EventType.MTM:
    double unrealizedAmt = ctx.getUnrealizedPnl() 
    if (unrealizedAmt < 0) {
        String accUnrealizedLoss = accountResolver.resolve("UNREALIZED_FX_LOSS", 1)
        ctx.journal.debit(accUnrealizedLoss, Math.abs(unrealizedAmt), 1)
        ctx.journal.credit(accPosition, Math.abs(unrealizedAmt), quoteCcy)
    } else if (unrealizedAmt > 0) {
        String accUnrealizedGain = accountResolver.resolve("UNREALIZED_FX_GAIN", 1)
        ctx.journal.debit(accPosition, unrealizedAmt, quoteCcy)
        ctx.journal.credit(accUnrealizedGain, unrealizedAmt, 1)
    }

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break

case EventType.ROLLOVER:
    String accRealizedLoss = accountResolver.resolve("REALIZED_FX_LOSS", quoteCcy)
    double swapCost = ctx.getSwapPointsCost() 
    ctx.journal.debit(accRealizedLoss, swapCost, quoteCcy)
    ctx.journal.credit(accPosition, swapCost, quoteCcy)

    ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED
    break

case EventType.TRADE_AMENDED:
case EventType.TRADE_CANCELED:
    ctx.accountingPhase = txn.txnAcctPhase
    ctx.reverseJournal()
    break
}
