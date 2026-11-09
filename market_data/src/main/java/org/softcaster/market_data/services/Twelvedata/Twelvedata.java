/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Twelvedata;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.core.enums.Market;

public class Twelvedata implements MarketDataManager {

    private TwelvedataApiClient client = null;

    public Twelvedata(RequestParamManager rpm) {
        client = new TwelvedataApiClient();
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        if (!ticker.isEmpty()) {
            String requestSymbol = ticker.substring(0, 3) + "/" + ticker.substring(3, 6);
            return client.getRate(requestSymbol);
        } else {
            return null;
        }
    }

    @Override
    public List<Node> getRates(String curveId) {
        return null;
    }

    @Override
    public String getMarketDataServiceId() {
        return "Twelvedata";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.CURRENCIES);
        return markets;
    }

}
