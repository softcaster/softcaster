/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author softc
 */
public class BondOutputData extends MarketOutputData {

    private double accruedInterest = 0;
    private double ytm = 0;
    private double modifiedDuration = 0;

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
}
