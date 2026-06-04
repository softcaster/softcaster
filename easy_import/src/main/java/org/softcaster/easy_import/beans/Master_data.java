// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import org.softcaster.commons.generator.IRecord;
import java.sql.Date;

public class Master_data implements IRecord {

    private Integer id_master_data = 0;
    private String code = "";
    private Integer currency = 0;
    private Integer calendar = 0;
    private Date issue_date = null;
    private Date maturity_date = null;
    private Integer type_of_interest = 0;
    private Integer form = 0;
    private Integer daycount = 0;
    private Integer frequency = 0;
    private Integer roll_convention = 0;
    private Integer accrual_schedule_type = 0;
    private Double interest_rate = 0.0;
    private Double issue_price = 0.0;
    private Double redempion_price = 0.0;
    private Integer business_days = 0;
    private Integer asset_class = 0;
    private Integer amortization_schedule = 0;
    private String description = "";
    private Integer accrual_daycount = 0;
    private Double multiplier = 0.0;

    public Integer getId_master_data() {
        return id_master_data;
    }

    public void setId_master_data(Integer id_master_data) {
        this.id_master_data = id_master_data;
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

    public Date getIssue_date() {
        return issue_date;
    }

    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }

    public Date getMaturity_date() {
        return maturity_date;
    }

    public void setMaturity_date(Date maturity_date) {
        this.maturity_date = maturity_date;
    }

    public Integer getType_of_interest() {
        return type_of_interest;
    }

    public void setType_of_interest(Integer type_of_interest) {
        this.type_of_interest = type_of_interest;
    }

    public Integer getForm() {
        return form;
    }

    public void setForm(Integer form) {
        this.form = form;
    }

    public Integer getDaycount() {
        return daycount;
    }

    public void setDaycount(Integer daycount) {
        this.daycount = daycount;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getRoll_convention() {
        return roll_convention;
    }

    public void setRoll_convention(Integer roll_convention) {
        this.roll_convention = roll_convention;
    }

    public Integer getAccrual_schedule_type() {
        return accrual_schedule_type;
    }

    public void setAccrual_schedule_type(Integer accrual_schedule_type) {
        this.accrual_schedule_type = accrual_schedule_type;
    }

    public Double getInterest_rate() {
        return interest_rate;
    }

    public void setInterest_rate(Double interest_rate) {
        this.interest_rate = interest_rate;
    }

    public Double getIssue_price() {
        return issue_price;
    }

    public void setIssue_price(Double issue_price) {
        this.issue_price = issue_price;
    }

    public Double getRedempion_price() {
        return redempion_price;
    }

    public void setRedempion_price(Double redempion_price) {
        this.redempion_price = redempion_price;
    }

    public Integer getBusiness_days() {
        return business_days;
    }

    public void setBusiness_days(Integer business_days) {
        this.business_days = business_days;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the asset_class
     */
    public Integer getAsset_class() {
        return asset_class;
    }

    /**
     * @param asset_class the asset_class to set
     */
    public void setAsset_class(Integer asset_class) {
        this.asset_class = asset_class;
    }

    /**
     * @return the amortization_schedule
     */
    public Integer getAmortization_schedule() {
        return amortization_schedule;
    }

    /**
     * @param amortization_schedule the amortization to set
     */
    public void setAmortization_schedule(Integer amortization_schedule) {
        this.amortization_schedule = amortization_schedule;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the accrual_daycount
     */
    public Integer getAccrual_daycount() {
        return accrual_daycount;
    }

    /**
     * @param accrual_daycount the accrual_daycount to set
     */
    public void setAccrual_daycount(Integer accrual_daycount) {
        this.accrual_daycount = accrual_daycount;
    }

    /**
     * @return the multiplier
     */
    public Double getMultiplier() {
        return multiplier;
    }

    /**
     * @param multiplier the multiplier to set
     */
    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
    }

}
