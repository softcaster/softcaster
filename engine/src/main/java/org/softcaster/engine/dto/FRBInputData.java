/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import org.softcaster.engine.enums.CouponProjectionMethod;

/**
 *
 * @author softc
 */
public class FRBInputData extends XRBInputData {

    private double redemptionPrice = 0;
    private double referenceRate = 0;
    private CouponProjectionMethod projectionMethod;
    private String projectionCurve = "";
    private String projectionIndex = "";
    /**
     * @return the redemptionPrice
     */
    public double getRedemptionPrice() {
        return redemptionPrice;
    }

    /**
     * @param redemptionPrice the redemptionPrice to set
     */
    public void setRedemptionPrice(double redemptionPrice) {
        this.redemptionPrice = redemptionPrice;
    }

    /**
     * @return the projectionMethod
     */
    public CouponProjectionMethod getProjectionMethod() {
        return projectionMethod;
    }

    /**
     * @param projectionMethod the projectionMethod to set
     */
    public void setProjectionMethod(CouponProjectionMethod projectionMethod) {
        this.projectionMethod = projectionMethod;
    }

    /**
     * @return the referenceRate
     */
    public double getReferenceRate() {
        return referenceRate;
    }

    /**
     * @param referenceRate the referenceRate to set
     */
    public void setReferenceRate(double referenceRate) {
        this.referenceRate = referenceRate;
    }

    /**
     * @return the projectionCurve
     */
    public String getProjectionCurve() {
        return projectionCurve;
    }

    /**
     * @param projectionCurve the projectionCurve to set
     */
    public void setProjectionCurve(String projectionCurve) {
        this.projectionCurve = projectionCurve;
    }

    /**
     * @return the projectionIndex
     */
    public String getProjectionIndex() {
        return projectionIndex;
    }

    /**
     * @param projectionIndex the projectionIndex to set
     */
    public void setProjectionIndex(String projectionIndex) {
        this.projectionIndex = projectionIndex;
    }
}
