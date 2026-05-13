/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.BondForwardInputData;
import org.softcaster.engine.dto.MarketOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.math.MathUtil;

/**
 *
 * @author ep
 */
public class BondForwardPricer {

    // Torna cedola piu capitalizzazione fino alla scadenza del future
    protected double getSingleCapitalizedCoupon(CashFlow item, LocalDate settlement, double shortRate, LocalDate maturity, double longRate, DaycountBasis daycount, Compounding compounding) {

        // Montante long
        double tenorL = MathUtil.getTimeToMaturity(daycount, settlement, maturity);
        double accumulationFactorL = 1 / MathUtil.getDiscountFactor(compounding, longRate, tenorL);

        // Montante short
        double tenorS = MathUtil.getTimeToMaturity(daycount, settlement, item.accrualEnd());
        double accumulationFactorS = 1 / MathUtil.getDiscountFactor(compounding, shortRate, tenorS);

        // Montante fwd-fwd
        double accumulationFactorF = accumulationFactorL;
        if (!MathUtil.isZero(accumulationFactorS)) {
            accumulationFactorF /= accumulationFactorS;
        }

        // Coupon piu capitalizzazione fino a scadenza future
        return item.interest() * accumulationFactorF;
    }

    protected double getCapitalizedCoupon(LocalDate settlement, double shortRate, LocalDate maturity, double longRate, List<CashFlow> underliyngCashFlows, DaycountBasis daycount, Compounding compounding) {
        double capitalizedCoupon = 0;
        if (underliyngCashFlows == null || underliyngCashFlows.isEmpty()) {
            return capitalizedCoupon;
        }

        LocalDate endDate = null;
        LocalDate startDate = null;
        CashFlow coupon = null;
        List<CashFlow> capitalizedFlows = new ArrayList<>();

        for (CashFlow item : underliyngCashFlows) {
            endDate = item.accrualEnd();
            startDate = item.accrualStart();
            // Se sono andato oltre, esco
            if (startDate.isAfter(maturity)) {
                break;
            }
            if (endDate.isAfter(settlement) && endDate.isBefore(maturity)) {
                coupon = new CashFlow(item.accrualStart(), item.accrualEnd(), item.accrualEnd(), item.interest(), item.principal(), 100.);
                capitalizedFlows.add(coupon);
            }
        }

        for (CashFlow item : capitalizedFlows) {
            capitalizedCoupon += getSingleCapitalizedCoupon(item, settlement, shortRate, maturity, longRate, daycount, compounding);
        }

        // Se almeno una cedola cade nell'intervallo tra settlement e la
        // maturity del future, allora la successiva determina il rateo
        // che andra dalla decorrenza della cedola (corrispondente alla end
        // dell' ultima cedola nella lista)alla maturity del future
        if (capitalizedCoupon > 0) {
            // ottengo ultima cedola
            CashFlow lastCoupon = capitalizedFlows.get(capitalizedFlows.size() - 1);
            if (lastCoupon != null) {
                // Giorni cedola
                long totalDays = ChronoUnit.DAYS.between(lastCoupon.accrualStart(), lastCoupon.accrualEnd());
                // Nota che la scadenza dell'ultima cedola della lista corrisponde
                // alla decorrenza della successiva
                long accrualDays = ChronoUnit.DAYS.between(lastCoupon.accrualEnd(), maturity);
                // trasformo in double moltiplicando per 1.0
                double tenor = accrualDays * 1.0 / totalDays;
                capitalizedCoupon += tenor * lastCoupon.interest();
            }
        }
        return capitalizedCoupon;
    }

