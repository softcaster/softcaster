/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author softc
 */
public class FixedIncomeOutputData extends MarketOutputData{
    
    private double irr = 0;
    private double accruedInterest = 0;
    private double modifiedDuration = 0;
    private double macaulayDuration = 0;
    private double convexity = 0;
    private double zspread = 0;

    /**
     * @return the irr
     */
    public double getIrr() {
        return irr;
    }

    /**
     * @param irr the irr to set
     */
    public void setIrr(double irr) {
        this.irr = irr;
    }

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

    /**
     * @return the convexity
     */
    public double getConvexity() {
        return convexity;
    }

    /**
     * @param convexity the convexity to set
     */
    public void setConvexity(double convexity) {
        this.convexity = convexity;
    }

    /**
     * @return the zspread
     */
    public double getZspread() {
        return zspread;
    }

    /**
     * @param zspread the zspread to set
     */
    public void setZspread(double zspread) {
        this.zspread = zspread;
    }
}
