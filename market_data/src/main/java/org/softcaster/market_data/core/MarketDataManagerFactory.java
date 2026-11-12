/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.core;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MarketDataManagerFactory {

    private final Map<String, MarketDataManagerProvider> providers;

    public MarketDataManagerFactory(
            Map<String, MarketDataManagerProvider> providers) {
        this.providers = providers;
    }

    public MarketDataManager getInstance(String beanName, RequestParamManager params) {

        MarketDataManagerProvider provider = providers.get(beanName + "Provider");

        if (provider == null) {
            throw new IllegalArgumentException(
                    "No MarketDataManager provider found: " + beanName);
        }

        return provider.create(params);
    }

    public List<String> gerProviders() {
        return providers.keySet().stream()
                // Il simbolo $ è un carattere speciale delle espressioni regolari (Regex) 
                // che indica la fine esatta della stringa
                .map(key -> key.replaceFirst("Provider$", ""))
                .toList();
    }
}
