/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;
import static org.softcaster.engine.enums.Compounding.COMPOUNDED;
import static org.softcaster.engine.enums.Compounding.CONTINUOUS;
import static org.softcaster.engine.enums.Compounding.SIMPLE;
import static org.softcaster.engine.enums.Compounding.SIMPLE_THEN_COMPOUNDED;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author ep
 */
public class BondCalculator {

    /**
     * Calcola il Rateo (Accrued Interest) alla data di valutazione.
     *
     * @param flows
     * @param valuationDate
     * @param dcb
     * @param freq
     * @return
     */
    public double calculateAccruedInterest(List<CashFlow> flows, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        // 1. Trova la cedola in corso (quella il cui periodo include la valuationDate)
        return flows.stream()
                .filter(cf -> !valuationDate.isBefore(cf.accrualStart()) && valuationDate.isBefore(cf.accrualEnd()))
                .findFirst()
                .map(cf -> {
                    // Rateo = Cedola Totale * (Giorni trascorsi dall'inizio / Giorni totali del periodo)
                    double daysFromStart = dcb.calculate(cf.accrualStart(), valuationDate, freq);
                    double totalDaysInPeriod = dcb.calculate(cf.accrualStart(), cf.accrualEnd(), freq);

                    // In alternativa, se hai la cedola annua: nominal * annualRate * dcb.calculate(start, valuationDate)
                    return cf.interest() * (daysFromStart / totalDaysInPeriod);
                })
                .orElse(0.0); // Nessun rateo se siamo fuori dai periodi o il bond è scaduto
    }

    /**
     * Calcola lo YTM (IRR) partendo dal Clean Price (Prezzo di mercato).
     *
     * @param flows
     * @param cleanPrice
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param frequency
     * @return the Yield To Maturity
     */
    public double calculateYtm(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double accrued = calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        double dirtyPrice = cleanPrice + accrued;

        // Filtriamo solo i flussi futuri per l'attualizzazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        return internalRateOfReturn(futureFlows, dirtyPrice, valuationDate, dcb, compounding, frequency);
    }

    public double calculateModifiedDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        double dirtyPrice = 0.0;
        double weightedSum = 0.0;

        // Filtriamo solo i flussi futuri rispetto alla valutazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        // 1. Calcolo Macaulay Duration
        for (CashFlow cf : futureFlows) {
            // Tempo 't' tra oggi e il pagamento (usando il Market Daycount, es. ACT/ACT)
            double t = dcb.calculate(valuationDate, cf.paymentDate(), freq);

            // Valore attuale del flusso (PV)
            double pv = cf.getTotalAmount() / Math.pow(1 + ytm, t);

            dirtyPrice += pv;
            weightedSum += t * pv;
        }

        double macaulayDuration = weightedSum / dirtyPrice;

        // 2. Calcolo Modified Duration
        // Nota: per i bond la formula standard usa la capitalizzazione composta annua 
        // o legata alla frequenza (k). Per i BTP si usa spesso k=1 o k=frequenza.
        int k = freq.getYearFraction();
        return macaulayDuration / (1 + (ytm / k));
    }

    public static double internalRateOfReturn(
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
                    switch (compounding) {
                        case SIMPLE ->
                            pv += cf.getTotalAmount() / (1 + rate * t);
                        case COMPOUNDED ->
                            pv += cf.getTotalAmount() / Math.pow(1 + rate, t);
                        case CONTINUOUS ->
                            pv += cf.getTotalAmount() * Math.exp(rate * t);
                        case SIMPLE_THEN_COMPOUNDED -> {
                            if (t <= 1) {
                                pv += cf.getTotalAmount() / (1 + rate * t);
                            } else {
                                pv += cf.getTotalAmount() / Math.pow(1 + rate, t);
                            }
                        }
                    }
                }
                return pv - dirtyPrice;
            }
        };

        return MathUtil.rootNewton(nlpFunction, 0.10, compounding);
    }
}
