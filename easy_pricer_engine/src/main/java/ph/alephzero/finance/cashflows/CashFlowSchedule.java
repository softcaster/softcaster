/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.cashflows;

/**
 *
 * @author ep
 */
public class CashFlowSchedule extends Schedule {

    public static class Builder extends Schedule.Builder<Builder> {

        @Override
        public CashFlowSchedule build() {
            return new CashFlowSchedule(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private CashFlowSchedule(Builder builder) {
        super(builder);
    }
}

/*    
    
    public CashFlowSchedule(CashFlowItem... cashFlows) {
        this.cashFlows = new ArrayList<>();

        this.cashFlows.addAll(Arrays.asList(cashFlows));

    }
 */
