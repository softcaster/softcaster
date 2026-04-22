/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import ph.alephzero.finance.DayCountBasis;

/**
 *
 * @author softc
 */
public class DayCountHelper {

    public static DayCountBasis decode(String daycount) {
        DayCountBasis dayCountBasis = null;
        switch (daycount) {
            case "NASD_30_360" ->
                dayCountBasis = DayCountBasis.NASD_30_360;
            case "EUR_30_360" ->
                dayCountBasis = DayCountBasis.EUR_30_360;
            case "ACT_360" ->
                dayCountBasis = DayCountBasis.ACT_360;
            case "ACT_365" ->
                dayCountBasis = DayCountBasis.ACT_365;
            case "ACT_ACT" ->
                dayCountBasis = DayCountBasis.ACT_ACT;
        }
        return dayCountBasis;
    }
}
