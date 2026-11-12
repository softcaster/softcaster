/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
            mdm = factory.getInstance(token.provider(), null);
            if (mdm != null) {
                node = mdm.getQuote(token.symbol(), market);
                if (node != null) {
                    nodes.put(token.symbol(),node);
                }
            }
        }
        return nodes;
    }
    
    public List<String>  getProviders() {
        return factory.gerProviders();
    }

    public List<String>  getProviders(Market market) {
        List<String> providers = new ArrayList<>();
        RequestParamManager dummy = new RequestParamManager();
        dummy.add("");
        for(String provider: factory.gerProviders()) {
            MarketDataManager mdm = factory.getInstance(provider, dummy);
            if(mdm != null) {
                List<Market> markets = mdm.getMarkets();
                for(Market m: markets) {
                    if(m == market) {
                        providers.add(provider);
                        break;
                    }
                }
            }
        }
        return providers;
    }
}
