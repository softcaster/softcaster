/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.core;

import org.softcaster.market_data.core.enums.Market;

/**
 *
 * @author ep
 */
public record RateKey(String symbol, Market market) {
    
}
