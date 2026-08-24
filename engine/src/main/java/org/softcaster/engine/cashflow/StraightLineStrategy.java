/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.DaycountBasis;

public class StraightLineStrategy implements AmortizationStrategy {

    @Override
    public List<CashFlow> generateCashFlows(double totalAmount, double rate,
            List<PaymentPeriod> periods, DaycountBasis dcb) {
        List<CashFlow> flows = new ArrayList<>();
        double principalPerPeriod = totalAmount / periods.size(); // Quota capitale fissa
        double remainingBalance = totalAmount;

        for (PaymentPeriod period : periods) {
            double interest = remainingBalance * rate * period.yearFraction();
            remainingBalance -= principalPerPeriod;

            // Creazione del CashFlow "legato" al periodo
            flows.add(new CashFlow(
                    period.accrualStart(),
                    period.accrualEnd(),
                    period.paymentDate(),
                    principalPerPeriod,
                    interest,
                    Math.max(0, remainingBalance),
                    CashFlowStatus.RECORDED
            ));
        }
        return flows;
    }
}
