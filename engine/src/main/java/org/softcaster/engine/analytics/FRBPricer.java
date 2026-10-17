/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.CashFlowAnalytics;
import org.softcaster.engine.dto.FRBInput;
import org.softcaster.engine.dto.FRBResult;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public class FRBPricer extends AbstractFixedIncomePricer<FRBInput, FRBResult> {

    @Override
    public FRBResult calculate(FRBInput input) {
        // ytm
        double ytm = calculateYtm(input.schedule().flows(), input.cleanPrice(), input.valuationDate(),
                input.schedule().daycount(), input.schedule().compounding(), input.schedule().frequency());

        // accruals 
        double accruedInterest = calculateAccruedInterest(input.schedule().flows(), input.valuationDate(),
                input.schedule().daycount(), input.schedule().frequency());

        // mod duration
        double modifiedDuration = calculateModifiedDuration(input.schedule().flows(), ytm, input.valuationDate(),
                input.schedule().daycount(), input.schedule().frequency());

        // dv01
        double dv01 = (input.cleanPrice() + accruedInterest) * modifiedDuration * 0.0001;

        // discountMargin
        double discountMargin = calculateDiscountMargin(input.schedule().flows(), input.cleanPrice(), input.valuationDate(),
                input.schedule().daycount(), input.schedule().compounding(), input.schedule().frequency(), input.floating().referenceRate(), input.floating().spread());

        // marginDuration
        double marginDuration = calculateMarginDuration(
                input.schedule().flows(),
                input.cleanPrice() + accruedInterest,
                input.valuationDate(),
                input.schedule().daycount(),
                input.schedule().compounding(),
                input.schedule().frequency(),
                input.floating().referenceRate(),//L'Euribor 6M corrente
                input.floating().spread(),
                discountMargin);//Il Discount Margin calcolato dal solutore

        // realMarginDuration
        double realMarginDuration = calculateRealMarginDuration(
                input.schedule().flows(),
                input.cleanPrice() + accruedInterest,
                input.valuationDate(),
                input.schedule().daycount(),
                input.schedule().compounding(),
                input.schedule().frequency(),
                input.floating().referenceRate(),//L'Euribor 6M corrente
                input.floating().spread(),
                discountMargin);//Il Discount Margin calcolato dal solutore

        // Prepara result
        CashFlowAnalytics analytics = new CashFlowAnalytics(
                input.cleanPrice() + accruedInterest, accruedInterest,
                ytm, modifiedDuration, 0., dv01, null);

        FRBResult result = new FRBResult(input.code(), input.valuationDate(), analytics,
                discountMargin,
                marginDuration,
                realMarginDuration, null);
        return result;
    }

    public double calculateDiscountMargin(List<CashFlow> flows, double cleanPrice, LocalDate valuationDate, DaycountBasis dcb, Compounding compounding, Frequency frequency, double currentRate, double spread) {
        double accrued = calculateAccruedInterest(flows, valuationDate, dcb, frequency);
        double dirtyPrice = cleanPrice + accrued;

        // Filtriamo solo i flussi futuri per l'attualizzazione
        List<CashFlow> futureFlows = flows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        return solveDiscountMargin(futureFlows, dirtyPrice, valuationDate, dcb, compounding, frequency, currentRate, spread);
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
     * Margin (spread) duration: sensitivity to the discount margin, coupons
     * fixed. Close to the residual life.
     *
     * @param flows
     * @param dirtyPrice
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param frequency
     * @param currentRate
     * @param spread
     * @param calculatedDm
     * @return
     */
    public double calculateMarginDuration(List<CashFlow> flows, double dirtyPrice, LocalDate valuationDate,
            DaycountBasis dcb, Compounding compounding, Frequency frequency,
            double currentRate, double spread, double calculatedDm) {
        double h = 0.0001;
        double up = pvFlatModel(flows, valuationDate, dcb, compounding, frequency, currentRate, spread, calculatedDm, 0.0, h);
        double down = pvFlatModel(flows, valuationDate, dcb, compounding, frequency, currentRate, spread, calculatedDm, 0.0, -h);
        return (down - up) / (2.0 * dirtyPrice * h);
    }

    /**
     * "Real" margin duration: Euribor shocked, coupons and discounting move
     * together. Short, about the time to the next reset.
     *
     * @param flows
     * @param dirtyPrice
     * @param valuationDate
     * @param dcb
     * @param compounding
     * @param frequency
     * @param currentRate
     * @param spread
     * @param calculatedDm
     * @return
     */
    public double calculateRealMarginDuration(List<CashFlow> flows, double dirtyPrice, LocalDate valuationDate,
            DaycountBasis dcb, Compounding compounding, Frequency frequency,
            double currentRate, double spread, double calculatedDm) {
        double h = 0.0001;
        double up = pvFlatModel(flows, valuationDate, dcb, compounding, frequency, currentRate, spread, calculatedDm, h, 0.0);
        double down = pvFlatModel(flows, valuationDate, dcb, compounding, frequency, currentRate, spread, calculatedDm, -h, 0.0);
        return (down - up) / (2.0 * dirtyPrice * h);
    }
}
