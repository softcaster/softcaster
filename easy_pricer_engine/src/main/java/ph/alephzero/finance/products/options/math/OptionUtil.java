/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.options.math;

import ph.alephzero.finance.products.options.OptionCalcInputData;
import ph.alephzero.finance.util.DateUtil;

/**
 *
 * @author ep
 */
public class OptionUtil {
    
    public static double getTimeToMaturity(OptionCalcInputData input) {
        
        java.util.Date dt1 = new java.util.Date(input.getSettlementDate().getTime());
        java.util.Date dt2 = new java.util.Date(input.getMaturityDate().getTime());

        return (DateUtil.diffDaysActual(dt1, dt2)) / input.getDaycount().getTime();
    }
}
