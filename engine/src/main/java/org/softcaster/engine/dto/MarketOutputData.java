/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 *
 * @author ep
 */
public class MarketOutputData {

    // Valido per tutti gli strumenti, prezzo di un bond, tasso fwd
    // di un fx forward, premio di un' opzione
    private double price = 0.;
    private double dv01 = 0.; // dollar value of a basis point
    
    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the dv01
     */
    public double getDv01() {
        return dv01;
    }

    /**
     * @param dv01 the dv01 to set
     */
    public void setDv01(double dv01) {
        this.dv01 = dv01;
    }
}
