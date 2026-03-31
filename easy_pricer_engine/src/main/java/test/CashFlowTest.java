/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlowItem;
import ph.alephzero.finance.cashflows.CustomCashFlowSchedule;

/**
 *
 * @author ep
 */
public class CashFlowTest {

    public static void main(String[] args) {
        Date startDate = new Date();
        List<CashFlowItem> cashFlows = new ArrayList<>();
        
        CashFlowItem item = new CashFlowItem();
        item.setStart(startDate.utilDate());
        startDate.addMonths(1);
        item.setEnd(startDate.utilDate());        
        item.setInterest(100);
        item.setAmount(100000);
        cashFlows.add(item);

        item = new CashFlowItem();
        item.setStart(startDate.utilDate());
        startDate.addDays(20);
        item.setEnd(startDate.utilDate());        
        item.setInterest(100);
        item.setAmount(0);
        cashFlows.add(item);
        
        item = new CashFlowItem();
        item.setStart(startDate.utilDate());
        startDate.addMonths(1);
        item.setEnd(startDate.utilDate());        
        item.setInterest(100);
        item.setAmount(100000);
        cashFlows.add(item);

        item = new CashFlowItem();
        item.setStart(startDate.utilDate());
        startDate.addMonths(1);
        item.setEnd(startDate.utilDate());        
        item.setInterest(100);
        item.setAmount(100000);
        cashFlows.add(item);
        
        CustomCashFlowSchedule ccf = new CustomCashFlowSchedule.CustomBuilder().
                basis(DayCountBasis.ACT_ACT).
                build();
        ccf.setCashFlows(cashFlows);
        
        
        double yield = ccf.irr(new Date().utilDate(),299044.71097, 0.1,Compounding.SIMPLE);
        System.out.println(Converter.fromDouble(yield));
        System.out.println((double)133/365);
        System.out.println(133/365);
        
        /*
        BondCalcInputData input = new BondCalcInputData();
        input.setSettlement(DateUtil.createDate(2023, 5, 22));
        input.setCurrentPrice(64.21);
        
        input.setIssue(DateUtil.createDate(2021, 1, 8));
        input.setFirstCoupon(DateUtil.createDate(2021, 3, 1));
        input.setMaturity(DateUtil.createDate(2037, 3, 1));
        input.setLastCoupon(input.getMaturity());
        input.setSettlement(DateUtil.createDate(2023, 5, 22));
        input.setFrequency(2);
        input.setIssuePrice(100.);
        input.setRedemptionPrice(100.);
        input.setCouponRate(0.0095);
        input.setBasis(DayCountBasis.ACT_365);

        BondPricer pricer = new BondPricer();
        BondCalcOutputData output = pricer.bondValuation(input);
                
        System.out.println("Internal Rate Return\t" + ": " + output.getYieldToMaturity() * 100.);
        System.out.println("Accrued Interest\t"  + ": " + output.getAccruedInterest());
        System.out.println("Macaulay Duration\t"  + ": " + output.getDurationMacaulay());
        System.out.println("Modified Duration\t"  + ": " + output.getDurationModified());
         */
    }
}
