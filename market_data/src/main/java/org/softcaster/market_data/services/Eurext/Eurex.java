/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Eurext;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;
import org.softcaster.market_data.core.MarketDataManager;

public class Eurex implements MarketDataManager {

    private EurexApiClient client = null;

    public Eurex() {
        client = new EurexApiClient(""/*this.rpm.get(0)*/);
    }

    @Override
    public Node getQuote(String ticker, Market market) {

        if (client != null) {
            return client.getFutureQuote(ticker);
        }
        else {
            return null;
        }
    }

    @Override
    public List<Node> getRates(String curveId) {
        return null;
    }

    @Override
    public String getMarketDataServiceId() {
        return "Eurex";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.FUTURES);
        markets.add(Market.OPTIONS);
        return markets;
    }
}
