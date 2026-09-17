/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.utils;

import java.time.DayOfWeek;

public final class PeakHoursCalculators {

    // EEX Peak Load: lun-ven, ore 08:00-20:00 (escluse le 20:00 stessa),
    // festivi tedeschi esclusi dal peak (contano come base anche in orario diurno)
    public static final PeakHoursCalculator EEX_PEAK = (hour, market) -> {
        DayOfWeek day = hour.getDayOfWeek();
        int h = hour.getHour();
        boolean isWeekday = day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY;
        boolean isPeakWindow = h >= 8 && h < 20;
        boolean isGermanHoliday = false/*GermanHolidayCalendar.isHoliday(hour.toLocalDate())*/;
        return isWeekday && isPeakWindow && !isGermanHoliday;
    };

    private PeakHoursCalculators() {}
}