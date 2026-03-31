/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.forward.ForexForward;
import ph.alephzero.finance.products.forward.ForexFwdInputData;
import ph.alephzero.finance.products.forward.ForexFwdOutputData;

/**
 *
 * @author ep
 */
public class ForexForwardTest {
    public static void main(String[] args) {
        ForexFwdInputData input = new ForexFwdInputData();
        input.setCompounding(Compounding.SIMPLE);
        input.setDaycount(DayCountBasis.ACT_360);
        Date settlement = new Date();
        input.setSettlementDate(settlement.sqlDate());
        settlement.addDays(30);
        input.setMaturityDate(settlement.sqlDate());
        input.setRate(0.01941);
        input.setRateCcy(0.0375);
        input.setSpotPrice(1.1648);
        
        ForexForward calculator = new ForexForward();
        ForexFwdOutputData output = calculator.valuation(input);
        
        System.out.println(Converter.fromDouble(output.getTheoreticalPrice()));
        System.out.println(Converter.fromDouble(output.getTheoreticalPrice() - input.getSpotPrice()));
    }
    
}
