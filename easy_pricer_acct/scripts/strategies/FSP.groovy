import org.softcaster.engine.enums.TxnSide

def txn = ctx.txn
def event = event

int baseCcy  = txn.masterData.bcy?.idCurrency
int quoteCcy = txn.masterData.ccy?.idCurrency

double baseAmount  = txn.quantity
double quoteAmount = txn.quantity * txn.price

switch(event.eventType) {
case TRADE_EXECUTED:
    if (txn.txnSide == TxnSide.BUY) { 
        ctx.journal.debit("240090", baseAmount, baseCcy) // Currency Position Control - EUR
        ctx.journal.credit("130050", baseAmount, baseCcy) // FX Spot Contracts - EUR
        ctx.journal.debit("130055", quoteAmount, quoteCcy)  // FX Spot Contracts - USD
        ctx.journal.credit("120095", quoteAmount, quoteCcy) // Currency Position  - USD
    } else if (txn.txnSide == TxnSide.SELL) { 
        ctx.journal.debit("130050", baseAmount, baseCcy)
        ctx.journal.credit("240090", baseAmount, baseCcy)
        ctx.journal.debit("120095", quoteAmount, quoteCcy)
        ctx.journal.credit("130050", quoteAmount, quoteCcy)
    }
    break;
case TRADE_AMENDED:
    break;
case TRADE_CANCELED:
    break;
default:
    break;
}
