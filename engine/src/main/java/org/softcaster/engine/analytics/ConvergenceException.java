/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

// Quando Newton-Raphson non trova una soluzione
public class ConvergenceException extends PricingException {

    public ConvergenceException(String model, int iterations) {
        super(String.format("%s failed to converge after %d iterations", model, iterations));
    }
}
