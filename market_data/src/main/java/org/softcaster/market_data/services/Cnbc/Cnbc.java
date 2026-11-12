/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Cnbc;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;
import static org.softcaster.market_data.core.enums.Market.CURRENCIES;

public class Cnbc implements MarketDataManager {

    private final CnbcApiClient client;

    public Cnbc() {
        client = new CnbcApiClient();
    }

    private Node getCurrencyQuote(String ticker) {
        try {
            ticker = ticker.replace("USD", "");
            String symbol = ticker + "%3D"/*"%3D%7C"*/;
            double rate = client.getRate(symbol);
            return new Node(ticker,
                    null,
                    null, // DaycountBasis
                    null, // Compounding
                    null, // CurveNodeType
                    new Data(rate, rate));

        } catch (IOException | InterruptedException | ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    private Node getBondQuote(String ticker) {
        try {
            double rate = client.getRate(ticker);
            return new Node(ticker,
                    null,
                    null, // DaycountBasis
                    null, // Compounding
                    null, // CurveNodeType
                    new Data(rate, rate));

        } catch (IOException | InterruptedException | ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }
    
    @Override
    public Node getQuote(String ticker, Market market) {
        return switch (market) {
            case CURRENCIES -> getCurrencyQuote(ticker);
            case BONDS -> getBondQuote(ticker);
            default -> null;
        };
    }

    @Override
    public List<Node> getRates(String curveId) {
        return null;
    }

    @Override
    public String getMarketDataServiceId() {
        return "Cnbc";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.CURRENCIES);
        markets.add(Market.RATES);
        return markets;
    }

}
