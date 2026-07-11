/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

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

    public double calculatePrice(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double accrued = calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        double dirtyPrice = 0;

        // Filtriamo solo i flussi futuri per l'attualizzazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        for (CashFlow cf : futureFlows) {
            double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);
            dirtyPrice += cf.getTotalAmount() * MathUtil.getDiscountFactor(compounding, ytm, t);
        }

        return dirtyPrice - accrued;
    }

    public double calculateMacaulayDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
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

        if (dirtyPrice <= 0.) {
            return 0.;
        } else {
            return (weightedSum / dirtyPrice);
        }
    }

    public double calculateModifiedDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {

        double macaulayDuration = calculateMacaulayDuration(flows, ytm, valuationDate, dcb, freq);

        // 2. Calcolo Modified Duration
        // Nota: per i bond la formula standard usa la capitalizzazione composta annua 
        // o legata alla frequenza (k). Per i BTP si usa spesso k=1 o k=frequenza.
        int k = freq.getYearFraction() > 0 ? freq.getYearFraction() : 1;
        return macaulayDuration / (1 + (ytm / k));
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

    public XRBOutputData calculate(XRBInputData input) {
        XRBOutputData output = new XRBOutputData();

        // ytm
        output.setYtm(calculateYtm(input.getFlows(), input.getReferencePrice(), input.getValuationDate(),
                input.getDaycount(), input.getCompounding(), input.getFrequency()));

        // accruals 
        output.setAccruedInterest(calculateAccruedInterest(input.getFlows(), input.getValuationDate(),
                input.getDaycount(), input.getFrequency()));

        // mod duration
        output.setModifiedDuration(calculateModifiedDuration(input.getFlows(), output.getYtm(), input.getValuationDate(),
                input.getDaycount(), input.getFrequency()));

        output.setValuationDate(input.getValuationDate());
        output.setMktPrice(input.getReferencePrice());

        double dv01 = output.getMktPrice() * output.getModifiedDuration() * 0.0001;
        output.setDv01(dv01);
        return output;
    }
    /*
    public double calculateZSpread(List<CashFlow> flows, double dirtyPrice, LocalDate valDate, 
                               DaycountBasis dcb, RateCurve curve) {
    
    MathUtil.Function1 zFunction = new MathUtil.Function1() {
        @Override
        public double f(double z) {
            double pv = 0.0;
            for (CashFlow cf : flows) {
                if (cf.paymentDate().isAfter(valDate)) {
                    double t = dcb.calculate(valDate, cf.paymentDate(), null);
                    // Recuperiamo il tasso risk-free per la scadenza t dalla curva
                    double r = curve.getRate(t); 
                    // Scontiamo al tasso (r + z)
                    pv += cf.getTotalAmount() / Math.pow(1 + r + z, t);
                }
            }
            return pv - dirtyPrice;
        }

        @Override public double f(double x, Compounding c) { return f(x); }
    };

    // Usiamo Newton-Raphson per trovare lo spread z
    // Guess iniziale: 0.01 (100 basis points)
    return MathUtil.rootNewton(zFunction, 0.01);
}

     */
}
