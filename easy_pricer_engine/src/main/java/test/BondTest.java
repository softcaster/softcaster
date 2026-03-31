/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.cashflows.CashFlows;
import ph.alephzero.finance.products.fixedincome.BondCashFlowGenerator;
import ph.alephzero.finance.products.fixedincome.BondValuation;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author ep
 */
public class BondTest {
    
    public static void main(String[] args) {
        Date issue = DateUtil.createDate(2017,9,1   );
        Date settlement = DateUtil.createDate(2021, 1, 28);
        Date maturity = DateUtil.createDate(2038, 9, 1);
        double coupon = 0.0295   ;
        double price = 127.97;
        int frequency = 2;
        double accruedInterest = BondValuation.accruedInterest(issue, settlement, coupon, frequency, DayCountBasis.ACT_ACT);
        System.out.println("Accrued Interest: "  + accruedInterest * 100.);
        double dirtyPrice = price + accruedInterest;
        System.out.println("Dirty Price: " + dirtyPrice);
        double yield = BondValuation.yield(settlement, maturity, coupon, price/100., frequency, DayCountBasis.ACT_ACT);
        System.out.println("Yield: " + yield*100.);
        double durationMacaulay = BondValuation.durationMacaulay(settlement, maturity, coupon, yield, frequency,DayCountBasis.ACT_ACT);
        System.out.println("Macaulay Duration: " + durationMacaulay);
        double durationModified = BondValuation.durationModified(settlement, maturity, coupon, yield, frequency,DayCountBasis.ACT_ACT);
        System.out.println("Modified Duration: " + durationModified);
        double calcPrice = BondValuation.price(settlement, maturity, coupon, yield, frequency,DayCountBasis.ACT_ACT);
        System.out.println("Calculated Clean Price: " + calcPrice * 100.);
        System.out.println("Calculated Dirty Price: " + (calcPrice + accruedInterest/100.)*100.);
        
        CashFlows cf = BondCashFlowGenerator.cashFlowsRPIBondTF(settlement, maturity, 1.0, coupon, frequency, DayCountBasis.ACT_ACT, true);
        List<Date> dates = cf.getDates();
        for(Date date: dates) {
            System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(date) + " : " + cf.getCashFlow(date));
        }
    }
}
