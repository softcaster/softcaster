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
    String accBondAsset    = accountResolver.resolve("BOND_ASSET", eurCcy)
    String accAccruedInt   = accountResolver.resolve("ACCRUED_INTEREST", eurCcy)
    String accInterestInc  = accountResolver.resolve("INTEREST_INCOME", eurCcy)
    String accCashReal     = accountResolver.resolve("CASH_ACCOUNT", eurCcy)
    
    String accCommitCcy    = accountResolver.resolve("BOND_COMMITMENT", eurCcy) 
    String accObsClearing  = accountResolver.resolve("OBS_CLEARING", eurCcy)

    switch(event.eventType) {
        case EventType.TRADE_EXECUTED:
            // DATA T: Registrazione dell'impegno esclusivamente fuori bilancio
            double totalAmt = (txn.quantity * txn.price * (txn.masterData.multiplier ?: 1.0)) + ctx.bondAccruedInterest
            if (txn.txnSide == TxnSide.BUY) {
                ctx.journal.debit(accCommitCcy, totalAmt, eurCcy)
                ctx.journal.credit(accObsClearing, totalAmt, eurCcy)
            } else if (txn.txnSide == TxnSide.SELL) {
                ctx.journal.debit(accObsClearing, totalAmt, eurCcy)
                ctx.journal.credit(accCommitCcy, totalAmt, eurCcy)
            }
            break

        case EventType.SETTLEMENT:
            // DATA T+2: Manifestazione finanziaria reale
            double bondValue   = txn.quantity * txn.price * (txn.masterData.multiplier ?: 1.0)
            double accruedBuy  = ctx.bondAccruedInterest
            double totalAmt    = bondValue + accruedBuy

            if (txn.txnSide == TxnSide.BUY) {
                // 1. Storno in nero dei conti memorandum
                ctx.journal.debit(accCommitCcy, totalAmt * (-1.0), eurCcy)
                ctx.journal.credit(accObsClearing, totalAmt * (-1.0), eurCcy)
                // 2. Accensione conti reali
                ctx.journal.debit(accBondAsset, bondValue, eurCcy)
                ctx.journal.debit(accAccruedInt, accruedBuy, eurCcy)
                ctx.journal.credit(accCashReal, totalAmt, eurCcy)
            } else if (txn.txnSide == TxnSide.SELL) {
                ctx.journal.debit(accObsClearing, totalAmt * (-1.0), eurCcy)
                ctx.journal.credit(accCommitCcy, totalAmt * (-1.0), eurCcy)
                ctx.journal.debit(accCashReal, totalAmt, eurCcy)
                ctx.journal.credit(accBondAsset, bondValue, eurCcy)
                ctx.journal.credit(accAccruedInt, accruedBuy, eurCcy)
            }
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

        case EventType.TRADE_AMENDED:
        case EventType.TRADE_CANCELED:
            ctx.reverseJournal()
            break
    }
    return 
}

// =========================================================================
// STRATEGIA 2: TITOLO IN VALUTA ESTERA (USD, CHF, CAD, GBP, ecc.)
// =========================================================================
String accBondAsset    = accountResolver.resolve("BOND_ASSET", tradeCcy)
String accAccruedInt   = accountResolver.resolve("ACCRUED_INTEREST", tradeCcy)
String accInterestInc  = accountResolver.resolve("INTEREST_INCOME", eurCcy)
String accCashReal     = accountResolver.resolve("CASH_ACCOUNT", eurCcy) // Liquidazione in EUR al broker
String accPosCcy       = accountResolver.resolve("CURRENCY_POSITION", tradeCcy) 
String accCtrlEUR      = accountResolver.resolve("POSITION_CONTROL", eurCcy)    

String accCommitCcy    = accountResolver.resolve("BOND_COMMITMENT", tradeCcy)
String accObsClearing  = accountResolver.resolve("OBS_CLEARING", tradeCcy)
String accObsClearingEUR = accountResolver.resolve("OBS_CLEARING", eurCcy)
String accCommitEUR    = accountResolver.resolve("BOND_COMMITMENT", eurCcy)

