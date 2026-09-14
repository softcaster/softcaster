/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.dto.FRBInputData;
import org.softcaster.engine.dto.FRBOutputData;
import org.softcaster.engine.dto.XRBInputData;
import org.softcaster.engine.dto.XRBOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

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

    public double calculateFltShortBondYield(List<CashFlow> flows, LocalDate valuationDate, double referencePrice, double redemptionPrice, double accruedInterest, DaycountBasis dcb) {
        // 1. Trova l'unico cash flow della cedola in corso (quella che scadrà a breve)
        CashFlow currentFlow = flows.stream()
                .filter(cf -> !valuationDate.isBefore(cf.accrualStart()) && valuationDate.isBefore(cf.accrualEnd()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nessun periodo cedolare attivo alla data di valutazione"));

        // 2. Calcola il Prezzo Tel Quel (Dirty Price) pagato sul mercato
        double dirtyPrice = referencePrice + accruedInterest;

        // 3. Calcola il valore totale ottenuto alla data di stacco (Capitale 100 + Cedola Semestrale Intera)
        double totalPayoutAtReset = redemptionPrice + currentFlow.interest();

        // 4. Calcola i giorni effettivi che mancano da OGGI (valuationDate) alla data di stacco cedola
        long daysToReset = ChronoUnit.DAYS.between(valuationDate, currentFlow.accrualEnd());

        // Frazione d'anno monetaria (ACT/360) per il tempo residuo
        double yearFractionToReset = daysToReset / dcb.getTime();

        // 5. Formula inversa per ricavare lo Yield (Rendimento annualizzato linearmente)
        // dirtyPrice = totalPayoutAtReset / (1 + yield * yearFractionToReset)
        double shortBondYield = ((totalPayoutAtReset / dirtyPrice) - 1.0) / yearFractionToReset;

        return shortBondYield; // Restituisce es. 0.032 (3.2%)
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

    public double calculateDiscountMargin(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency, double currentRate) {
        double accrued = calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        double dirtyPrice = cleanPrice + accrued;

        // Filtriamo solo i flussi futuri per l'attualizzazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        return solveDiscountMargin(futureFlows, dirtyPrice, valuationDate, dcb, compounding, frequency, currentRate);
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

    public double calculatePrice(List<CashFlow> flows, YieldCurve yieldCurve, LocalDate valuationDate, DaycountBasis dcb, Frequency frequency) {
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

    /**
     * Calcola la Margin Duration per un CCT-Eu tramite shock numerico del
     * Discount Margin.
     *
     * @param cashflows Lista dei flussi futuri
     * @param dirtyPrice Il prezzo Tel Quel di mercato attuale
     * @param valuationDate Data di valutazione
     * @param dcb Daycountbasis (es. ACT_360)
     * @param compounding Compounding (es. COMPOUNDED)
     * @param frequency Frequenza (es. SEMIANNUAL)
     * @param currentRate L'Euribor 6M corrente (es. 0.0262)
     * @param calculatedDm Il Discount Margin calcolato dal solutore (es.
     * 0.0030486)
     * @return La Margin Duration (espressa in anni, solitamente compresa tra
     * 0.1 e 0.5)
     */
    public double calculateMarginDuration(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double currentRate,
            double calculatedDm
    ) {
        // Dimensione dello shock: 1 punto base (0.01%)
        double h = 0.0001;

        // 1. Ricalcoliamo il Prezzo se lo spread sale (Calculated DM + 1 bp)
        double priceUp = calculatePvForDm(cashflows, valuationDate, dcb, compounding, frequency, currentRate, calculatedDm + h);

        // 2. Ricalcoliamo il Prezzo se lo spread scende (Calculated DM - 1 bp)
        double priceDown = calculatePvForDm(cashflows, valuationDate, dcb, compounding, frequency, currentRate, calculatedDm - h);

        // 3. Formula della Duration Modificata Numerica (Differenze Centrali):
        // Duration = (PriceDown - PriceUp) / (2 * PriceOriginale * Shock)
        double marginDuration = (priceDown - priceUp) / (2.0 * dirtyPrice * h);

        return marginDuration;
    }

    /**
     * Funzione privata di supporto per calcolare il PV dato un preciso livello
     * di DM (Riprende esattamente la logica della tua funzione obiettivo)
     */
    private double calculatePvForDm(List<CashFlow> cashflows, LocalDate valuationDate, DaycountBasis dcb,
            Compounding compounding, Frequency frequency, double currentRate, double dm) {
        double pv = 0.0;
        double freqValue = frequency.getYearFraction();
        double cctSpread = 0.0075; // Spread contrattuale del CCT

        for (int i = 0; i < cashflows.size(); i++) {
            CashFlow cf = cashflows.get(i);
            double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);

            // Stima della cedola condizionata al currentRate
            double estimatedCouponRate = currentRate + cctSpread;
            double expectedFlowAmount = 100.0 * (estimatedCouponRate / freqValue);

            if (i == cashflows.size() - 1) {
                expectedFlowAmount += 100.0; // Rimborso capitale
            }

            // Sconto con la combinazione corrente + spread incognito (shockato)
            double totalDiscountRate = currentRate + dm;
            double df = MathUtil.getDiscountFactor(compounding, totalDiscountRate, t);

            pv += expectedFlowAmount * df;
        }
        return pv;
    }

    /**
     * Ricalcola il PV simulando uno shock dell'Euribor per estrarre la Margin
     * Duration reale.
     *
     * @param deltaRate Lo shock da applicare al tasso (es. +0.0001 o -0.0001)
     */
    private double calculatePvForRealDuration(
            List<CashFlow> cashflows,
            LocalDate valuationDate,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double currentRate,
            double spread,
            double dm,
            double deltaDm // Lo shock applicato (+h oppure -h)
    ) {
        double pv = 0.0;
        double freqValue = frequency.getYearFraction();

        for (int i = 0; i < cashflows.size(); i++) {
            CashFlow cf = cashflows.get(i);
            double t = dcb.calculate(valuationDate, cf.paymentDate(), frequency);

            double expectedFlowAmount;

            // I flussi NON vengono toccati dallo shock dello spread di credito.
            // Rimangono ancorati alla proiezione dell'Euribor iniziale (currentRate).
            if (i == 0) {
                expectedFlowAmount = cf.interest();
            } else {
                double estimatedCouponRate = currentRate + spread;
                expectedFlowAmount = 100.0 * (estimatedCouponRate / freqValue);
            }

            if (i == cashflows.size() - 1) {
                expectedFlowAmount += 100.0; // Rimborso del capitale alla fine
            }

            // LO SCONTO: Risente dell'Euribor stabile + il Discount Margin shockato (+h o -h)
            double totalDiscountRate = currentRate + (dm + deltaDm);
            double df = MathUtil.getDiscountFactor(compounding, totalDiscountRate, t);

            pv += expectedFlowAmount * df;
        }
        return pv;
    }

    public double calculateRealMarginDuration(
            List<CashFlow> cashflows,
            double dirtyPrice,
            LocalDate valuationDate,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double currentRate,
            double spread,
            double calculatedDm
    ) {
        double h = 0.0001; // Shock di 1 punto base

        // Applichiamo lo shock (+h e -h) all'Euribor, facendo ricalcolare flussi e sconti in modo coordinato
        double priceUp = calculatePvForRealDuration(cashflows, valuationDate, dcb, compounding, frequency, currentRate, spread, calculatedDm, h);
        double priceDown = calculatePvForRealDuration(cashflows, valuationDate, dcb, compounding, frequency, currentRate, spread, calculatedDm, -h);

        // Formula della Duration Modificata classica
        return (priceDown - priceUp) / (2.0 * dirtyPrice * h);
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

    public FRBOutputData calculate(FRBInputData input) {
        FRBOutputData output = new FRBOutputData();

        // ytm
        output.setYtm(calculateYtm(input.getFlows(), input.getReferencePrice(), input.getValuationDate(),
                input.getDaycount(), input.getCompounding(), input.getFrequency()));

        // accruals 
        output.setAccruedInterest(calculateAccruedInterest(input.getFlows(), input.getValuationDate(),
                input.getDaycount(), input.getFrequency()));

        // mod duration
        output.setModifiedDuration(calculateModifiedDuration(input.getFlows(), output.getYtm(), input.getValuationDate(),
                input.getDaycount(), input.getFrequency()));

        // ShortBond Yield
        output.setShortBondYield(calculateFltShortBondYield(input.getFlows(), input.getValuationDate(), input.getReferencePrice(), input.getRedemptionPrice(), output.getAccruedInterest(), input.getDaycount()));

        output.setValuationDate(input.getValuationDate());
        output.setMktPrice(input.getReferencePrice());

        // Discount Margin
        output.setDiscountMargin(calculateDiscountMargin(input.getFlows(), input.getReferencePrice(), input.getValuationDate(), input.getDaycount(), input.getCompounding(), input.getFrequency(), input.getReferenceRate()));

        // Margin Duration
        output.setMarginDuration(calculateRealMarginDuration(input.getFlows(),
                input.getReferencePrice() + output.getAccruedInterest(),
                input.getValuationDate(),
                input.getDaycount(),
                input.getCompounding(),
                input.getFrequency(),
                input.getReferenceRate(),
                0.0075,
                output.getDiscountMargin()));

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
