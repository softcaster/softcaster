/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.calc;

import java.util.List;
import org.softcaster.easy_pricer_core.data.Calendar;
import org.softcaster.easy_pricer_core.data.Holiday;

/**
 *
 * @author ep
 */
public class CalendarHelper {

    private static boolean isHoliday(org.softcaster.commons.types.Date _date, Calendar calendar) {
        List<Holiday> holidays = calendar.getHolidays();
        for (Holiday holiday : holidays) {
            if ((holiday.getHolidayDay() == _date.getDay() && holiday.getHolidayMonth() == _date.getMonth()) || _date.isWeekEnd()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameMonth(java.sql.Date dt1, java.sql.Date dt2) {
        org.softcaster.commons.types.Date _dt1 = new org.softcaster.commons.types.Date(dt1);
        org.softcaster.commons.types.Date _dt2 = new org.softcaster.commons.types.Date(dt2);
        return _dt1.getMonth() == _dt2.getMonth();
    }

    public static java.sql.Date getNextBusinessDate(java.sql.Date currentDate, Calendar calendar, int businessDays) {
        org.softcaster.commons.types.Date _date = new org.softcaster.commons.types.Date(currentDate);
        for (int offset = 1; offset <= businessDays; offset++) {
            _date.addDays(1);
            while (_date.isWeekEnd()) {
                _date.addDays(1);
            }
            while (isHoliday(_date, calendar)) {
                _date.addDays(1);
            }
        }

        return _date.sqlDate();
    }

    public static java.sql.Date getNextBusinessDate(java.sql.Date currentDate, List<Calendar> calendars, int businessDays) {
        org.softcaster.commons.types.Date _date = new org.softcaster.commons.types.Date(currentDate);
        for (int offset = 1; offset <= businessDays; offset++) {
            _date.addDays(1);
            while (_date.isWeekEnd()) {
                _date.addDays(1);
            }
            for (Calendar calendar : calendars) {
                while (isHoliday(_date, calendar)) {
                    _date.addDays(1);
                }
            }
        }

        return _date.sqlDate();
    }

    public static java.sql.Date getPreviousBusinessDate(java.sql.Date currentDate, Calendar calendar, int businessDays) {
        org.softcaster.commons.types.Date _date = new org.softcaster.commons.types.Date(currentDate);
        for (int offset = 1; offset <= businessDays; offset++) {
            _date.addDays(-1);
            while (_date.isWeekEnd()) {
                _date.addDays(-1);
            }
            while (isHoliday(_date, calendar)) {
                _date.addDays(-1);
            }
        }

        return _date.sqlDate();
    }

    public static boolean isNonWorkingDay(java.sql.Date date, Calendar calendar) {
        org.softcaster.commons.types.Date _date = new org.softcaster.commons.types.Date(date);
        return !isHoliday(_date, calendar) && !_date.isWeekEnd();
    }

    public static java.sql.Date adjustDate(java.sql.Date date, Calendar calendar, String rollConvention) {
        if (isNonWorkingDay(date, calendar)) {
            switch (rollConvention) {
                case "PREVIOUS":
                    date = getPreviousBusinessDate(date, calendar, 1);
                    date = adjustDate(date, calendar, rollConvention);
                    break;
                case "FORWARD":
                    date = getNextBusinessDate(date, calendar, 1);
                    date = adjustDate(date, calendar, rollConvention);
                    break;
                case "PREVIOUS_MODIFIED":
                    java.sql.Date newDate = getPreviousBusinessDate(date, calendar, 1);
                    if (!isSameMonth(date, newDate)) {
                        date = adjustDate(getNextBusinessDate(date, calendar, 1), calendar, "FORWARD");
                    } else {
                        date = adjustDate(newDate, calendar, rollConvention);
                    }
                    break;
                case "FORWARD_MODIFIED":
                    newDate = getNextBusinessDate(date, calendar, 1);
                    if (!isSameMonth(date, newDate)) {
                        date = adjustDate(getPreviousBusinessDate(date, calendar, 1), calendar, "PREVIOUS");
                    } else {
                        date = adjustDate(newDate, calendar, rollConvention);
                    }
                    break;
                case "NONE":
                default:
                    break;
            }
        }

        return date;
    }

}
