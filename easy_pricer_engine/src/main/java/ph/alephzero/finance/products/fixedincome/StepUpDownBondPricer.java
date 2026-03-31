/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.fixedincome;

import java.util.List;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.cashflows.CashFlowItem;
import ph.alephzero.finance.cashflows.StepUpDownCashFlowSchedule;

/**
 *
 * @author ep
 */
public class StepUpDownBondPricer {
    
    public BondCalcOutputData price(BondCalcInputData input, List<CashFlowItem> items) {
        BondCalcOutputData output = new BondCalcOutputData();

        StepUpDownCashFlowSchedule cashFlows = new StepUpDownCashFlowSchedule.Builder()
                .issue(input.getIssue())
                .firstCoupon(input.getFirstCoupon())
                .maturity(input.getMaturity())
                .lastCoupon(input.getLastCoupon())
                .frequency(input.getFrequency())
                .issuePrice(input.getIssuePrice())
                .redemptionPrice(input.getRedemptionPrice())
                .couponRate(input.getCouponRate())
                .basis(input.getBasis()).build();
        
        if(items != null) {
            cashFlows.completeCashFlowSchedule(items);
        }
        
        output.setYieldToMaturity(cashFlows.irr(input.getSettlement(), input.getCurrentPrice(), 0.1,Compounding.COMPOUNDED));
        output.setAccruedInterest(cashFlows.accruedInterest(input.getSettlement()));
        output.setDurationModified(cashFlows.durationModified(input.getSettlement(), output.getYieldToMaturity(),Compounding.COMPOUNDED));
        output.setDurationMacaulay(cashFlows.durationMacaulay(input.getSettlement(), output.getYieldToMaturity(),Compounding.COMPOUNDED));

        
        
        return output;
    }

}
