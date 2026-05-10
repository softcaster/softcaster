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

/**
 *
 * @author
 */
public class BondPricer extends AbstractFixedIncomePricer {

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

        return solveInternalRateOfReturn(futureFlows, dirtyPrice, valuationDate, dcb, compounding, frequency);
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
}
