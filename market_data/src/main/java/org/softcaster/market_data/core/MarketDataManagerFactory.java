/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.core;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MarketDataManagerFactory {

    private final Map<String, MarketDataManager> managers;

    // Costruttore per la Dependency Injection
    public MarketDataManagerFactory(Map<String, MarketDataManager> managers) {
        this.managers = managers;
    }

    public MarketDataManager getInstance(String beanName) {
        MarketDataManager manager = managers.get(beanName);

        if (manager == null) {
            throw new IllegalArgumentException(
                    String.format("No MarketDataManager found with name '%s'. Available: %s",
                            beanName, managers.keySet())
            );
        }

        // Dal momento che l'istanza è un Singleton già configurato all'avvio, 
        // l'rpm passato in questo metodo viene ignorato perché l'oggetto esiste già.
        return manager;
    }
}
