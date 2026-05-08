/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.temporal.ChronoUnit;

/**
 *
 * @author ep
 */
public class DayCountCalculators {

    public static final DayCountCalculator ACT_360 = (start, end)
            -> ChronoUnit.DAYS.between(start, end) / 360.0;

    public static final DayCountCalculator ACT_365 = (start, end)
            -> ChronoUnit.DAYS.between(start, end) / 365.0;

    public static final DayCountCalculator NASD_30_360 = (start, end) -> {
        int d1 = Math.min(start.getDayOfMonth(), 30);
        int d2 = (d1 == 30) ? Math.min(end.getDayOfMonth(), 30) : end.getDayOfMonth();
        return ((end.getYear() - start.getYear()) * 360
                + (end.getMonthValue() - start.getMonthValue()) * 30
                + (d2 - d1)) / 360.0;
    };
}
