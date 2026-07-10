/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 *
 * @author softc
 */
public class XRBOutputData extends MarketOutputData {

    private double mktPrice = 0.;
    private double duration = 0.;
    private double ytm = 0.;
    private double modifiedDuration = 0.;
    private double macaulayDuration = 0.;
    private double accruedInterest = 0.;
    private LocalDate valuationDate = null;
    /**
     * @return the accruedInterest
     */
    public double getAccruedInterest() {
        return accruedInterest;
    }

    /**
     * @param accruedInterest the accruedInterest to set
     */
    public void setAccruedInterest(double accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    /**
     * @return the ytm
     */
    public double getYtm() {
        return ytm;
    }

    /**
     * @param ytm the ytm to set
     */
    public void setYtm(double ytm) {
        this.ytm = ytm;
    }

    /**
     * @return the modifiedDuration
     */
    public double getModifiedDuration() {
        return modifiedDuration;
    }

    /**
     * @param modifiedDuration the modifiedDuration to set
     */
    public void setModifiedDuration(double modifiedDuration) {
        this.modifiedDuration = modifiedDuration;
    }

    /**
     * @return the mktPrice
     */
    public double getMktPrice() {
        return mktPrice;
    }

    /**
     * @param mktPrice the mktPrice to set
     */
    public void setMktPrice(double mktPrice) {
        this.mktPrice = mktPrice;
    }

    /**
     * @return the duration
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(double duration) {
        this.duration = duration;
    }

    /**
     * @return the valuationDate
     */
    public LocalDate getValuationDate() {
        return valuationDate;
    }

    /**
     * @param valuationDate the valuationDate to set
     */
    public void setValuationDate(LocalDate valuationDate) {
        this.valuationDate = valuationDate;
    }
    
    /**
     * @return the macaulayDuration
     */
    public double getMacaulayDuration() {
        return macaulayDuration;
    }

    /**
     * @param macaulayDuration the macaulayDuration to set
     */
    public void setMacaulayDuration(double macaulayDuration) {
        this.macaulayDuration = macaulayDuration;
    }
}
