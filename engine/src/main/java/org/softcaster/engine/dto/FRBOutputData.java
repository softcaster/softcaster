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
}
