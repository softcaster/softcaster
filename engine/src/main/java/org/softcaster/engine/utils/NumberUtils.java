/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.utils;

/**
 *
 * @author softc
 */
public class NumberUtils {
    private static double epsilon = 0.000000001;
    
    public static boolean isZero(double target) {
        return Math.abs(target) < epsilon;
    }
}
