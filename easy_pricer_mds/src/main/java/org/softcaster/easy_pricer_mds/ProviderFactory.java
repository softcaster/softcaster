/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.softcaster.marketdataprovider.CmeGroup.CmeGroupProvider;
import org.softcaster.marketdataprovider.IMarketDataProvider;
import org.softcaster.marketdataprovider.euribor.EuriborRatesProvider;
import org.softcaster.marketdataprovider.euronext.EuroNextProvider;
import org.softcaster.marketdataprovider.investingcom.InvestingComProvider;
import org.softcaster.marketdataprovider.sole24h.Sole24hProvider;

/**
 *
 * @author softc
 */
public class ProviderFactory {

    // Mappa che associa una stringa a un costruttore
    private static final Map<String, Supplier<IMarketDataProvider>> REGISTRY = new HashMap<>();

    // Registrazione statica dei tipi disponibili
    static {
        REGISTRY.put("EuroNextProvider", EuroNextProvider::getInstance);
        REGISTRY.put("CmeGroupProvider", CmeGroupProvider::getInstance);
        REGISTRY.put("InvestingComProvider", InvestingComProvider::getInstance);
        REGISTRY.put("Sole24hProvider", Sole24hProvider::getInstance);
        REGISTRY.put("EuriborRatesProvider", EuriborRatesProvider::getInstance);
    }

    // Metodo statico per ottenere l'istanza
    public static IMarketDataProvider getInstance(String provider) {
        if (provider == null) {
            return null;
        }

        Supplier<IMarketDataProvider> supplier = REGISTRY.get(provider);

        if (supplier == null) {
            throw new IllegalArgumentException("Unsupported provider type: " + provider);
        }

        return supplier.get();
    }
}
