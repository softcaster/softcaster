/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author softc
 */
public class FxForwardInputData extends ForwardBaseInputData {
    // Nel Forex domesticRate è il tasso della ccy e
    // foreignRate il tasso della bcy.
    // es nel caso EUR/USD il tasso del EUR (bcy) e' il foreignRate
    // e il tasso dell USD (ccy) il domestic rate
    private double foreignRate;  

    /**
     * @return the foreignRate
     */
    public double getForeignRate() {
        return foreignRate;
    }

    /**
     * @param foreignRate the foreignRate to set
     */
    public void setForeignRate(double foreignRate) {
        this.foreignRate = foreignRate;
    }
}
