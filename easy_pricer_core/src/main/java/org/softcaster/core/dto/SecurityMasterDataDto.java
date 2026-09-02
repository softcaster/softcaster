/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.core.dto;

import java.time.LocalDate;

/**
 *
 * @author ep
 */
public class SecurityMasterDataDto extends MasterDataDto {

    private String shortIssuerName = "";
    private String longIssuerName = "";    
    private String frequency = "";    
    private String country = "";    
    private LocalDate firstCouponPaymentDate;
    private double firstCouponRate;

    /**
     * @return the shortIssuerName
     */
    public String getShortIssuerName() {
        return shortIssuerName;
    }

    /**
     * @param shortIssuerName the shortIssuerName to set
     */
    public void setShortIssuerName(String shortIssuerName) {
        this.shortIssuerName = shortIssuerName;
    }

    /**
     * @return the longIssuerName
     */
    public String getLongIssuerName() {
        return longIssuerName;
    }

    /**
     * @param longIssuerName the longIssuerName to set
     */
    public void setLongIssuerName(String longIssuerName) {
        this.longIssuerName = longIssuerName;
    }

    /**
     * @return the firstCouponPaymentDate
     */
    public LocalDate getFirstCouponPaymentDate() {
        return firstCouponPaymentDate;
    }

    /**
     * @param firstCouponPaymentDate the firstCouponPaymentDate to set
     */
    public void setFirstCouponPaymentDate(LocalDate firstCouponPaymentDate) {
        this.firstCouponPaymentDate = firstCouponPaymentDate;
    }

    /**
     * @return the firstCouponRate
     */
    public double getFirstCouponRate() {
        return firstCouponRate;
    }

    /**
     * @param firstCouponRate the firstCouponRate to set
     */
    public void setFirstCouponRate(double firstCouponRate) {
        this.firstCouponRate = firstCouponRate;
    }

    /**
     * @return the frequency
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

}
