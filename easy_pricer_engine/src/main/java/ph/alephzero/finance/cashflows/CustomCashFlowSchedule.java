/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.cashflows;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.softcaster.commons.utils.NumberUtils;

/**
 *
 * @author ep
 */
public class CustomCashFlowSchedule extends Schedule {

    public static class CustomBuilder extends Schedule.Builder<CustomBuilder> {

        @Override
        public CustomCashFlowSchedule build() {
            return new CustomCashFlowSchedule(this);
        }

        @Override
        protected CustomBuilder self() {
            return this;
        }

        @Override
        protected List<CashFlowItem> getCashFlows() {
            return new ArrayList<>();
        }
    }

    private CustomCashFlowSchedule(CustomBuilder builder) {
        super(builder);
    }

    public void setCashFlows(List<CashFlowItem> cashFlows) {
        this.cashFlows.clear();
        for (CashFlowItem item : cashFlows) {
            this.cashFlows.add(item);
        }
    }

    @Override
    public double presentValue(final Date settlement) {
        double pv = 0;
        org.softcaster.commons.types.Date _settlement = new org.softcaster.commons.types.Date(settlement);
        for (CashFlowItem item : cashFlows) {
            org.softcaster.commons.types.Date end = new org.softcaster.commons.types.Date(item.getEnd());
            if(end.isLessOrEqualThan(_settlement))
                continue;
            double t = (end.days(_settlement)) / 365.;
            double df = Math.pow((1 + item.getDiscountFactors()), t);
            if (!NumberUtils.isZero(df)) {
                pv += (item.getAmount() + item.getInterest()) / df;
            }
        }
        return pv;
    }
}
