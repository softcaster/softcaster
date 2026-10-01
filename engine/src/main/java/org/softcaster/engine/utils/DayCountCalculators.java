/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author ep
 */
public class DayCountCalculators {

    public static final DayCountCalculator ACT_360 = (start, end, freq)
            -> {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date " + start + " is after end date " + end);
        }
        return ChronoUnit.DAYS.between(start, end) / 360.0;
    };

    public static final DayCountCalculator ACT_365 = (start, end, freq)
            -> {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date " + start + " is after end date " + end);
        }
        return ChronoUnit.DAYS.between(start, end) / 365.0;
    };

    public static final DayCountCalculator NASD_30_360 = (start, end, freq) -> {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date " + start + " is after end date " + end);
        }
        int d1 = Math.min(start.getDayOfMonth(), 30);
        int d2 = (d1 == 30) ? Math.min(end.getDayOfMonth(), 30) : end.getDayOfMonth();
        return ((end.getYear() - start.getYear()) * 360
                + (end.getMonthValue() - start.getMonthValue()) * 30
                + (d2 - d1)) / 360.0;
    };

    public static final DayCountCalculator ACT_ACT_ICMA = (start, end, freq) -> {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date " + start + " is after end date " + end);
        }
        if (freq == null || freq.getYearFraction() <= 0) {
            return ChronoUnit.DAYS.between(start, end) / 365.0;
        }
        int perYear = (int) freq.getYearFraction();          // adjust if it means something else
        int months = 12 / perYear;

        int whole = 0;
        // whole periods counted backwards from end (anchored on 'end' to avoid month-end drift)
        while (!end.minusMonths((long) (whole + 1) * months).isBefore(start)) {
            whole++;
        }
        LocalDate stubEnd = end.minusMonths((long) whole * months);
        if (stubEnd.isEqual(start)) {
            return whole / (double) perYear;
        }
        LocalDate refStart = end.minusMonths((long) (whole + 1) * months);
        double stub = (double) ChronoUnit.DAYS.between(start, stubEnd)
                / ChronoUnit.DAYS.between(refStart, stubEnd);
        return (whole + stub) / perYear;
    };

    public static final DayCountCalculator EUR_30_360 = (start, end, freq) -> {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date " + start + " is after end date " + end);
        }
        int d1 = Math.min(start.getDayOfMonth(), 30);
        int d2 = Math.min(end.getDayOfMonth(), 30);
        return ((end.getYear() - start.getYear()) * 360
                + (end.getMonthValue() - start.getMonthValue()) * 30
                + (d2 - d1)) / 360.0;
    };

    public static final DayCountCalculator ACT_ACT_ISDA = (start, end, freq) -> {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date " + start + " is after end date " + end);
        }
        int y1 = start.getYear();
        int y2 = end.getYear();

        // same calendar year: actual days / days in that year
        if (y1 == y2) {
            return ChronoUnit.DAYS.between(start, end) / (double) start.lengthOfYear();
        }

        // portion in the first year, whole years in between, portion in the last year
        double first = ChronoUnit.DAYS.between(start, LocalDate.of(y1 + 1, 1, 1)) / (double) start.lengthOfYear();
        double last = ChronoUnit.DAYS.between(LocalDate.of(y2, 1, 1), end) / (double) end.lengthOfYear();
        return first + (y2 - y1 - 1) + last;
    };
}
