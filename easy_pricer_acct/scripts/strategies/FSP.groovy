import org.softcaster.engine.enums.TxnSide

def txn = ctx.txn
int baseCcy  = txn.masterData.bcy?.idCurrency
int quoteCcy = txn.masterData.ccy?.idCurrency

double baseAmount  = txn.quantity.round(2)
double quoteAmount = (txn.quantity * txn.price).round(2)

String clearAccount    = "240090"
String positionAccount = "240095"
String feesAccount     = "580010"

if (txn.txnSide == TxnSide.DEBIT) { 
    ctx.journal.debit(clearAccount, baseAmount, baseCcy)
    ctx.journal.credit(positionAccount, baseAmount, baseCcy)
    ctx.journal.debit(positionAccount, baseAmount, quoteCcy)
    ctx.journal.credit(clearAccount, quoteAmount, quoteCcy)
} else if (txn.txnSide == TxnSide.CREDIT) { 
    ctx.journal.debit(positionAccount, baseAmount, baseCcy)
    ctx.journal.credit(clearAccount, baseAmount, baseCcy)
    ctx.journal.debit(clearAccount, quoteAmount, quoteCcy)
    ctx.journal.credit(positionAccount, baseAmount, quoteCcy)
}

// Gestione Fee
if (txn.components) {
    txn.components.each { cmp ->
        double feeAmount = cmp.amount ? cmp.amount.round(2) : 0.0
        if (feeAmount > 0) {
            String feeCcy = cmp.currency?.idCurrency ?: baseCcy
            ctx.journal.debit(feesAccount, feeAmount, feeCcy)
            ctx.journal.credit(clearAccount, feeAmount, feeCcy)
        }
    }
}
