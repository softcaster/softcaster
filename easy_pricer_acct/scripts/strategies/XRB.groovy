import org.softcaster.engine.enums.TxnSide
import org.softcaster.engine.enums.EventType

def txn = ctx.txn
def event = ctx.event

int tradeCcy = txn.masterData.currency?.idCurrency 
int eurCcy   = 1 // ID fisso dell'Euro

// RISOLUZIONE TUTTI I CONTI IN TESTATA
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
        double cleanPrice    = txn.price
        double bondValue     = nominalAmount * cleanPrice // Corso secco
        double accruedBuy    = txn.accruedInterest        // Rateo all'acquisto
        double totalUSD      = bondValue + accruedBuy     // Prezzo Dirty
        double totalEUR      = totalUSD / txn.fxRate      // Regolamento speculare convertito

        if (txn.txnSide == TxnSide.BUY) {
            // Registro in divisa del titolo
            ctx.journal.debit(accBondAsset, bondValue, tradeCcy)
            ctx.journal.debit(accAccruedInt, accruedBuy, tradeCcy)
            ctx.journal.credit(accPosCcy, totalUSD, tradeCcy)
            
            // Registro nazionale di regolamento in EUR
            ctx.journal.debit(accCtrlEUR, totalEUR, eurCcy)
            ctx.journal.credit(accSettlement, totalEUR, eurCcy)
        } 
        else if (txn.txnSide == TxnSide.SELL) {
            ctx.journal.debit(accPosCcy, totalUSD, tradeCcy)
            ctx.journal.credit(accBondAsset, bondValue, tradeCcy)
            ctx.journal.credit(accAccruedInt, accruedBuy, tradeCcy)
            
            ctx.journal.debit(accSettlement, totalEUR, eurCcy)
            ctx.journal.credit(accCtrlEUR, totalEUR, eurCcy)
        }
        break

    case EventType.TRADE_AMENDED:
    case EventType.TRADE_CANCELED:
        ctx.journal.reverseCurrentJournal()
        break

    case EventType.ACCRUAL:
        // Maturazione quotidiana pro-rata temporis del rateo cedola
        double dailyAccrualUSD = ctx.getDailyAccrualAmount() 
        double dailyAccrualEUR = dailyAccrualUSD / ctx.getFxRate() 

        ctx.journal.debit(accAccruedInt, dailyAccrualUSD, tradeCcy)
        ctx.journal.credit(accPosCcy, dailyAccrualUSD, tradeCcy)
        
        ctx.journal.debit(accCtrlEUR, dailyAccrualEUR, eurCcy)
        ctx.journal.credit(accInterestInc, dailyAccrualEUR, eurCcy)
        break

    case EventType.COUPON:
        // Incasso finanziario della cedola periodica
        double couponAmountUSD = ctx.getCouponAmount() 
        double couponAmountEUR = couponAmountUSD / ctx.getFxRate()

        ctx.journal.debit(accCashReal, couponAmountUSD, tradeCcy)
        ctx.journal.credit(accAccruedInt, couponAmountUSD, tradeCcy)
        
        ctx.journal.debit(accPosCcy, couponAmountUSD, tradeCcy)
        ctx.journal.credit(accCtrlEUR, couponAmountEUR, eurCcy)
        break

    case EventType.MATURITY:
        // Rimborso finale a 100 del valore nominale del capitale
        double faceValueUSD = txn.quantity 
        double faceValueEUR = faceValueUSD / ctx.getFxRate()

        ctx.journal.debit(accCashReal, faceValueUSD, tradeCcy)
        ctx.journal.credit(accBondAsset, faceValueUSD, tradeCcy)
        
        ctx.journal.debit(accPosCcy, faceValueUSD, tradeCcy)
        ctx.journal.credit(accCtrlEUR, faceValueEUR, eurCcy)
        break
}
