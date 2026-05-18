/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package org.softcaster.engine.enums;

import java.time.LocalDate;
import org.softcaster.engine.cashflow.HolidayCalendar;

/**
 *
 * @author ep
 */
public enum BusinessDayConvention implements IdentifiableEnum {
    PREVIOUS(1, "PREVIOUS", "Previus"),
    PREVIOUS_MODIFIED(2, "PREVIOUS-MODIFIED", "Previus Following"),
    FORWARD(3, "FORWARD", "Following"),
    FORWARD_MODIFIED(4, "FORWARD-MODIFIED", "Modified Following"),
    UNADJUSTED(100, "UNADJUSTED", "Unadjusted");

    private final int id;
    private final String code;
    private final String description;

    BusinessDayConvention(int id, String code, String description) {
        this.id = id;
        this.code = code;
        this.description = description;
    }
    
    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @return the code
     */
    @Override
    public String getCode() {
        return code;
    }

    /**
     * @return the description
     */
    @Override
    public String getDescription() {
        return description;
    }
/**
     * Applica la convenzione a una data usando un calendario specifico
     * @param date
     * @param calendar
     * @return 
     */
    public LocalDate adjust(LocalDate date, HolidayCalendar calendar) {
        if (calendar == null || this == UNADJUSTED || calendar.isBusinessDay(date)) {
            return date;
        }

        LocalDate adjusted = date;
        switch (this) {
            case FORWARD -> {
                while (!calendar.isBusinessDay(adjusted)) adjusted = adjusted.plusDays(1);
            }
            case PREVIOUS -> {
                while (!calendar.isBusinessDay(adjusted)) adjusted = adjusted.minusDays(1);
            }
            case FORWARD_MODIFIED -> {
                adjusted = FORWARD.adjust(date, calendar);
                if (adjusted.getMonth() != date.getMonth()) {
                    adjusted = PREVIOUS.adjust(date, calendar);
                }
            }
        }
        return adjusted;
    }
}
