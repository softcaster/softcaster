/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

public abstract class AbstractFixedIncomePricer {

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
}
