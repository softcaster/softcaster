/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.CashFlowHelper;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.CashFlowInput;
import org.softcaster.engine.dto.CashFlowResult;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

public abstract class AbstractFixedIncomePricer<I extends CashFlowInput, O extends CashFlowResult>
        implements Pricer<I, O> {

    /**
     * Valore di un variabile nel modello piatto del discount margin: cedole
     * future al tasso corrente + spread (la prima, gia' fissata, resta quella del
     * piano), sul capitale residuo del periodo; capitale rimborsato come da piano
     * (100 alla scadenza per un bullet, quote per un ammortizzato); sconto al
     * tasso corrente + discount margin.
     */
    protected double pvFlatModel(List<CashFlow> flows, LocalDate valuationDate, DaycountBasis dcb,
            Compounding compounding, Frequency frequency,
            double currentRate, double spread, double dm,
            double euriborShift, double dmShift) {

        List<CashFlow> future = CashFlowHelper.futureFlows(flows, valuationDate);
        double periodsPerYear = frequency.getYearFraction();   // numero di cedole l'anno

        double euribor = currentRate + euriborShift;
        double discountRate = euribor + dm + dmShift;

        // capitale residuo per 100 di nominale: somma dei rimborsi futuri
        double outstanding = future.stream().mapToDouble(CashFlow::principal).sum();
        double pv = 0.0;
        for (int i = 0; i < future.size(); i++) {
            CashFlow cf = future.get(i);
            double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);

            // la prima cedola futura e' quella in corso, gia' fissata: non si muove con lo shock dell'Euribor
            double coupon = (i == 0) ? cf.interest() : outstanding * ((euribor + spread) / periodsPerYear);

            pv += (coupon + cf.principal()) * MathUtil.getDiscountFactor(compounding, discountRate, t);
            outstanding -= cf.principal();
        }
        return pv;
    }

    // yield, prezzo, duration e convexity sui flussi: CashFlowHelper (un'unica implementazione)

    public double solveDiscountMargin(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double currentRate, // Euribor 6M corrente
            double spread) {      // spread contrattuale del titolo

        MathUtil.Function1 dmFunction = new MathUtil.Function1() {
            @Override
            public double f(double dm) {
                return f(dm, compounding);
            }

            @Override
            public double f(double dm, Compounding c) {
                return pvFlatModel(cashflows, valuationDate, dcb, c, frequency,
                        currentRate, spread, dm, 0.0, 0.0) - dirtyPrice;
            }
        };
        return MathUtil.rootNewton(dmFunction, 0.005, compounding);
    }

    public double calculateZSpread(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate,
            DiscountCurve curve) {
        return CashFlowHelper.solveZSpread(cashflows,
                dirtyPrice,
                valuationDate,
                curve);

    }

    public double calculateYtm(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        return CashFlowHelper.calculateYtm(flows, cleanPrice, valuationDate, dcb, compounding, frequency);
    }

    public double calculateAccruedInterest(List<CashFlow> flows, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        return CashFlowHelper.calculateAccruedInterest(flows, valuationDate, dcb, freq);
    }

    public double calculateMacaulayDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency freq) {
        return CashFlowHelper.calculateMacaulayDuration(flows, ytm, valuationDate, dcb, compounding, freq);
    }

    public double calculateModifiedDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency freq) {
        return CashFlowHelper.calculateModifiedDuration(flows, ytm, valuationDate, dcb, compounding, freq);
    }

    public double calculateConvexity(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency freq) {
        return CashFlowHelper.calculateConvexity(flows, ytm, valuationDate, dcb, compounding, freq);
    }

    public double calculatePrice(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        return CashFlowHelper.calculatePrice(flows, ytm, valuationDate, dcb, compounding, frequency);
    }

    public double calculatePrice(List<CashFlow> flows, DiscountCurve curve, LocalDate valuationDate, DaycountBasis dcb, Frequency frequency) {
        return CashFlowHelper.calculatePrice(flows, curve, valuationDate, dcb, frequency);
    }
}
