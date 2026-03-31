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
    
    public static boolean isGreaterThan(double value1, double value2) {
        BigDecimal _value1 = BigDecimal.valueOf(value1);
        BigDecimal _value2 = BigDecimal.valueOf(value2);
        return _value1.compareTo(_value2) == 1;
    }
    
    public static <T extends Number> boolean isOdd(T number) {
        // L'operatore modulo (%) restituisce il resto della divisione
        // Usiamo longValue() per rendere il calcolo compatibile con tutti i Number
        return number.longValue() % 2 != 0;
    }
}
