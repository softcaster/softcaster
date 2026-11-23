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
import static org.softcaster.market_data.core.enums.Market.FUTURES;

public class Eurex implements MarketDataManager {

    private EurexApiClient client = null;

    public Eurex() {
        client = new EurexApiClient(/*"OESX"*/);
    }

    @Override
    public Node getQuote(String ticker, Market market) {

        // ticker = PRODOTTO@ISIN, es. OESX@DE000F0N5VS5
        String[] parsedSymbol = ticker.split("@");
        if (parsedSymbol.length != 2) {
            return null;
        }
        if (client != null) {
            client.setProduct(parsedSymbol[0]);
            return switch (market) {
                case FUTURES -> client.getFutureQuote(parsedSymbol[1]);
                case OPTIONS -> client.getOptionQuote(parsedSymbol[1]);
                default -> null;
            };
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
