/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.XRBForwardInputData;
import org.softcaster.engine.dto.XRBForwardOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

/**
 *
 * @author ep
 */
public class BondForwardPricer {

    public XRBForwardOutputData calculateForwardPrice(XRBForwardInputData input) {

        XRBForwardOutputData output = new XRBForwardOutputData();
        LocalDate settlement = input.getValuationDate();
        double shortRate = input.getDomesticRate();
        LocalDate maturity = input.getMaturityDate();
        double longRate = input.getForeignRate();
        List<CashFlow> underliyngCashFlows = input.getUnderliyngCashFlows();
        DaycountBasis daycount = input.getDaycount();
        Frequency frequency = input.getFrequency();
        Compounding compounding = input.getCompounding();
        double cleanSpotPrice = input.getUnderlyingReferencePrice();
        double cf = input.getConversionFactor();

        double forwardPrice = calculateTheoreticalFuturePrice(
            underliyngCashFlows,
            settlement,
            shortRate,
            maturity,
            longRate,
            daycount,
            compounding,
            frequency,
            cleanSpotPrice,
            cf);                

        output.setPrice(forwardPrice);
        return output;
    }

    /**
     * Calcola il rateo di cedola esatto di un BTP per una determinata data
     * target
     *
     * @param cashFlows
     * @param targetDate
     * @param daycount
     * @param frequency
     * @return
     */
    public double calculateAccrualAtDate(List<CashFlow> cashFlows, LocalDate targetDate, DaycountBasis daycount, Frequency frequency) {
        for (CashFlow cf : cashFlows) {

            // Trova la cedola che contiene la data target
            if (!targetDate.isBefore(cf.accrualStart()) && !targetDate.isAfter(cf.accrualEnd())) {
                double totalTenor = MathUtil.getTimeToMaturity(daycount, frequency, cf.accrualStart(), cf.accrualEnd());
                double accruedTenor = MathUtil.getTimeToMaturity(daycount, frequency, cf.accrualStart(), targetDate);
                return (accruedTenor / totalTenor) * cf.interest();
            }
        }
        return 0.0;
    }

    public double getCapitalizedIntermediateCoupons(List<CashFlow> cashFlows, LocalDate settlement, double shortRate, LocalDate maturity, double longRate, DaycountBasis daycount, Compounding compounding, Frequency frequency) {
        double totalCapitalizedCoupons = 0.0;

        // Il DF alla maturity del future ci serve come base per capitalizzare i flussi intermedi
        //int daysToMaturity = (int) ChronoUnit.DAYS.between(settlement, maturity);
        //double dfMaturity = curveManager.getDiscountFactor(daysToMaturity);
        // Montante long
        double tenorL = MathUtil.getTimeToMaturity(daycount, frequency, settlement, maturity);
        //double accumulationFactorL = 1 / MathUtil.getDiscountFactor(compounding, longRate, tenorL);
        double dfMaturity = MathUtil.getDiscountFactor(compounding, longRate, tenorL);

        for (CashFlow cf : cashFlows) {
            LocalDate paymentDate = cf.accrualEnd(); // Data di stacco/pagamento fisica

            // Se la cedola viene pagata DOPO oggi ed ENTRO la scadenza del future
            if (paymentDate.isAfter(settlement) && !paymentDate.isAfter(maturity)) {
                //int daysToCoupon = (int) ChronoUnit.DAYS.between(settlement, paymentDate);
                //double dfCoupon = curveManager.getDiscountFactor(daysToCoupon);
                double tenorS = MathUtil.getTimeToMaturity(daycount, frequency, settlement, cf.accrualEnd());
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
     *
     * @param settlement
     * @param maturity
     * @param shortRate
     * @param cashFlows
     * @param longRate
     * @param daycount
     * @param compounding
     * @param frequency
     * @param spotCleanPrice
     * @param conversionFactor
     * @return
     */
    public double calculateTheoreticalFuturePrice(
            List<CashFlow> cashFlows,
            LocalDate settlement,
            double shortRate,
            LocalDate maturity,
            double longRate,
            DaycountBasis daycount,
            Compounding compounding,
            Frequency frequency,
            double spotCleanPrice,
            double conversionFactor) {

        //int daysToMaturity = (int) ChronoUnit.DAYS.between(settlement, maturity);
        //double dfMaturity = curveManager.getDiscountFactor(daysToMaturity);
        double tenorL = MathUtil.getTimeToMaturity(daycount, frequency, settlement, maturity);
        double dfMaturity = MathUtil.getDiscountFactor(compounding, longRate, tenorL);

        // 1. Calcolo dei Ratei (Spot e Delivery) mappando correttamente i flussi
        double spotAccrual = calculateAccrualAtDate(cashFlows, settlement, daycount, frequency);
        double deliveryAccrual = calculateAccrualAtDate(cashFlows, maturity, daycount, frequency);

        // 2. Prezzo Dirty Spot (Prezzo Tel Quel iniziale)
        double dirtyPriceSpot = spotCleanPrice + spotAccrual;

        // 3. Capitalizzazione continua del prezzo Dirty fino alla maturity del future
        double dirtyPriceForward = dirtyPriceSpot / dfMaturity;

        // 4. Calcolo delle cedole intermedie fisiche capitalizzate alla scadenza
        double capitalizedCoupons = getCapitalizedIntermediateCoupons(cashFlows, settlement, shortRate, maturity, longRate, daycount, compounding, frequency);

        // 5. Formula istituzionale del Cost of Carry (Forward Clean Price)
        double forwardCleanPrice = dirtyPriceForward - capitalizedCoupons - deliveryAccrual;

        // 6. Normalizzazione finale per il mercato dei Future (Divisione per il CF)
        return forwardCleanPrice / conversionFactor;
    }
}

