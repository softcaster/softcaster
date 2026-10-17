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

    protected double pvFlatModel(List<CashFlow> flows, LocalDate valuationDate, DaycountBasis dcb,
            Compounding compounding, Frequency frequency,
            double currentRate, double spread, double dm,
            double euriborShift, double dmShift) {

        List<CashFlow> future = CashFlowHelper.futureFlows(flows, valuationDate);
        double freqValue = frequency.getYearFraction();          // check: coupon = 100 * rate / freqValue, as in your code

        double euribor = currentRate + euriborShift;
        double discountRate = euribor + dm + dmShift;

        double pv = 0.0;
        for (int i = 0; i < future.size(); i++) {
            CashFlow cf = future.get(i);
            double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);

            // the first future coupon is the current one, already fixed: it does not move with the Euribor shock
            double amount = (i == 0) ? cf.interest() : 100.0 * ((euribor + spread) / freqValue);

            if (i == future.size() - 1) {
                amount += 100.0;                                  // redemption (remove if the list already has a principal flow)
            }
            pv += amount * MathUtil.getDiscountFactor(compounding, discountRate, t);
        }
        return pv;
    }

    // Metodo universale per calcolare il Valore Attuale (NPV)
    protected double calculateNpv(List<CashFlow> flows, double rate,
            LocalDate valuationDate, DaycountBasis dcb) {
        double npv = 0.0;
        for (CashFlow cf : flows) {
            double t = dcb.calculate(valuationDate, cf.paymentDate(), null);
            npv += cf.getTotalAmount() / Math.pow(1 + rate, t);
        }
        return npv;
    }

    // Il solutore IRR generico che useranno sia Bond (per YTM) che Loan (per TAEG)
    protected double solveInternalRateOfReturn(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate, // 
            DaycountBasis dcb, // Necessario per calcolare i tempi corretti
            Compounding compounding,
            Frequency frequency
    ) {

        MathUtil.Function1 nlpFunction = new MathUtil.Function1() {

            @Override
            public double f(double rate) {
                // Possiamo delegare al metodo con compounding usando uno di default
                return f(rate, compounding);
            }

            @Override
            public double f(double rate, Compounding compounding) {
                double pv = 0.0;
                for (CashFlow cf : cashflows) {
                    double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);
                    pv += cf.getTotalAmount() * MathUtil.getDiscountFactor(compounding, rate, t);
                }
                return pv - dirtyPrice;
            }
        };

        return MathUtil.rootNewton(nlpFunction, 0.10, compounding);
    }

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
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            DiscountCurve curve) {
        return CashFlowHelper.solveZSpread(cashflows,
                dirtyPrice,
                valuationDate,
                dcb,
                compounding,
                frequency,
                curve);

    }

    public double calculateYtm(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        return CashFlowHelper.calculateYtm(flows, cleanPrice, valuationDate, dcb, compounding, frequency);
    }

    public double calculateAccruedInterest(List<CashFlow> flows, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        return CashFlowHelper.calculateAccruedInterest(flows, valuationDate, dcb, freq);
    }

    public double calculateMacaulayDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        return CashFlowHelper.calculateMacaulayDuration(flows, ytm, valuationDate, dcb, freq);
    }

    public double calculateModifiedDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        return CashFlowHelper.calculateModifiedDuration(flows, ytm, valuationDate, dcb, freq);
    }

    // Mentre la Duration ci dice quanto il prezzo varia in modo lineare, 
    // la Convexity corregge l'errore di questa approssimazione quando i tassi si muovono molto   
    public double calculateConvexity(List<CashFlow> flows, double ytm, double dirtyPrice, LocalDate valDate, DaycountBasis dcb, Compounding compounding) {
        double weightedSum = 0.0;

        for (CashFlow cf : flows) {
            if (cf.paymentDate().isAfter(valDate)) {
                double t = dcb.calculate(valDate, cf.paymentDate(), null);
                // PV del flusso scontato allo YTM
                double pv = cf.getTotalAmount() * MathUtil.getDiscountFactor(compounding, ytm, t);

                // Termine della sommatoria: t * (t + 1) * PV
                weightedSum += t * (t + 1) * pv;
            }
        }

        // Convexity = Sommatoria / (Prezzo * (1 + y)^2)
        return weightedSum / (dirtyPrice * Math.pow(1 + ytm, 2));
    }

    public double calculatePrice(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        return CashFlowHelper.calculatePrice(flows, ytm, valuationDate, dcb, compounding, frequency);
    }

    public double calculatePrice(List<CashFlow> flows, DiscountCurve curve, LocalDate valuationDate, DaycountBasis dcb, Frequency frequency) {
        return CashFlowHelper.calculatePrice(flows, curve, valuationDate, dcb, frequency);
    }
}
