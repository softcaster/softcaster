/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.math;

import org.softcaster.engine.dto.ForwardBaseInputData;

/**
 *
 * @author ep
 */
public class OptionUtil {

    public static double getTimeToMaturity(ForwardBaseInputData input) {
        return input.getDaycount().calculate(input.getValuationDate(), input.getMaturityDate(), null);
    }
}
