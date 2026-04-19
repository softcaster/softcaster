/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.easy_pricer_mds.MarketDataNotFoundException;

/**
 *
 * @author softc
 */
public record YieldCurve(
        String currency, // es. "EUR", "USD"
        String curveName, // es. "ESTR", "EURIBOR-6M"
        NavigableMap<org.softcaster.commons.types.Date, Double> rates // Scadenza (1M, 1Y) -> Tasso (0.035)
        ) {

    private static final Comparator<org.softcaster.commons.types.Date> comparator = Comparator
            .comparingInt(org.softcaster.commons.types.Date::getYear)
            .thenComparingInt(org.softcaster.commons.types.Date::getMonth)
            .thenComparingInt(org.softcaster.commons.types.Date::getDay);

    // Costruttore compatto per garantire che i tassi siano ordinati per scadenza
    public YieldCurve {
        // Mappa ordinata per data
        TreeMap<org.softcaster.commons.types.Date, Double> sortedMap = new TreeMap<>(comparator);
        sortedMap.putAll(rates);
        // Rendiamo la mappa navigabile ma non modificabile
        rates = Collections.unmodifiableNavigableMap(sortedMap);
    }

    public Iterator<Double> getRates() {
        return rates.values().iterator();
    }

    public NavigableMap<org.softcaster.commons.types.Date, Double> getRatesMap() {
        return rates;
    }

    public double getRate(Date settlement) {
        // Metodo che ritorna il tasso eventualmente interpolando
        // la scadenza precedente e successiva
        Map.Entry<org.softcaster.commons.types.Date, Double> low = rates.floorEntry(settlement);
        Map.Entry<org.softcaster.commons.types.Date, Double> high = rates.ceilingEntry(settlement);

        if (low == null || high == null) {
            // Richiesta antecedente il primo elemento
            if (low == null && high != null) {
                return high.getValue();
            } // richiesta oltre l' ultimo elemento
            else if (low != null && high == null) {
                return low.getValue();
            } else {
                throw new MarketDataNotFoundException("Date outside the limits of the curve");
            }
        }

        // Logica di interpolazione...
        return interpolate(low, high, settlement);
    }

    private double interpolate(Map.Entry<Date, Double> low, Map.Entry<Date, Double> high, Date settlement) {
        // Richiesta antecedente al primo elemento 
        double yield = 0.;
        double deltaX = high.getKey().days(low.getKey()) / 365.;
        if (!NumberUtils.isZero(deltaX)) {
            double deltaY = (high.getValue() - low.getValue());
            double x = settlement.days(low.getKey()) / 365.;
            yield = low.getValue() + x * (deltaY / deltaX);
        }

        return yield / 100.;
    }
}
