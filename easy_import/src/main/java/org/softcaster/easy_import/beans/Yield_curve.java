// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;

public class Yield_curve implements IRecord {

    private Integer id_yield_curve = 0;
    private String code = "";
    private String description = "";
    private Integer currency = 0;
    private Integer calendar = 0;
    private Integer compounding = 0;

    public Integer getId_yield_curve() {
        return id_yield_curve;
    }

    public void setId_yield_curve(Integer id_yield_curve) {
        this.id_yield_curve = id_yield_curve;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCurrency() {
        return currency;
    }

    public void setCurrency(Integer currency) {
        this.currency = currency;
    }

    public Integer getCalendar() {
        return calendar;
    }

    public void setCalendar(Integer calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the compounding
     */
    public Integer getCompounding() {
        return compounding;
    }

    /**
     * @param compounding the compounding to set
     */
    public void setCompounding(Integer compounding) {
        this.compounding = compounding;
    }

}
