/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.utils;

import java.math.BigDecimal;

/**
 *
 * @author softc
 */
public class NumberUtils {
    
    public static boolean isZero(double value) {
        BigDecimal _value = BigDecimal.valueOf(value);
        return _value.signum() == 0;
    }
}
