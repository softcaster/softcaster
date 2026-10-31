package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import org.softcaster.engine.cashflow.CashFlow;

/**
 * Rata del piano salvato di un mutuo (cash_flow_item): quota capitale,
 * interessi e debito residuo dopo la rata.
 */
public record LoanCashFlowDto(
        int number,
        LocalDate startDate,
        LocalDate endDate,
        double principal,
        double interest,
        double total,
        double outstanding,
        String status) {

    public static LoanCashFlowDto of(int number, CashFlow cf) {
        return new LoanCashFlowDto(number, cf.accrualStart(), cf.accrualEnd(), cf.principal(), cf.interest(),
                cf.getTotalAmount(), cf.outstandingBalance(), cf.cashFlowStatus().getDescription());
    }
}
