/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public class LoanPricer extends AbstractFixedIncomePricer {

    // Il TAEG usa il solutore della classe base ma con il netto erogato
    public double calculateTaeg(List<CashFlow> flows, double nominal, double fees,
            LocalDate settlementDate, DaycountBasis dcb, Compounding compounding, Frequency frequency) {
        double netDisbursed = nominal - fees;
        return solveInternalRateOfReturn(flows, netDisbursed, settlementDate, dcb, compounding, frequency);
    }
}
