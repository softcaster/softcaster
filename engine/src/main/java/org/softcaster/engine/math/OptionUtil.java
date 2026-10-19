/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.math;

import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.enums.DaycountBasis;

/**
 *
 * @author ep
 */
public class OptionUtil {

    public static double getTimeToMaturity(XRBForwardInput input) {
        return DaycountBasis.ACT_360.calculate(input.valuationDate(), input.deliveryDate(), null);
    }
}
