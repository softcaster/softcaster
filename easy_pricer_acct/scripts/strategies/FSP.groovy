import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType 

def txn = ctx.txn
def event = ctx.event

int baseCcy  = txn.masterData.bcy?.idCurrency
int quoteCcy = txn.masterData.ccy?.idCurrency

double baseAmount  = txn.quantity
double quoteAmount = txn.quantity * txn.price

// I conti generici o legati alla valuta base usano l'ID esplicito dell'Euro (1)
String accPositionControl = accountResolver.resolve("POSITION_CONTROL", 1) // Restituisce il conto legato a EUR (ID 1)
String accSpotBase         = accountResolver.resolve("FX_SPOT_ASSET", baseCcy)     
String accSpotQuote        = accountResolver.resolve("FX_SPOT_ASSET", quoteCcy)    
String accPosition         = accountResolver.resolve("CURRENCY_POSITION", quoteCcy) 

switch(event.eventType) {
case EventType.TRADE_EXECUTED:
    if (txn.txnSide == TxnSide.BUY) { 
        // Compri EUR vendendo USD
        ctx.journal.debit(accPositionControl, baseAmount, baseCcy)
        ctx.journal.credit(accSpotBase, baseAmount, baseCcy)
        
        ctx.journal.debit(accSpotQuote, quoteAmount, quoteCcy)
        ctx.journal.credit(accPosition, quoteAmount, quoteCcy)
    } else if (txn.txnSide == TxnSide.SELL) { 
        // Vendi EUR comprando USD
        ctx.journal.debit(accSpotBase, baseAmount, baseCcy)
        ctx.journal.credit(accPositionControl, baseAmount, baseCcy)
        
        ctx.journal.debit(accPosition, quoteAmount, quoteCcy)
        ctx.journal.credit(accSpotQuote, quoteAmount, quoteCcy)
    }
    break;

case EventType.TRADE_AMENDED:
    // Storno totale del movimento precedente e predisposizione per il ricalcolo
    ctx.journal.reverseCurrentJournal()
    break;

case EventType.TRADE_CANCELED:
    // Cancellazione dell'operazione: viene generata la scrittura di storno speculare
    ctx.journal.reverseCurrentJournal()
    break;

default:
    break;
}
