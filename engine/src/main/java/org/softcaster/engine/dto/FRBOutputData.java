/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author softc
 */
public class FRBOutputData extends XRBOutputData {
    private double shortBondYield = 0.;
    private double discountMargin = 0;
    private double marginDuration = 0;
    
    /**
     * @return the shortBondYield
     */
    public double getShortBondYield() {
        return shortBondYield;
    }

    /**
     * @param shortBondYield the shortBondYield to set
     */
    public void setShortBondYield(double shortBondYield) {
        this.shortBondYield = shortBondYield;
    }

    /**
     * @return the discountMargin
     */
    public double getDiscountMargin() {
        return discountMargin;
    }

    /**
     * @param discountMargin the discountMargin to set
     */
    public void setDiscountMargin(double discountMargin) {
        this.discountMargin = discountMargin;
    }

    /**
     * @return the marginDuration
     */
    public double getMarginDuration() {
        return marginDuration;
    }

    /**
     * @param marginDuration the marginDuration to set
     */
    public void setMarginDuration(double marginDuration) {
        this.marginDuration = marginDuration;
    }
}