switch(event.eventType) {

    case EventType.TRADE_EXECUTED:
        double bondValueCcy  = txn.quantity * txn.price * (txn.masterData.multiplier ?: 1.0)
        double accruedBuyCcy = ctx.bondAccruedInterest    
        double totalCcy      = bondValueCcy + accruedBuyCcy
        double totalEUR      = totalCcy / txn.fxRate      

        if (txn.txnSide == TxnSide.BUY) {
            ctx.journal.debit(accCommitCcy, totalCcy, tradeCcy)
            ctx.journal.credit(accObsClearing, totalCcy, tradeCcy)
            ctx.journal.debit(accObsClearingEUR, totalEUR, eurCcy)
            ctx.journal.credit(accCommitEUR, totalEUR, eurCcy)
        } else if (txn.txnSide == TxnSide.SELL) {
            ctx.journal.debit(accObsClearing, totalCcy, tradeCcy)
            ctx.journal.credit(accCommitCcy, totalCcy, tradeCcy)
            ctx.journal.debit(accCommitEUR, totalEUR, eurCcy)
            ctx.journal.credit(accObsClearingEUR, totalEUR, eurCcy)
        }
        break

    case EventType.SETTLEMENT:
        double bondValueCcy  = txn.quantity * txn.price * (txn.masterData.multiplier ?: 1.0)
        double accruedBuyCcy = ctx.bondAccruedInterest    
        double totalCcy      = bondValueCcy + accruedBuyCcy
        double totalEUR      = totalCcy / txn.fxRate      

        if (txn.txnSide == TxnSide.BUY) {
            // 1. Storno impegni fuori bilancio
            ctx.journal.debit(accCommitCcy, totalCcy * (-1.0), tradeCcy)
            ctx.journal.credit(accObsClearing, totalCcy * (-1.0), tradeCcy)
            ctx.journal.debit(accObsClearingEUR, totalEUR * (-1.0), eurCcy)
            ctx.journal.credit(accCommitEUR, totalEUR * (-1.0), eurCcy)

            // 2. Accensione contabilità reale patrimoniale
            ctx.journal.debit(accBondAsset, bondValueCcy, tradeCcy)
            ctx.journal.debit(accAccruedInt, accruedBuyCcy, tradeCcy)
            ctx.journal.credit(accPosCcy, totalCcy, tradeCcy)
            
            ctx.journal.debit(accCtrlEUR, totalEUR, eurCcy)
            ctx.journal.credit(accCashReal, totalEUR, eurCcy)
        } else if (txn.txnSide == TxnSide.SELL) {
            ctx.journal.debit(accObsClearing, totalCcy * (-1.0), tradeCcy)
            ctx.journal.credit(accCommitCcy, totalCcy * (-1.0), tradeCcy)
            ctx.journal.debit(accCommitEUR, totalEUR * (-1.0), eurCcy)
            ctx.journal.credit(accObsClearingEUR, totalEUR * (-1.0), eurCcy)

            ctx.journal.debit(accPosCcy, totalCcy, tradeCcy)
            ctx.journal.credit(accBondAsset, bondValueCcy, tradeCcy)
            ctx.journal.credit(accAccruedInt, accruedBuyCcy, tradeCcy)
            
            ctx.journal.debit(accCashReal, totalEUR, eurCcy)
            ctx.journal.credit(accCtrlEUR, totalEUR, eurCcy)
        }
        break

    case EventType.ACCRUAL:
        double dailyAccrualCcy = ctx.getDailyAccrualAmount() 
        double dailyAccrualEUR = dailyAccrualCcy / ctx.getFxRate() 

        ctx.journal.debit(accAccruedInt, dailyAccrualCcy, tradeCcy)
        ctx.journal.credit(accPosCcy, dailyAccrualCcy, tradeCcy)
            
        ctx.journal.debit(accCtrlEUR, dailyAccrualEUR, eurCcy)
        ctx.journal.credit(accInterestInc, dailyAccrualEUR, eurCcy)
        break

    case EventType.COUPON:
        double couponAmountCcy = ctx.getCouponAmount() 
        double couponAmountEUR = couponAmountCcy / ctx.getFxRate()
        String accCashCcy       = accountResolver.resolve("CASH_ACCOUNT", tradeCcy)

        ctx.journal.debit(accCashCcy, couponAmountCcy, tradeCcy)
        ctx.journal.credit(accAccruedInt, couponAmountCcy, tradeCcy)
            
        ctx.journal.debit(accPosCcy, couponAmountCcy, tradeCcy)
        ctx.journal.credit(accCtrlEUR, couponAmountEUR, eurCcy)
        break

    case EventType.MATURITY:
        double faceValueCcy = txn.quantity 
        double faceValueEUR = faceValueCcy / ctx.getFxRate()
        String accCashCcy     = accountResolver.resolve("CASH_ACCOUNT", tradeCcy)

        ctx.journal.debit(accCashCcy, faceValueCcy, tradeCcy)
        ctx.journal.credit(accBondAsset, faceValueCcy, tradeCcy)
            
        ctx.journal.debit(accPosCcy, faceValueCcy, tradeCcy)
        ctx.journal.credit(accCtrlEUR, faceValueEUR, eurCcy)
        break

    case EventType.TRADE_AMENDED:
    case EventType.TRADE_CANCELED:
        ctx.reverseJournal()
        break
}
