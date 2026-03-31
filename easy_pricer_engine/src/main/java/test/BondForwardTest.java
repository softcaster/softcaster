/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import org.softcaster.commons.types.Date;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.forward.BondForward;
import ph.alephzero.finance.products.forward.BondForwardInputData;
import ph.alephzero.finance.products.forward.BondForwardOutputData;

/**
 *
 * @author ep
 */
public class BondForwardTest {

    public static void main(String[] args) {
        BondForwardInputData input = new BondForwardInputData();
        input.setCompounding(Compounding.COMPOUNDED);
        input.setDaycount(DayCountBasis.ACT_ACT);
        Date settlement = new Date();
        input.setSettlementDate(settlement.sqlDate());
        settlement.addDays(30);
        input.setMaturityDate(settlement.sqlDate());
        input.setRate(0.02);
        input.setSpotPrice(112.5);
        input.setUnderliyngCashFlows(null);
        
        BondForward calculator = new BondForward();
        BondForwardOutputData output = calculator.valuation(input);
        
        System.out.println(output.getTheoreticalPrice());
    }
}
