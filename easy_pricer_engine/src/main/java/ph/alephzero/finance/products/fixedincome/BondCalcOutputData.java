/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.fixedincome;

import java.sql.Date;

/**
 *
 * @author ep
 */
public class BondCalcOutputData {

    private double accruedInterest = 0.0;
    private double yieldToMaturity = 0.0;
    private double durationMacaulay = 0.0;
    private double durationModified = 0.0;
    private double convexity = 0.0;
    private double presentValue = 0;
    private String isin = "";
    private double cleanPrice = 0;
    private double yieldToMaturityPV = 0.0;
    private Date maturity = null;

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
     * @return the presentValue
     */
    public double getPresentValue() {
        return presentValue;
    }

    /**
     * @param presentValue the presentValue to set
     */
    public void setPresentValue(double presentValue) {
        this.presentValue = presentValue;
    }

    /**
     * @return the isin
     */
    public String getIsin() {
        return isin;
    }

    /**
     * @param isin the isin to set
     */
    public void setIsin(String isin) {
        this.isin = isin;
    }

    /**
     * @return the cleanPrice
     */
    public double getCleanPrice() {
        return cleanPrice;
    }

    /**
     * @param cleanPrice the cleanPrice to set
     */
    public void setCleanPrice(double cleanPrice) {
        this.cleanPrice = cleanPrice;
    }

    /**
     * @return the yieldToMaturityPV
     */
    public double getYieldToMaturityPV() {
        return yieldToMaturityPV;
    }

    /**
     * @param yieldToMaturityPV the yieldToMaturityPV to set
     */
    public void setYieldToMaturityPV(double yieldToMaturityPV) {
        this.yieldToMaturityPV = yieldToMaturityPV;
    }

    /**
     * @return the maturity
     */
    public Date getMaturity() {
        return maturity;
    }

    /**
     * @param maturity the maturity to set
     */
    public void setMaturity(Date maturity) {
        this.maturity = maturity;
    }

}
