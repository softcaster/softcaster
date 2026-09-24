/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import org.softcaster.engine.enums.LoadType;

public class PowerForwardPriceCurve {

    private final LocalDate businessDate;      // when this snapshot was frozen (metadata, not the axis)
    private final String market;               // 'DE'
    private final LoadType loadType;           // BASE/PEAK
    private final ConcurrentSkipListMap<LocalDate, Double> pricesByDeliveryDate = new ConcurrentSkipListMap<>();

    public PowerForwardPriceCurve(LocalDate businessDate, String market, LoadType loadType,
                                    List<GranularCurvePoint> points) {
        this.businessDate = businessDate;
        this.market = market;
        this.loadType = loadType;
        for (GranularCurvePoint p : points) {
            pricesByDeliveryDate.put(p.deliveryDate(), p.price());
        }
    }

    // No interpolation needed: every delivery date is already populated
    // by the upstream bootstrap+shaping process
    public double getPrice(LocalDate deliveryDate) {
        Double price = pricesByDeliveryDate.get(deliveryDate);
        if (price == null) {
            throw new IllegalArgumentException("No price found for delivery date " + deliveryDate
                + " - outside the period covered by this curve");
        }
        return price;
    }

    // The core operation for MTM: average price over a delivery period
    public double getAveragePrice(LocalDate periodStart, LocalDate periodEnd) {
        return pricesByDeliveryDate.subMap(periodStart, true, periodEnd, false)
            .values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElseThrow(() -> new IllegalStateException("No prices found in the requested period"));
    }

    public LocalDate getBusinessDate() { 
        return businessDate; 
    }

    /**
     * @return the market
     */
    public String getMarket() {
        return market;
    }

    /**
     * @return the loadType
     */
    public LoadType getLoadType() {
        return loadType;
    }
}