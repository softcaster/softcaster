// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import java.sql.Date;
import org.softcaster.commons.generator.IRecord;

public class Bond_registry implements IRecord {

    private Integer id_bond_registry = 0;
    private String isin = "";
    private String cfi_code = "";
    private String fisn = "";
    private String lei = "";
    private Integer issuer = 0;
    private String issue_description = "";
    private Integer currency = 0;
    private Integer calendar = 0;
    private Date issue_date = null;
    private Date maturity_date = null;
    private Integer type_of_interest = 0;
    private Integer form = 0;
    private Integer daycount = 0;
    private Integer coupon_frequency = 0;
    private Integer roll_convention = 0;
    private Integer accrual_schedule_type = 0;
    private Double interest_rate = 0.0;
    private Double issue_price = 0.0;
    private Double redempion_price = 0.0;
    private Double nominal_value = 0.0;
    private Double first_coupon_rate = 0.0;
    private Date first_coupon_payment_date = null;
    private Integer business_days = 0;
    private Double multiplier = 0.01;

    public Integer getId_bond_registry() {
        return id_bond_registry;
    }

    public void setId_bond_registry(Integer id_bond_registry) {
        this.id_bond_registry = id_bond_registry;
    }

    public String getIsin() {
        return isin;
    }

    public void setIsin(String isin) {
        this.isin = isin;
    }

    public String getCfi_code() {
        return cfi_code;
    }

    public void setCfi_code(String cfi_code) {
        this.cfi_code = cfi_code;
    }

    public String getFisn() {
        return fisn;
    }

    public void setFisn(String fisn) {
        this.fisn = fisn;
    }

    public String getLei() {
        return lei;
    }

    public void setLei(String lei) {
        this.lei = lei;
    }

    public Integer getIssuer() {
        return issuer;
    }

    public void setIssuer(Integer issuer) {
        this.issuer = issuer;
    }

    public String getIssue_description() {
        return issue_description;
    }

    public void setIssue_description(String issue_description) {
        this.issue_description = issue_description;
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

    public Integer getCoupon_frequency() {
        return coupon_frequency;
    }

    public void setCoupon_frequency(Integer coupon_frequency) {
        this.coupon_frequency = coupon_frequency;
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

    public Double getNominal_value() {
        return nominal_value;
    }

    public void setNominal_value(Double nominal_value) {
        this.nominal_value = nominal_value;
    }

    public Double getFirst_coupon_rate() {
        return first_coupon_rate;
    }

    public void setFirst_coupon_rate(Double first_coupon_rate) {
        this.first_coupon_rate = first_coupon_rate;
    }

    public Integer getBusiness_days() {
        return business_days;
    }

    public void setBusiness_days(Integer business_days) {
        this.business_days = business_days;
    }

    /**
     * @return the issue_date
     */
    public Date getIssue_date() {
        return issue_date;
    }

    /**
     * @param issue_date the issue_date to set
     */
    public void setIssue_date(Date issue_date) {
        this.issue_date = issue_date;
    }

    /**
     * @return the maturity_date
     */
    public Date getMaturity_date() {
        return maturity_date;
    }

    /**
     * @param maturity_date the maturity_date to set
     */
    public void setMaturity_date(Date maturity_date) {
        this.maturity_date = maturity_date;
    }

    /**
     * @return the first_coupon_payment_date
     */
    public Date getFirst_coupon_payment_date() {
        return first_coupon_payment_date;
    }

    /**
     * @param first_coupon_payment_date the first_coupon_payment_date to set
     */
    public void setFirst_coupon_payment_date(Date first_coupon_payment_date) {
        this.first_coupon_payment_date = first_coupon_payment_date;
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
