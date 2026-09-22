/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.math.MathUtil;

public class EffectiveInterestScheduleGenerator {

    public List<AmortizedCostPeriod> generate(
            List<CashFlow> cashflows,
            double dirtyPrice,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double irr) { // gia' calcolato a monte con solveInternalRateOfReturn

        List<AmortizedCostPeriod> schedule = new ArrayList<>();
        double carryingValue = dirtyPrice;

        for (CashFlow cf : cashflows) {

            double periodYearFraction = dcb.calculate(cf.accrualStart(), cf.accrualEnd(), frequency);

            // capitalizzazione del carrying value per un periodo, stesso schema usato per l'IRR
            double discountFactor = MathUtil.getDiscountFactor(compounding, irr, periodYearFraction);
            double effectiveInterest = carryingValue * (1.0 / discountFactor - 1.0);

            double couponCashInterest = cf.interest();
            double discountAccretion = effectiveInterest - couponCashInterest;
            double closingCarryingValue = carryingValue + discountAccretion - cf.principal();

            schedule.add(new AmortizedCostPeriod(
                cf.accrualStart(), cf.accrualEnd(),
                carryingValue, effectiveInterest, couponCashInterest,
                discountAccretion, closingCarryingValue
            ));

            carryingValue = closingCarryingValue;
        }

        return schedule;
    }
    
public List<AmortizedCostPeriod> buildAmortizedCostSchedule(
        List<CashFlow> flows, double cleanPrice, LocalDate valuationDate,
        DaycountBasis dcb, Compounding compounding, Frequency frequency) {

    // Stesso identico filtro/IRR di calculateYtm: un solo tasso, quello di mercato
    double accrued = CashFlowHelper.calculateAccruedInterest(flows, valuationDate, dcb, frequency);
    double dirtyPrice = cleanPrice + accrued;

    List<CashFlow> futureFlows = flows.stream()
            .filter(cf -> cf.paymentDate().isAfter(valuationDate))
            .toList();

    double irr = CashFlowHelper.solveInternalRateOfReturn(futureFlows, dirtyPrice, valuationDate, dcb, compounding, frequency);

    List<AmortizedCostPeriod> schedule = new ArrayList<>();
    double carryingValue = cleanPrice; // <-- CLEAN, non dirty: l'accrued resta fuori,
                                        //     gestito dal meccanismo esistente

    for (int i = 0; i < futureFlows.size(); i++) {
        CashFlow cf = futureFlows.get(i);
        boolean isFirstPeriod = (i == 0);

        LocalDate periodStart = isFirstPeriod ? valuationDate : cf.accrualStart();
        // periodStart per lo stub e' la data di valutazione, non l'inizio
        // del periodo cedolare contrattuale (che potrebbe essere gia' passato)

        double periodYearFraction = dcb.calculate(periodStart, cf.accrualEnd(), frequency);

        double effectiveInterest = carryingValue * (1.0 / MathUtil.getDiscountFactor(compounding, irr, periodYearFraction) - 1.0);

        double couponCashInterest;
        if (isFirstPeriod) {
            // Solo la quota di cedola maturata da valuationDate in poi -
            // esattamente cio' che il meccanismo di accrual esistente
            // (getDailyAccrualAmount) matura giorno per giorno da oggi.
            // La quota gia' maturata prima dell'acquisto (accrued) e' esclusa:
            // e' gia' interamente sul conto ponte Accrued Interest.
            couponCashInterest = cf.interest() - accrued;
        } else {
            couponCashInterest = cf.interest(); // periodi pieni, nessuna esclusione
        }

        double discountAccretion = effectiveInterest - couponCashInterest;
        double closingCarryingValue = carryingValue + discountAccretion - cf.principal();

        schedule.add(new AmortizedCostPeriod(
            periodStart, cf.accrualEnd(),
            carryingValue, effectiveInterest, couponCashInterest,
            discountAccretion, closingCarryingValue
        ));

        carryingValue = closingCarryingValue;
    }

    return schedule;
}    
}