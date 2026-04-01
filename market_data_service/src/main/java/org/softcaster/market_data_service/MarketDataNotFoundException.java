/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data_service;

/**
 *
 * @author softc
 */
public class MarketDataNotFoundException extends RuntimeException {

    public MarketDataNotFoundException(String message) {
        super(message);
    }
}
