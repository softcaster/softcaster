/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.DaycountBasis;

public class FrenchAmortizationStrategy implements AmortizationStrategy {

    @Override
    public List<CashFlow> generateCashFlows(double totalAmount, double rate, List<PaymentPeriod> periods, DaycountBasis dcb) {

        List<CashFlow> flows = new ArrayList<>();
        int n = periods.size();

        // Per l'ammortamento francese standard, il tasso periodico è fisso
        // (Solitamente annualRate / paymentsPerYear)
        double periodicRate = rate / (12.0 / getMonthsBetween(periods.get(0)));

        // Calcolo della Rata Costante (Formula Standard)
        double constantPayment = totalAmount * (periodicRate / (1 - Math.pow(1 + periodicRate, -n)));

        double remainingBalance = totalAmount;

        for (int i = 0; i < n; i++) {
            PaymentPeriod period = periods.get(i);

            // 1. Quota Interessi: calcolata sul debito residuo
            // Nota: Nel piano francese puro si usa il tasso periodico fisso, 
            // ma per precisione di pricing usiamo la yearFraction del periodo
            double interestPayment = remainingBalance * rate * period.yearFraction();

            // 2. Quota Capitale: Rata totale - Interessi
            double principalPayment = constantPayment - interestPayment;

            // Gestione dell'ultimo periodo per evitare errori di arrotondamento
            if (i == n - 1) {
                principalPayment = remainingBalance;
            }

            remainingBalance -= principalPayment;

            flows.add(new CashFlow(
                    period.accrualStart(),
                    period.accrualEnd(),
                    period.paymentDate(),
                    principalPayment,
                    interestPayment,
                    Math.max(0, remainingBalance),
                    CashFlowStatus.RECORDED
            ));
        }
        return flows;
    }

    private int getMonthsBetween(PaymentPeriod p) {
        return (int) java.time.temporal.ChronoUnit.MONTHS.between(p.accrualStart(), p.accrualEnd());
    }
}
