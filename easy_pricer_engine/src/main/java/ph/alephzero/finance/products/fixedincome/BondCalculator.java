/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.fixedincome;

import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.cashflows.CashFlowSchedule;
import ph.alephzero.finance.cashflows.CustomCashFlowSchedule;
import ph.alephzero.finance.cashflows.Schedule;

/**
 *
 * @author ep
 */
public class BondCalculator {

    public BondCalcOutputData bondValuation(BondCalcInputData input) {

        BondCalcOutputData output = new BondCalcOutputData();
        Schedule cfSchedule = null;
        if (input.getCashFlows() == null) {
            cfSchedule = new CashFlowSchedule.Builder()
                    .issue(input.getIssue())
                    .firstCoupon(input.getFirstCoupon())
                    .maturity(input.getMaturity())
                    .lastCoupon(input.getLastCoupon())
                    .frequency(input.getFrequency())
                    .issuePrice(input.getIssuePrice())
                    .redemptionPrice(input.getRedemptionPrice())
                    .couponRate(input.getCouponRate())
                    .basis(input.getBasis()).build();
        } else {
            cfSchedule = new CustomCashFlowSchedule.CustomBuilder().
                    basis(input.getBasis()).
                    build();
            ((CustomCashFlowSchedule) cfSchedule).setCashFlows(input.getCashFlows());

        }

        if (cfSchedule != null) {
            output.setAccruedInterest(cfSchedule.accruedInterest(input.getSettlement()));
            double dirtyPrice = input.getCurrentPrice() + output.getAccruedInterest();
            org.softcaster.commons.types.Date from = new org.softcaster.commons.types.Date(input.getSettlement());
            org.softcaster.commons.types.Date to = new org.softcaster.commons.types.Date(input.getMaturity());
            long daysToMaturity = to.days(from);
            Compounding compounding = Compounding.COMPOUNDED;
            if (daysToMaturity < 365) {
                compounding = Compounding.SIMPLE;
            }
            output.setYieldToMaturity(cfSchedule.irr(input.getSettlement(), dirtyPrice, 0.1, compounding));
            output.setDurationModified(cfSchedule.durationModified(input.getSettlement(), output.getYieldToMaturity(),compounding));
            output.setDurationMacaulay(cfSchedule.durationMacaulay(input.getSettlement(), output.getYieldToMaturity(),compounding));

            if (input.isFullCalc() && cfSchedule != null) {
                output.setPresentValue(cfSchedule.presentValue(input.getSettlement()));
            }
        }
        return output;
    }
}
