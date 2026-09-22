/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

public class CashFlowHelper {

    public static double solveInternalRateOfReturn(
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

    public static double calculateAccruedInterest(List<CashFlow> flows, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
        // 1. Trova la cedola in corso (quella il cui periodo include la valuationDate)
        return flows.stream()
                .filter(cf -> !valuationDate.isBefore(cf.accrualStart()) && valuationDate.isBefore(cf.accrualEnd()))
                .findFirst()
                .map(cf -> {

                    long daysFromStart = ChronoUnit.DAYS.between(cf.accrualStart(), valuationDate);
                    double theoreticalDaysInPeriod;

                    if (dcb == DaycountBasis.ACT_360) {
                        // Regola CCT: ignora la durata del semestre, usa sempre la base commerciale fissa (360 / 2 = 180)
                        theoreticalDaysInPeriod = dcb.getTime() / freq.getYearFraction();
                    } else {
                        // Regola BTP (ACT/ACT ICMA): usa i giorni ESATTI di questo specifico semestre (nel tuo caso restituirà 183)
                        theoreticalDaysInPeriod = ChronoUnit.DAYS.between(cf.accrualStart(), cf.accrualEnd());
                    }

                    return cf.interest() * ((double) daysFromStart / theoreticalDaysInPeriod);
                })
                .orElse(0.0); // Nessun rateo se siamo fuori dai periodi o il bond è scaduto
    }

    public static double calculateYtm(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double accrued = calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        double dirtyPrice = cleanPrice + accrued;

        // Filtriamo solo i flussi futuri per l'attualizzazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        return solveInternalRateOfReturn(futureFlows, dirtyPrice, valuationDate, dcb, compounding, frequency);
    }

    public static double calculatePrice(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
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

    public static double calculatePrice(List<CashFlow> flows, YieldCurve yieldCurve, LocalDate valuationDate, DaycountBasis dcb, Frequency frequency) {
        double accrued = calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        double dirtyPrice = 0;

        // Filtriamo solo i flussi futuri per l'attualizzazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        for (CashFlow cf : futureFlows) {
            double discountFactor = yieldCurve.getDiscountFactor(cf.accrualEnd());
            double amount = cf.getTotalAmount();
            double pv = amount * discountFactor;

            if (FileUtil.dumpDebugInfo()) {
                String message = "Accrual End: " + cf.accrualEnd() + "\tDF: " + discountFactor + "\tAmount:" + amount + "\tPresent Value:" + pv;
                System.out.println(message);
                LoggerMgr.logInfo(message);
            }

            dirtyPrice += pv;
        }

        return dirtyPrice - accrued;
    }

    public static double calculateMacaulayDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {
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

    public static double calculateModifiedDuration(List<CashFlow> flows, double ytm, LocalDate valuationDate, DaycountBasis dcb, Frequency freq) {

        double macaulayDuration = calculateMacaulayDuration(flows, ytm, valuationDate, dcb, freq);

        // 2. Calcolo Modified Duration
        // Nota: per i bond la formula standard usa la capitalizzazione composta annua 
        // o legata alla frequenza (k). Per i BTP si usa spesso k=1 o k=frequenza.
        int k = freq.getYearFraction() > 0 ? freq.getYearFraction() : 1;
        return macaulayDuration / (1 + (ytm / k));
    }

    // Mentre la Duration ci dice quanto il prezzo varia in modo lineare, 
    // la Convexity corregge l'errore di questa approssimazione quando i tassi si muovono molto   
    public static double calculateConvexity(List<CashFlow> flows, double ytm, double dirtyPrice, LocalDate valDate, DaycountBasis dcb, Compounding compounding, Frequency freq) {
        double weightedSum = 0.0;

        for (CashFlow cf : flows) {
            if (cf.paymentDate().isAfter(valDate)) {
                double t = dcb.calculate(valDate, cf.paymentDate(), freq);
                // PV del flusso scontato allo YTM
                double pv = cf.getTotalAmount() * MathUtil.getDiscountFactor(compounding, ytm, t);

                // Termine della sommatoria: t * (t + 1) * PV
                weightedSum += t * (t + 1) * pv;
            }
        }

        // Convexity = Sommatoria / (Prezzo * (1 + y)^2)
        return weightedSum / (dirtyPrice * Math.pow(1 + ytm, 2));
    }

}
