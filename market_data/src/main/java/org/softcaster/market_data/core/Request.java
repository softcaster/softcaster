/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.market_data.core;

import org.softcaster.market_data.core.enums.Market;

public record Request(String url, Market market) {

}

