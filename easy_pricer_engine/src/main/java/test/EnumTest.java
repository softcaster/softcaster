/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import ph.alephzero.finance.DayCountBasis;

/**
 *
 * @author softc
 */
public class EnumTest {

    public static void main(String[] args) {
        System.out.println("ACT_360: " + DayCountBasis.ACT_360.ordinal());
        System.out.println("ACT_365: " + DayCountBasis.ACT_365.ordinal());
        System.out.println("ACT_ACT: " + DayCountBasis.ACT_ACT.ordinal());
        System.out.println("EUR_30_360: " + DayCountBasis.EUR_30_360.ordinal());
        System.out.println("NASD_30_360: " + DayCountBasis.NASD_30_360.ordinal());
    }
}
