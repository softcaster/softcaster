/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

public class CurveNodeInput {

    private final MarketQuote marketQuote;
    private double discountFactor;     
    
    public CurveNodeInput(MarketQuote marketQuote) {
        this.marketQuote = marketQuote;
    }
    
    public boolean hasDiscountFactor() { 
        return !Double.isNaN(discountFactor); 
    }

    /**
     * @return the marketQuote
     */
    public MarketQuote getMarketQuote() {
        return marketQuote;
    }

    /**
     * @return the discountFactor
     */
    public double getDiscountFactor() {
        return discountFactor;
    }

    /**
     * @param discountFactor the discountFactor to set
     */
    public void setDiscountFactor(double discountFactor) {
        this.discountFactor = discountFactor;
    }
}

/*
    // Costruttore secondario per quando legge da DB (senza DF)
    public CurveNodeInput(String symbol, Offset tenorOffset, double rate, DaycountBasis daycount, Compounding compounding, CurveNodeType nodeType) {
        this(symbol, tenorOffset, rate, 1.0, daycount, compounding, nodeType); 
    }

    // Metodo Wither: crea una copia esatta aggiornando solo il DF
    public CurveNodeInput withDiscountFactor(double newDiscountFactor) {
        return new CurveNodeInput(this.symbol, this.tenorOffset, this.rate, newDiscountFactor, this.daycount, this.compounding, this.nodeType);
    }
*/    
