/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;
import org.softcaster.engine.math.MathUtil.Function1;

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

    public double solveDiscountMargin(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double currentRate // es. Euribor 6M noto (0.0262)
    ) {

        MathUtil.Function1 dmFunction = new MathUtil.Function1() {
            @Override
            public double f(double dm) {
                return f(dm, compounding);
            }

            @Override
            public double f(double dm, Compounding compounding) {
                double pv = 0.0;
                double freqValue = frequency.getYearFraction(); // es. 2.0 per semestrale

                for (int i = 0; i < cashflows.size(); i++) {
                    CashFlow cf = cashflows.get(i);

                    // 1. Calcoliamo il tempo residuo per lo sconto
                    double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);

                    // 2. STIMA DEL FLUSSO FUTURO (Indicizzazione costante all'Euribor attuale)
                    // Ricostruiamo la cedola teorica del CCT: (Euribor + Spread del Titolo) * FrazioneAnnoPeriodo
                    // Lo spread del titolo (0,75%) è fisso. Per ricavarlo dinamicamente dal flusso corrente:
                    // Tasso Cedola Corrente = (Cedola Semestrale * Frequenza) / 100 -> es. (1.5935 * 2) / 100 = 3.187%
                    // Spread Titolo = Tasso Cedola Corrente - currentRate -> 3.187% - 2.62% = 0.567% (o 0.75% a seconda della cedola)
                    // NOTA: Se l'oggetto CashFlow ha già incorporato lo spread corretto all'emissione,
                    // le cedole future stimate all'Euribor attuale avranno tasso annuo pari a (currentRate + spread)
                    double cctSpread = 0.0075; // Lo spread dello 0,75% del tuo CCT-Eu Ot30

                    double estimatedCouponRate = currentRate + cctSpread;
                    double estimatedInterest = 100.0 * (estimatedCouponRate / freqValue);

                    double expectedFlowAmount = estimatedInterest;

                    // Se è l'ultimo flusso (scadenza), aggiungiamo il rimborso del capitale a 100
                    if (i == cashflows.size() - 1) {
                        expectedFlowAmount += 100.0;
                    }

                    // 3. ATTUALIZZAZIONE (Euribor + DM)
                    double totalDiscountRate = currentRate + dm;
                    double df = MathUtil.getDiscountFactor(compounding, totalDiscountRate, t);

                    pv += expectedFlowAmount * df;
                }

                return pv - dirtyPrice;
            }
        };

        return MathUtil.rootNewton(dmFunction, 0.005, compounding);
    }

    public double solveZSpread(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            YieldCurve curve
    ) {

        double dfSettle = curve.getDiscountFactor(valuationDate);

        // keep only flows paid after settlement
        List<CashFlow> future = cashflows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        final double[] t = new double[future.size()];
        final double[] amountTimesDf = new double[future.size()];
        for (int i = 0; i < future.size(); i++) {
            CashFlow cf = future.get(i);
            t[i] = dcb.calculate(valuationDate, cf.paymentDate(), frequency);
            amountTimesDf[i] = cf.getTotalAmount() * curve.getDiscountFactor(cf.paymentDate()) / dfSettle;
        }

        Function1 objective = new Function1() {
            @Override
            public double f(double s) {
                return f(s, Compounding.CONTINUOUS);
            }

            @Override
            public double f(double s, Compounding c) {
                double pv = 0;
                for (int i = 0; i < t.length; i++) {
                    pv += amountTimesDf[i] * Math.exp(-s * t[i]);YieldCurve.discountFactorFromRate(s, t[i], c);
                }
                return pv - dirtyPrice;
            }
        };
        return MathUtil.rootNewton(objective, 0.001, 1e-8, 50, compounding);
    }
}
