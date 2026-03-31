/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.fixedincome;

import java.io.Serializable;

/**
 *
 * @author ep
 */
public class CalcOutputData implements Serializable {

    private double accruedInterest = 0.0;
    private double yieldToMaturity = 0.0;
    private double durationMacaulay = 0.0;
    private double durationModified = 0.0;

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
     * @return the yieldToMaturity
     */
    public double getYieldToMaturity() {
        return yieldToMaturity;
    }

    /**
     * @param yieldToMaturity the yieldToMaturity to set
     */
    public void setYieldToMaturity(double yieldToMaturity) {
        this.yieldToMaturity = yieldToMaturity;
    }

    /**
     * @return the durationMacaulay
     */
    public double getDurationMacaulay() {
        return durationMacaulay;
    }

    /**
     * @param durationMacaulay the durationMacaulay to set
     */
    public void setDurationMacaulay(double durationMacaulay) {
        this.durationMacaulay = durationMacaulay;
    }

    /**
     * @return the durationModified
     */
    public double getDurationModified() {
        return durationModified;
    }

    /**
     * @param durationModified the durationModified to set
     */
    public void setDurationModified(double durationModified) {
        this.durationModified = durationModified;
    }
}
