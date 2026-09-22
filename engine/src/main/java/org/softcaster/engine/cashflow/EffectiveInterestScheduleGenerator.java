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
            LocalDate valuationDate,
            List<CashFlow> cashflows,
            double dirtyPrice,
            DaycountBasis dcb,
            Compounding compounding,
            Frequency frequency,
            double irr) {

        List<AmortizedCostPeriod> schedule = new ArrayList<>();
        double carryingValue = dirtyPrice;

        List<CashFlow> futureFlows = cashflows.stream()
                .filter(cf -> cf.paymentDate().isAfter(valuationDate))
                .toList();

        for (int i = 0; i < futureFlows.size(); i++) {
            CashFlow cf = futureFlows.get(i);
            boolean isFirstPeriod = (i == 0);

            // FIX: il primo periodo parte da valuationDate, non da cf.accrualStart(),
            // per restare coerente con il "t" usato da solveInternalRateOfReturn
            LocalDate periodStart = isFirstPeriod ? valuationDate : cf.accrualStart();
            double periodYearFraction = dcb.calculate(periodStart, cf.accrualEnd(), frequency);

            double discountFactor = MathUtil.getDiscountFactor(compounding, irr, periodYearFraction);
            double effectiveInterest = carryingValue * (1.0 / discountFactor - 1.0);

            // couponCashInterest resta SEMPRE l'importo pieno, mai netto dell'accrued -
            // coerente con l'equazione di prezzo usata per determinare l'IRR
            double couponCashInterest = cf.interest();

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
