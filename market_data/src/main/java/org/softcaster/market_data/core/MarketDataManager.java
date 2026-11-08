/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.market_data.core;

import java.util.List;
import org.softcaster.market_data.core.enums.Market;

public interface MarketDataManager {
    Node getQuote(String ticker, Market market);
    List<Node> getRates(String curveId);
    String getMarketDataServiceId();
    List<Market> getMarkets();
    void addParam(String param);
    String getParam(int pos);
}
