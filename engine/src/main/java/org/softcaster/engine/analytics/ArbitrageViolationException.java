/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

// Quando un'opzione viola i limiti di arbitraggio (es. prezzo < valore intrinseco)
public class ArbitrageViolationException extends PricingException {

    public ArbitrageViolationException(double price, double intrinsicValue) {
        super(String.format("Market price (%.4f) is below intrinsic value (%.4f)", price, intrinsicValue));
    }
}
