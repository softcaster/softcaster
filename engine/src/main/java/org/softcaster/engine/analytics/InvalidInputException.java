/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

// Quando i dati in input sono finanziariamente impossibili
public class InvalidInputException extends PricingException {

    public InvalidInputException(String message) {
        super(message);
    }
}
