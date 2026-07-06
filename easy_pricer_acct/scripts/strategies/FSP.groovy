import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType
import org.softcaster.engine.enums.AccountingPhase

def txn = ctx.txn
def event = ctx.event

int baseCcy  = txn.masterData.bcy?.idCurrency
int quoteCcy = txn.masterData.ccy?.idCurrency
int eurCcy   = 1 

double baseAmount  = txn.quantity
double quoteAmount = txn.quantity * txn.price

// RISOLUZIONE CONTI IN TESTATA
String accCommitBase   = accountResolver.resolve("FXSPOT_COMMITMENT", baseCcy)
String accObsClearingBase = accountResolver.resolve("OBS_CLEARING", baseCcy)
String accCommitQuote  = accountResolver.resolve("FXSPOT_COMMITMENT", quoteCcy)
String accObsClearingQuote = accountResolver.resolve("OBS_CLEARING", quoteCcy)

String accPositionControl = accountResolver.resolve("POSITION_CONTROL", eurCcy) 
String accSpotBase         = accountResolver.resolve("FX_SPOT_ASSET", baseCcy)     
String accSpotQuote        = accountResolver.resolve("FX_SPOT_ASSET", quoteCcy)    
String accPosition         = accountResolver.resolve("CURRENCY_POSITION", quoteCcy) 

switch(event.eventType) {

    case EventType.TRADE_EXECUTED:
        // DATA T: Registrazione dell'impegno esclusivamente FUORI BILANCIO
        if (txn.txnSide == TxnSide.BUY) { 
            ctx.journal.debit(accCommitBase, baseAmount, baseCcy)
            ctx.journal.credit(accObsClearingBase, baseAmount, baseCcy)
            
            ctx.journal.debit(accObsClearingQuote, quoteAmount, quoteCcy)
            ctx.journal.credit(accCommitQuote, quoteAmount, quoteCcy)
        } else if (txn.txnSide == TxnSide.SELL) { 
            ctx.journal.debit(accObsClearingBase, baseAmount, baseCcy)
            ctx.journal.credit(accCommitBase, baseAmount, baseCcy)
            
            ctx.journal.debit(accCommitQuote, quoteAmount, quoteCcy)
            ctx.journal.credit(accObsClearingQuote, quoteAmount, quoteCcy)
        }
        
        ctx.accountingPhase = AccountingPhase.MEMO_POSTED
        break

    case EventType.SETTLEMENT:
        // DATA T+2: Manifestazione finanziaria ed accensione contabilità reale
        if (txn.txnSide == TxnSide.BUY) {
            // 1. Storno impegni fuori bilancio (* -1.0)
            ctx.journal.debit(accCommitBase, baseAmount * (-1.0), baseCcy)
            ctx.journal.credit(accObsClearingBase, baseAmount * (-1.0), baseCcy)
            ctx.journal.debit(accObsClearingQuote, quoteAmount * (-1.0), quoteCcy)
            ctx.journal.credit(accCommitQuote, quoteAmount * (-1.0), quoteCcy)

            // 2. Accensione registri reali patrimonio (FVTPL)
            ctx.journal.debit(accPositionControl, baseAmount, baseCcy)
            ctx.journal.credit(accSpotBase, baseAmount, baseCcy)
            ctx.journal.debit(accSpotQuote, quoteAmount, quoteCcy)
            ctx.journal.credit(accPosition, quoteAmount, quoteCcy)
        } else if (txn.txnSide == TxnSide.SELL) {
            // 1. Storno impegni fuori bilancio (* -1.0)
            ctx.journal.debit(accObsClearingBase, baseAmount * (-1.0), baseCcy)
            ctx.journal.credit(accCommitBase, baseAmount * (-1.0), baseCcy)
            ctx.journal.debit(accCommitQuote, quoteAmount * (-1.0), quoteCcy)
            ctx.journal.credit(accObsClearingQuote, quoteAmount * (-1.0), quoteCcy)

            // 2. Accensione registri reali patrimonio (FVTPL)
            ctx.journal.debit(accSpotBase, baseAmount, baseCcy)
            ctx.journal.credit(accPositionControl, baseAmount, baseCcy)
            ctx.journal.debit(accPosition, quoteAmount, quoteCcy)
            ctx.journal.credit(accSpotQuote, quoteAmount, quoteCcy)
        }
        
        ctx.accountingPhase = AccountingPhase.OFFICIAL_POSTED        
        break

    case EventType.MTM:
        double unrealizedAmt = ctx.getUnrealizedPnl() 
        if (unrealizedAmt < 0) {
            String accUnrealizedLoss = accountResolver.resolve("UNREALIZED_FX_LOSS", eurCcy)
            ctx.journal.debit(accUnrealizedLoss, Math.abs(unrealizedAmt), eurCcy)
            ctx.journal.credit(accPosition, Math.abs(unrealizedAmt), quoteCcy)
        } else if (unrealizedAmt > 0) {
            String accUnrealizedGain = accountResolver.resolve("UNREALIZED_FX_GAIN", eurCcy)
            ctx.journal.debit(accPosition, unrealizedAmt, quoteCcy)
            ctx.journal.credit(accUnrealizedGain, unrealizedAmt, eurCcy)
        }
        break

    case EventType.ROLLOVER:
        String accRealizedLoss = accountResolver.resolve("REALIZED_FX_LOSS", quoteCcy)
        double swapCost = ctx.getSwapPointsCost() 
        ctx.journal.debit(accRealizedLoss, swapCost, quoteCcy)
        ctx.journal.credit(accPosition, swapCost, quoteCcy)
        break

    case EventType.TRADE_AMENDED:
    case EventType.TRADE_CANCELED:
        ctx.accountingPhase = txn.txnAcctPhase
        ctx.reverseJournal()
        break
}
