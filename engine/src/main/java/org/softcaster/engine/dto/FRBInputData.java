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
    private CouponProjectionMethod projectionMethod;

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
}