    // Rateo d'interesse maturato dal titolo(CTD) alla data di scadenza del Future.
    // E' il caso della cedola che non cade tra settlement e maturity del future.
    // Devo trovare la cedola che "ricopre" l'intervallo tra settlement e maturity
    protected double getAccrual(LocalDate settlement, LocalDate maturity, List<CashFlow> underliyngCashFlows, boolean isSpotAccrual) {
        double accrual = 0;
        if (underliyngCashFlows == null || underliyngCashFlows.isEmpty()) {
            return accrual;
        }
        for (CashFlow item : underliyngCashFlows) {
            // Trovata la cedola che ricopre l' intervallo settlement-maturity
            if (item.accrualStart().isBefore(settlement) && item.accrualEnd().isAfter(maturity)) {
                // Giorni cedola
                long totalDays = ChronoUnit.DAYS.between(item.accrualStart(), item.accrualEnd());
                long accrualDays = 0;
                if (isSpotAccrual) {
                    // rateo spot del sottostante
                    accrualDays = ChronoUnit.DAYS.between(item.accrualStart(), settlement);
                } else {
                    // Nota che la scadenza dell'ultima cedola della lista corrisponde
                    // alla decorrenza della successiva
                    accrualDays = ChronoUnit.DAYS.between(item.accrualStart(), maturity);

                }
                // trasformo in double moltiplicando per 1.0
                double tenor = accrualDays * 1.0 / totalDays;
                accrual = tenor * item.interest();
                // esco dal ciclo
                break;
            }
        }
        return accrual;
    }

    protected double theoreticalPrice(LocalDate settlement, double shortRate, LocalDate maturity, double longRate,
            List<CashFlow> underliyngCashFlows,
            double underlyingPrice, DaycountBasis daycount, Compounding compounding) {
        double thPrice = 0;

        double capitalizedCoupon = getCapitalizedCoupon(settlement, shortRate, maturity, longRate, underliyngCashFlows, daycount, compounding);

        // Se nessuna cedola cade tra settlement e maturity del future
        // calcolo rateo tra star della cedola e maturity del future
        double accrual = 0;
        if (MathUtil.isZero(capitalizedCoupon)) {
            accrual = getAccrual(settlement, maturity, underliyngCashFlows, false);
        }

        double tenor = daycount.calculate(settlement, maturity, null);
        double accumulationFactor = 1 / MathUtil.getDiscountFactor(compounding, longRate, tenor);
        // accrual sottostante
        double spotAccrual = getAccrual(settlement, maturity, underliyngCashFlows, true);
        double dirtyPrice = underlyingPrice + spotAccrual;
        thPrice = (dirtyPrice * accumulationFactor - (capitalizedCoupon + accrual));

        return thPrice;
    }

    public MarketOutputData calculateForwardPrice(BondForwardInputData input) {

        MarketOutputData output = new MarketOutputData();
        LocalDate settlement = input.getValuationDate();
        double shortRate = input.getDomesticRate();
        LocalDate maturity = input.getMaturityDate();
        double longRate = input.getForeignRate();
        List<CashFlow> underliyngCashFlows = input.getUnderliyngCashFlows();
        double underlyingPrice = input.getSpotPrice();
        DaycountBasis daycount = input.getDaycount();
        Compounding compounding = input.getCompounding();

        double forwardPrice = theoreticalPrice(settlement, shortRate, maturity, longRate, underliyngCashFlows, underlyingPrice, daycount, compounding);
        output.setPrice(forwardPrice);

        return output;
    }

    /**
     * Calcola il rateo di cedola esatto di un BTP per una determinata data target
     * @param cashFlows
     * @param targetDate
     * @return 
     */
    public double calculateAccrualAtDate(List<CashFlow> cashFlows, LocalDate targetDate) {
        for (CashFlow cf : cashFlows) {
            // Trova la cedola che contiene la data target
            if (!targetDate.isBefore(cf.accrualStart()) && !targetDate.isAfter(cf.accrualEnd())) {
                long totalDays = ChronoUnit.DAYS.between(cf.accrualStart(), cf.accrualEnd());
                long accruedDays = ChronoUnit.DAYS.between(cf.accrualStart(), targetDate);
                return (double) accruedDays / totalDays * cf.interest();
            }
        }
        return 0.0;
    }

