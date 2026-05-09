/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

public class PricingException extends RuntimeException {

    public PricingException(String message) {
        super(message);
    }

    public PricingException(String message, Throwable cause) {
        super(message, cause);
    }
}
