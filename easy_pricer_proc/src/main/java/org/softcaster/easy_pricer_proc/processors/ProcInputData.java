/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.engine.enums.TxnSide;

/**
 *
 * @author ep
 */
public class ProcInputData {

    private double quantity = 0;
    // 
    private double price = 0;
    // da prezzo quotato (sulla posizione: market_price) a prezzo unitario:
    // il multiplier dell'anagrafica quando price = prezzo del deal x multiplier
    private double priceMultiplier = 1;
    private TxnSide side = TxnSide.BUY;

    /**
     * @return the quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

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

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    /**
     * @return the side
     */
    public TxnSide getSide() {
        return side;
    }

    /**
     * @param side the side to set
     */
    public void setSide(TxnSide side) {
        this.side = side;
    }

}
