/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author softc
 */
public class ForwardBaseOutputData extends MarketOutputData {
    
   private double basis = 0.;

    /**
     * @return the basis
     */
    public double getBasis() {
        return basis;
    }

    /**
     * @param basis the basis to set
     */
    public void setBasis(double basis) {
        this.basis = basis;
    }

}
