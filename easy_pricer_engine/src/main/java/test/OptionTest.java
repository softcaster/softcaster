/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import ph.alephzero.finance.DayCountBasis;
import ph.alephzero.finance.products.options.GarmanKohlhagenPricer;
import ph.alephzero.finance.products.options.OptionCalcInputData;
import ph.alephzero.finance.products.options.OptionCalcOutputData;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author ep
 */
// Testato con http://www.finance-calculators.com/fxoptions/
public class OptionTest {
    
    public static void main(String[] args) {
        double s, k, rd, rf, v, t;
        s = 1.25;
        k = 1.25;
        rd = 0.02;
        rf = 0.01;
        v =0.2;
        t = 1;

        OptionCalcInputData input = new OptionCalcInputData();
        input.setSpotPrice(s);
        input.setStrike(k);
        input.setBcyRate(rd);
        input.setCcyRate(rf);
        input.setVolatility(v);
        java.util.Date settlement = DateUtil.createDate(2023,5,3);
        input.setSettlementDate(new java.sql.Date(settlement.getTime()));
        java.util.Date maturity = DateUtil.createDate(2024,5,3);
        input.setMaturityDate(new java.sql.Date(maturity.getTime()));
        input.setDaycount(DayCountBasis.ACT_365);

        GarmanKohlhagenPricer pricer = new GarmanKohlhagenPricer();
        
        OptionCalcOutputData output = pricer.priceCall(input);
        System.out.println(output.getPrice());

        System.out.println();

        output = pricer.pricePut(input);
        System.out.println(output.getPrice());
    }
}
