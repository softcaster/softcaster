/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.engine.enums.DaycountBasis;

public class BulletAmortizationStrategy implements AmortizationStrategy {

    @Override
    public List<CashFlow> generateCashFlows(double nominal, double annualRate,
            List<PaymentPeriod> periods, DaycountBasis dcb) {
        List<CashFlow> flows = new ArrayList<>();

        for (int i = 0; i < periods.size(); i++) {
            PaymentPeriod period = periods.get(i);
            boolean isLastPeriod = (i == periods.size() - 1);

            // 1. Calcolo dell'interesse per questo slot temporale
            // Usiamo la yearFraction già calcolata dal generatore
            double interest = nominal * annualRate * period.yearFraction();

            // 2. Il capitale viene rimborsato SOLO nell'ultimo periodo
            double principal = isLastPeriod ? nominal : 0.0;

            // 3. Il debito residuo resta uguale al nominale fino all'ultimo pagamento
            double outstandingBalance = isLastPeriod ? 0.0 : nominal;

            // Creazione del CashFlow finale
            flows.add(new CashFlow(
                    period.accrualStart(),
                    period.accrualEnd(),
                    period.paymentDate(),
                    principal,
                    interest,
                    outstandingBalance
                    
            ));
        }
        return flows;
    }
}
