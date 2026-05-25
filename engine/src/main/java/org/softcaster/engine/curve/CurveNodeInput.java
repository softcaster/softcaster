/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;

public record CurveNodeInput(
    String symbol,
    Offset tenorOffset, 
    double rate, 
    double discountFactor, 
    DaycountBasis daycount, 
    Compounding compounding
) {

// Costruttore compatto per quando leggi da DB (senza DF)
    public CurveNodeInput(String symbol, Offset tenorOffset, double rate, DaycountBasis daycount, Compounding compounding) {
        this(symbol, tenorOffset, rate, 1.0, daycount, compounding); // 1.0 o Double.NaN come valore temporaneo
    }

    // Metodo Wither: crea una copia esatta aggiornando solo il DF
    public CurveNodeInput withDiscountFactor(double newDiscountFactor) {
        return new CurveNodeInput(this.symbol, this.tenorOffset, this.rate, newDiscountFactor, this.daycount, this.compounding);
    }
}

