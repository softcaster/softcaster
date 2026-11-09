/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.BorsaItaliana;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.core.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;

/**
 *
 * @author ep
 */
public class BorsaItaliana implements MarketDataManager {

    private BorsaItalianaApiClient client = null;

    public BorsaItaliana(RequestParamManager params) {
        client = new BorsaItalianaApiClient();
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return client.getQuote(ticker, BONDS);
    }

    @Override
    public List<Node> getRates(String curveId) {
        return null;
    }

    @Override
    public String getMarketDataServiceId() {
        return "BorsaItaliana";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.BONDS);
        markets.add(Market.FUTURES);
        return markets;
    }
}
