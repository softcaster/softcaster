/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.fixedincome.BondCalcInputData;
import ph.alephzero.finance.products.fixedincome.BondCalcOutputData;
import ph.alephzero.finance.products.fixedincome.StepUpDownBondPricer;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author ep
 */
public class StepUpDownCashFlowTest {
    
    public static void main(String[] args) {
        BondCalcInputData input = new BondCalcInputData();
        input.setSettlement(DateUtil.createDate(2023, 5, 25));
        input.setCurrentPrice(83.31);
        
        input.setIssue(DateUtil.createDate(2021, 4, 1));
        input.setFirstCoupon(DateUtil.createDate(2022, 4, 1));
        input.setMaturity(DateUtil.createDate(2028, 4, 1));
        input.setLastCoupon(input.getMaturity());
        input.setFrequency(1);
        input.setIssuePrice(100.);
        input.setRedemptionPrice(100.);
        input.setCouponRate(0.095);
        input.setBasis(DayCountBasis.ACT_365);

        StepUpDownBondPricer pricer = new StepUpDownBondPricer();
        BondCalcOutputData output = pricer.price(input, null);
        
        
        System.out.println("Internal Rate Return\t" + ": " + output.getYieldToMaturity() * 100.);
        System.out.println("Accrued Interest\t"  + ": " + output.getAccruedInterest());
        System.out.println("Macaulay Duration\t"  + ": " + output.getDurationMacaulay());
        System.out.println("Modified Duration\t"  + ": " + output.getDurationModified());
    }
}
