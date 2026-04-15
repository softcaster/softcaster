/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data_service.model;

/**
 *
 * @author softc
 */
public record SpotPrice(String symbol, double bid, double ask, double middle) {

    // Validazione finanziaria
    public SpotPrice    {
        if (bid < 0 || ask < 0 || middle < 0) {
            throw new IllegalArgumentException("The spot price cannot be negative");
        }
        if (symbol == null || symbol.isBlank()) {
            throw new IllegalArgumentException("Symbol is mandatory");
        }
    }
}
// uso: SpotPrice newPrice = new SpotPrice("USD/JPY", 150.10);
