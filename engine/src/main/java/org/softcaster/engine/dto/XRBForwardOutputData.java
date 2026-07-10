/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author ep
 */
public class XRBForwardOutputData extends MarketOutputData {
    
    private double netBasis = 0.;

    /**
     * @return the netBasis
     */
    public double getNetBasis() {
        return netBasis;
    }

    /**
     * @param netBasis the netBasis to set
     */
    public void setNetBasis(double netBasis) {
        this.netBasis = netBasis;
    }
}
