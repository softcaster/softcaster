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
            -> ChronoUnit.DAYS.between(start, end) / 360.0;

    public static final DayCountCalculator ACT_365 = (start, end, freq)
            -> ChronoUnit.DAYS.between(start, end) / 365.0;

    public static final DayCountCalculator NASD_30_360 = (start, end, freq) -> {
        int d1 = Math.min(start.getDayOfMonth(), 30);
        int d2 = (d1 == 30) ? Math.min(end.getDayOfMonth(), 30) : end.getDayOfMonth();
        return ((end.getYear() - start.getYear()) * 360
                + (end.getMonthValue() - start.getMonthValue()) * 30
                + (d2 - d1)) / 360.0;
    };

    public static final DayCountCalculator ACT_ACT_ICMA = (start, end, freq) -> {
        if (freq == null || freq.getYearFraction() <= 0) {
            return ChronoUnit.DAYS.between(start, end) / 365.0;
        }

        // 1. Quanti giorni ci sono tra le due date cedolari?
        double actualDays = ChronoUnit.DAYS.between(start, end);

        // 2. Quanti giorni ci sarebbero stati se il periodo fosse stato "pieno"?
        // Se il periodo è già pieno (es. 6 mesi su semestrale), coincide con actualDays.
        // Usiamo la frequenza per determinare la durata del periodo di riferimento.
        long monthsInPeriod = 12 / freq.getYearFraction();
        LocalDate theoreticalStart = end.minusMonths(monthsInPeriod);
        double daysInReferencePeriod = ChronoUnit.DAYS.between(theoreticalStart, end);

        // 3. Formula ICMA:
        // (Giorni Effettivi / Giorni Periodo Riferimento) / Frequenza
            return (actualDays / daysInReferencePeriod) / (double) freq.getYearFraction();
    };
}
