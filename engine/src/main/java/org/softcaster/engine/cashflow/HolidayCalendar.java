/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.cashflow;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 *
 * @author ep
 */
public interface HolidayCalendar {
    boolean isHoliday(LocalDate date);
    
    default boolean isBusinessDay(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return false;
        }
        return !isHoliday(date);
    }
}

