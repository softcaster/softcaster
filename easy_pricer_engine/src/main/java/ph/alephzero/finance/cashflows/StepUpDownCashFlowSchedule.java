/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.cashflows;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ep
 */
public class StepUpDownCashFlowSchedule extends Schedule {

        public static class Builder extends Schedule.Builder<Builder> {

        @Override
        public StepUpDownCashFlowSchedule build() {
            return new StepUpDownCashFlowSchedule(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private StepUpDownCashFlowSchedule(Builder builder) {
        super(builder);
    }

    private CashFlowItem getCashFlowItem(Date val) {
        for (CashFlowItem cashFlowItem : getCashFlows()) {
            if (val.compareTo(cashFlowItem.getEnd()) == 0) {
                return cashFlowItem;
            }
        }

        return null;
    }

    public boolean completeCashFlowSchedule(List<CashFlowItem> stepUpDownData) {
        if (stepUpDownData == null || stepUpDownData.isEmpty()) {
            return false;
        }
        CashFlowItem cashFlowItem;
        for (CashFlowItem item : stepUpDownData) {
            cashFlowItem = getCashFlowItem(item.getEnd());
            if (cashFlowItem != null) {
                cashFlowItem.setInterest(principal * item.getInterest() * 1.0 / frequency);
            }
        }

        return true;
    }
}
