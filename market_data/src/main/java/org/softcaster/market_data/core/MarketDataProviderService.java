/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.engine.curve.MarketQuote;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.market_data.core.enums.Market;
import org.springframework.beans.factory.annotation.Autowired;

public class MarketDataProviderService {

    @Autowired
    MarketDataManagerFactory factory;

    public Map<String, Node> getSpotPrices(List<TokenItem> tokens, Market market) {
        MarketDataManager mdm = null;
        Node node = null;
        Map<String, Node> nodes = new HashMap<>();
        for (TokenItem token : tokens) {
            mdm = factory.getInstance(token.provider());
            if (mdm != null) {
                node = mdm.getQuote(token.symbol(), market);
                if (node != null) {
                    nodes.put(token.symbol(), node);
                }
            }
        }
        return nodes;
    }

    public List<MarketQuote> getNewInput(String provider, String curveId) {

        if (provider == null || provider.isBlank()) {
            return null;
        }

        MarketDataManager mdm = factory.getInstance(provider);
        List<Market> markets = mdm.getMarkets();
        boolean hasMarket = false;
        for (Market market : markets) {
            if (market == Market.RATES) {
                hasMarket = true;
                break;
            }
        }
        if (!hasMarket) {
            return null;
        }

        List<MarketQuote> newInput = null;
        List<Node> nodes = mdm.getRates(curveId);
        if (nodes != null && !nodes.isEmpty()) {
            newInput = new ArrayList<>();
            MarketQuote marketQuote;
            for (Node n : nodes) {
                marketQuote = getMarketQuote(n);
                if (marketQuote != null) {
                    newInput.add(marketQuote);
                }
            }
        }
        return newInput;
    }

    public List<String> getProviders() {
        return factory.gerProviders();
    }

    public List<String> getProviders(Market market) {
        List<String> providers = new ArrayList<>();
        for (String provider : factory.gerProviders()) {
            MarketDataManager mdm = factory.getInstance(provider);
            if (mdm != null) {
                List<Market> markets = mdm.getMarkets();
                for (Market m : markets) {
                    if (m == market) {
                        providers.add(provider);
                        break;
                    }
                }
            }
        }
        return providers;
    }

    private MarketQuote getMarketQuote(Node node) {
        MarketQuote marketQuote = null;
        OffsetType offsetType = OffsetType.fromCode(node.offset().offsetType().getCode());
        long step = node.offset().step();
        marketQuote = new MarketQuote(node.symbol(),
                new Offset(step, offsetType),
                node.data().bid(),
                node.daycount(),
                node.compounding(),
                node.nodeType());
        return marketQuote;
    }

}
