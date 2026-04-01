// File generato automaticamente. Non modificare!

package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;

public class Currency implements IRecord  {

    private Integer id_currency = 0;
    private String iso_code = "";
    private Integer currency_numeric_code = 0;
    private String description = "";
    private Integer minor_unit = 0;
    private Integer system_curr = 0;
    private Integer physical_curr = 0;
    private Integer calendar = 0;
    private Integer daycount = 0;
    private Integer business_days = 0;

    public Integer getId_currency() {
        return id_currency;
    }

    public void setId_currency(Integer id_currency) {
        this.id_currency = id_currency;
    }

    public String getIso_code() {
        return iso_code;
    }

    public void setIso_code(String iso_code) {
        this.iso_code = iso_code;
    }

    public Integer getCurrency_numeric_code() {
        return currency_numeric_code;
    }

    public void setCurrency_numeric_code(Integer currency_numeric_code) {
        this.currency_numeric_code = currency_numeric_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinor_unit() {
        return minor_unit;
    }

    public void setMinor_unit(Integer minor_unit) {
        this.minor_unit = minor_unit;
    }

    public Integer getSystem_curr() {
        return system_curr;
    }

    public void setSystem_curr(Integer system_curr) {
        this.system_curr = system_curr;
    }

    public Integer getPhysical_curr() {
        return physical_curr;
    }

    public void setPhysical_curr(Integer physical_curr) {
        this.physical_curr = physical_curr;
    }

    public Integer getCalendar() {
        return calendar;
    }

    public void setCalendar(Integer calendar) {
        this.calendar = calendar;
    }

    public Integer getBusiness_days() {
        return business_days;
    }

    public void setBusiness_days(Integer business_days) {
        this.business_days = business_days;
    }

    /**
     * @return the daycount
     */
    public Integer getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(Integer daycount) {
        this.daycount = daycount;
    }

}
