/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.math;

import java.time.LocalDate;
import org.softcaster.engine.dto.OptionCalcInputData;

/**
 *
 * @author ep
 */
public class OptionUtil {

    public static double getTimeToMaturity(OptionCalcInputData input) {

        LocalDate settlementDate = input.getSettlementDate().toLocalDate();
        LocalDate maturityDate = input.getMaturityDate().toLocalDate();
        return input.getDaycount().calculate(settlementDate, maturityDate, null);
    }
}
