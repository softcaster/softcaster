/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.util.List;
import org.softcaster.engine.enums.DaycountBasis;

public interface AmortizationStrategy {

    public List<CashFlow> generateCashFlows(double totalAmount, double rate,
            List<PaymentPeriod> periods, DaycountBasis dcb);
}
