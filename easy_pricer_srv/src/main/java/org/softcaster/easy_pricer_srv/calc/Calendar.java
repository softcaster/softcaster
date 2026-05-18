/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.Holiday;
import org.softcaster.engine.cashflow.HolidayCalendar;

/**
 *
 * @author softc
 */
public class Calendar implements HolidayCalendar {

    private List<Holiday> holidays = null;

    public Calendar(Currency currency) {
        holidays = currency.getCalendar().getHolidays();
    }

    @Override
    public boolean isHoliday(LocalDate date) {

        for (Holiday holiday : holidays) {
            if (holiday.getHolidayDay() == date.getDayOfMonth()
                    && holiday.getHolidayMonth() == date.getMonthValue()) {
                return true;
            }
        }

        return false;
    }

    public LocalDate getNextBusinessDate(java.sql.Date date, int businessDays) {
        return getNextBusinessDate(date.toLocalDate(), businessDays);
    }

    public LocalDate getNextBusinessDate(LocalDate refDate, int businessDays) {
        for (int offset = 1; offset <= businessDays; offset++) {
            refDate = refDate.plusDays(1);
            while (!isBusinessDay(refDate)) {
                refDate = refDate.plusDays(1);
            }
        }
        return refDate;
    }

    public LocalDate getPreviousBusinessDate(java.sql.Date date, int businessDays) {
        return getPreviousBusinessDate(date.toLocalDate(), businessDays);
    }
    
    public LocalDate getPreviousBusinessDate(LocalDate refDate, int businessDays) {
        refDate = refDate.plusDays(-1);
        while (!isBusinessDay(refDate)) {
            refDate = refDate.plusDays(-1);
        }

        return refDate;
    }
}
