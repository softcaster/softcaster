/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.dto;

import java.io.Serializable;

/**
 *
 * @author softc
 */
public class BondPricingResponse implements Serializable {

    public double accruedInterest = 0.;
    public double yieldToMaturity = 0.;
    public double macaulayDuration = 0.;
    public double modifiedDuration = 0.;
    public double convexity = 0.;
    public double presentValue = 0.;
    public double yieldToMaturityPV = 0.;
}
