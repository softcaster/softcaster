import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType

def txn = ctx.txn
def event = ctx.event

int tradeCcy = txn.masterData.currency?.idCurrency 
int eurCcy   = 1 // ID fisso dell'Euro

// =========================================================================
// STRATEGIA 1: TITOLO IN EURO (Semplificazione Nativa Monovaluta)
// =========================================================================
if (tradeCcy == eurCcy) {
    String accBondAsset   = accountResolver.resolve("BOND_ASSET", eurCcy)
    String accAccruedInt  = accountResolver.resolve("ACCRUED_INTEREST", eurCcy)
    String accSettlement  = accountResolver.resolve("SETTLEMENT_LIAB", eurCcy)
    String accInterestInc = accountResolver.resolve("INTEREST_INCOME", eurCcy)
    String accCashReal    = accountResolver.resolve("CASH_ACCOUNT", eurCcy)
    double cleanPrice = txn.price * txn.masterData.multiplier;
    
    switch(event.eventType) {
    case EventType.TRADE_EXECUTED:
        double totalAmt = (txn.quantity * cleanPrice) + ctx.bondAccruedInterest
        if (txn.txnSide == TxnSide.BUY) {
            ctx.journal.debit(accBondAsset, txn.quantity *cleanPrice, eurCcy)
            ctx.journal.debit(accAccruedInt, ctx.bondAccruedInterest, eurCcy)
            ctx.journal.credit(accSettlement, totalAmt, eurCcy)
        } else if (txn.txnSide == TxnSide.SELL) {
            ctx.journal.debit(accSettlement, totalAmt, eurCcy)
            ctx.journal.credit(accBondAsset, cleanPrice, eurCcy)
            ctx.journal.credit(accAccruedInt, ctx.bondAccruedInterest, eurCcy)
        }
        break
    case EventType.TRADE_AMENDED:
    case EventType.TRADE_CANCELED:
        ctx.reverseJournal()
        break
    case EventType.ACCRUAL:
        double dailyAccrual = ctx.getDailyAccrualAmount()
        ctx.journal.debit(accAccruedInt, dailyAccrual, eurCcy)
        ctx.journal.credit(accInterestInc, dailyAccrual, eurCcy)
        break
    case EventType.COUPON:
        double couponAmount = ctx.getCouponAmount()
        ctx.journal.debit(accCashReal, couponAmount, eurCcy)
        ctx.journal.credit(accAccruedInt, couponAmount, eurCcy)
        break
    case EventType.MATURITY:
        ctx.journal.debit(accCashReal, txn.quantity, eurCcy)
        ctx.journal.credit(accBondAsset, txn.quantity, eurCcy)
        break
    }
    return // Fine esecuzione per l'Euro. Evita di scendere nella logica multicurrency.
}

// =========================================================================
// STRATEGIA 2: TITOLO IN VALUTA ESTERA (USD, CHF, CAD, GBP, ecc.)
// =========================================================================
String accBondAsset    = accountResolver.resolve("BOND_ASSET", tradeCcy)
String accAccruedInt   = accountResolver.resolve("ACCRUED_INTEREST", tradeCcy)
String accSettlement   = accountResolver.resolve("SETTLEMENT_LIAB", eurCcy)
String accInterestInc  = accountResolver.resolve("INTEREST_INCOME", eurCcy)
String accCashReal     = accountResolver.resolve("CASH_ACCOUNT", tradeCcy)
String accPosCcy       = accountResolver.resolve("CURRENCY_POSITION", tradeCcy) 
String accCtrlEUR      = accountResolver.resolve("POSITION_CONTROL", eurCcy)    

switch(event.eventType) {

case EventType.TRADE_EXECUTED:
    double nominalAmount = txn.quantity
    double cleanPrice    = txn.price * txn.masterData.multiplier;
    double bondValue     = nominalAmount * cleanPrice 
    double accruedBuy    = ctx.bondAccruedInterest    
    double totalCcy      = bondValue + accruedBuy     // Rinominato (Generico Valuta)
    double totalEUR      = totalCcy / txn.fxRate      

    if (txn.txnSide == TxnSide.BUY) {
        ctx.journal.debit(accBondAsset, bondValue, tradeCcy)
        ctx.journal.debit(accAccruedInt, accruedBuy, tradeCcy)
        ctx.journal.credit(accPosCcy, totalCcy, tradeCcy)
            
        ctx.journal.debit(accCtrlEUR, totalEUR, eurCcy)
        ctx.journal.credit(accSettlement, totalEUR, eurCcy)
    } 
    else if (txn.txnSide == TxnSide.SELL) {
        ctx.journal.debit(accPosCcy, totalCcy, tradeCcy)
        ctx.journal.credit(accBondAsset, bondValue, tradeCcy)
        ctx.journal.credit(accAccruedInt, accruedBuy, tradeCcy)
            
        ctx.journal.debit(accSettlement, totalEUR, eurCcy)
        ctx.journal.credit(accCtrlEUR, totalEUR, eurCcy)
    }
    break

case EventType.TRADE_AMENDED:
case EventType.TRADE_CANCELED:
    ctx.reverseJournal()
    break

case EventType.ACCRUAL:
    double dailyAccrualCcy = ctx.getDailyAccrualAmount() // Rinominato
    double dailyAccrualEUR = dailyAccrualCcy / ctx.getFxRate() 

    ctx.journal.debit(accAccruedInt, dailyAccrualCcy, tradeCcy)
    ctx.journal.credit(accPosCcy, dailyAccrualCcy, tradeCcy)
            
    ctx.journal.debit(accCtrlEUR, dailyAccrualEUR, eurCcy)
    ctx.journal.credit(accInterestInc, dailyAccrualEUR, eurCcy)
    break

case EventType.COUPON:
    double couponAmountCcy = ctx.getCouponAmount() // Rinominato
    double couponAmountEUR = couponAmountCcy / ctx.getFxRate()

    ctx.journal.debit(accCashReal, couponAmountCcy, tradeCcy)
    ctx.journal.credit(accAccruedInt, couponAmountCcy, tradeCcy)
            
    ctx.journal.debit(accPosCcy, couponAmountCcy, tradeCcy)
    ctx.journal.credit(accCtrlEUR, couponAmountEUR, eurCcy)
    break

case EventType.MATURITY:
    double faceValueCcy = txn.quantity // Rinominato
    double faceValueEUR = faceValueCcy / ctx.getFxRate()

    ctx.journal.debit(accCashReal, faceValueCcy, tradeCcy)
    ctx.journal.credit(accBondAsset, faceValueCcy, tradeCcy)
            
    ctx.journal.debit(accPosCcy, faceValueCcy, tradeCcy)
    ctx.journal.credit(accCtrlEUR, faceValueEUR, eurCcy)
    break
}
