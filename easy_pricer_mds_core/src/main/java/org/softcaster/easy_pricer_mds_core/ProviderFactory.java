/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.softcaster.provider.bricks.IMarketDataProvider;
import org.softcaster.provider.cme.CmeGroupProvider;
import org.softcaster.provider.cnbc.CnbcProvider;
import org.softcaster.provider.eex.EexProvider;
import org.softcaster.provider.euronext.EuroNextProvider;
import org.softcaster.provider.sole24h.Sole24hProvider;
import org.softcaster.provider.twelvedata.TwelvedataProvider;
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
        REGISTRY.put("CnbcProvider", CnbcProvider::getInstance);
        REGISTRY.put("Sole24hProvider", Sole24hProvider::getInstance);
        REGISTRY.put("EexProvider", EexProvider::getInstance);
        REGISTRY.put("TwelvedataProvider", TwelvedataProvider::getInstance);
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