    public double getCapitalizedIntermediateCoupons(List<CashFlow> cashFlows, LocalDate settlement, double shortRate, LocalDate maturity, double longRate, DaycountBasis daycount, Compounding compounding) {
        double totalCapitalizedCoupons = 0.0;

        // Il DF alla maturity del future ci serve come base per capitalizzare i flussi intermedi
        //int daysToMaturity = (int) ChronoUnit.DAYS.between(settlement, maturity);
        //double dfMaturity = curveManager.getDiscountFactor(daysToMaturity);
        // Montante long
        double tenorL = MathUtil.getTimeToMaturity(daycount, settlement, maturity);
        //double accumulationFactorL = 1 / MathUtil.getDiscountFactor(compounding, longRate, tenorL);
        double dfMaturity = MathUtil.getDiscountFactor(compounding, longRate, tenorL);

        for (CashFlow cf : cashFlows) {
            LocalDate paymentDate = cf.accrualEnd(); // Data di stacco/pagamento fisica

            // Se la cedola viene pagata DOPO oggi ed ENTRO la scadenza del future
            if (paymentDate.isAfter(settlement) && !paymentDate.isAfter(maturity)) {
                //int daysToCoupon = (int) ChronoUnit.DAYS.between(settlement, paymentDate);
                //double dfCoupon = curveManager.getDiscountFactor(daysToCoupon);
                double tenorS = MathUtil.getTimeToMaturity(daycount, settlement, cf.accrualEnd());
                double dfCoupon = MathUtil.getDiscountFactor(compounding, shortRate, tenorS);

                // Capitalizzazione continua della cedola dalla data di stacco alla maturity del future
                // Finanziariamente equivale a: Cedola * (dfCoupon / dfMaturity)
                double intermediateCarry = cf.interest() * (dfCoupon / dfMaturity);
                totalCapitalizedCoupons += intermediateCarry;
            }
        }
        return totalCapitalizedCoupons;
    }

/**
     * Calcola il prezzo teorico esatto del BTP Mini-Future 10y
     * @param settlement
     * @param maturity
     * @param cashFlows
     * @param spotCleanPrice
     * @param conversionFactor
     * @return 
     */
    public double calculateTheoreticalFuturePrice(
            LocalDate settlement, 
            LocalDate maturity, 
            List<CashFlow> cashFlows, 
            double spotCleanPrice, 
            double conversionFactor) {

        int daysToMaturity = (int) ChronoUnit.DAYS.between(settlement, maturity);
        double dfMaturity = curveManager.getDiscountFactor(daysToMaturity);

        // 1. Calcolo dei Ratei (Spot e Delivery) mappando correttamente i flussi
        double spotAccrual = calculateAccrualAtDate(cashFlows, settlement);
        double deliveryAccrual = calculateAccrualAtDate(cashFlows, maturity);

        // 2. Prezzo Dirty Spot (Prezzo Tel Quel iniziale)
        double dirtyPriceSpot = spotCleanPrice + spotAccrual;

        // 3. Capitalizzazione continua del prezzo Dirty fino alla maturity del future
        double dirtyPriceForward = dirtyPriceSpot / dfMaturity;

        // 4. Calcolo delle cedole intermedie fisiche capitalizzate alla scadenza
        double capitalizedCoupons = getCapitalizedIntermediateCoupons(cashFlows, settlement, maturity);

        // 5. Formula istituzionale del Cost of Carry (Forward Clean Price)
        double forwardCleanPrice = dirtyPriceForward - capitalizedCoupons - deliveryAccrual;

        // 6. Normalizzazione finale per il mercato dei Future (Divisione per il CF)
        return forwardCleanPrice / conversionFactor;
    }    
}

/*
1) double dfSetToMaturity = curve.df(settlement, maturity);
double dfSetToCouponEnd = curve.df(settlement, couponEnd);
double forwardFactor = dfSetToMaturity / dfSetToCouponEnd;

2)double dfSetToMat = curve.df(settlement, maturity);

double pvCoupons = 0.0;

for (CashFlow cf : cashFlows) {
    if (cf.accrualEnd().isAfter(settlement) && cf.accrualEnd().isBefore(maturity)) {
        double df = curve.df(settlement, cf.accrualEnd());
        pvCoupons += cf.interest() * df;
    }
}

double dirtySpot = underlyingPrice + accrued(settlement);

double forwardPrice = (dirtySpot - pvCoupons) / curve.df(settlement, maturity);

 */
