/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.util.List;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.cashflows.CashFlowItem;
import ph.alephzero.finance.products.loan.LoanCashFlowGenerator;
import ph.alephzero.finance.products.loan.LoanInfo;

/**
 *
 * @author ep
 */
public class LoanTest {

    public static void main(String[] args) {

        Compounding compounding = Compounding.fromOrdinal(0);
        System.out.println(compounding);

        compounding = Compounding.fromOrdinal(1);
        System.out.println(compounding);

        compounding = Compounding.fromOrdinal(2);
        System.out.println(compounding);

        compounding = Compounding.fromOrdinal(3);
        System.out.println(compounding);

        /*
        org.softcaster.commons.types.Date start = new org.softcaster.commons.types.Date();
        org.softcaster.commons.types.Date end = new org.softcaster.commons.types.Date(start);
        end.addYears(3);
        LoanInfo info = new LoanInfo();
        info.start = start;
        info.end = end;
        info.amount = 100.;
        info.rate = 3.;
        info.nop = 2;

        List<CashFlowItem> cashFlows = LoanCashFlowGenerator.bullet(info);
        for (CashFlowItem item : cashFlows) {
            System.out.println(item.getAmount() + item.getInterest());
        }
         */
    }
}
