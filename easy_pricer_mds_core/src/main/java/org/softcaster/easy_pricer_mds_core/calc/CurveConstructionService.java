/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.engine.curve.GranularCurvePoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurveConstructionService {

    @Autowired
    private ShapeProfileRepository shapeProfileRepository;

    /**
     * Scompone un prezzo di blocco (market_quote) sui singoli giorni del
     * periodo di delivery, usando i fattori dello shape_profile applicabile.
     */
    public List<GranularCurvePoint> buildGranularCurve(
            MarketQuote blockQuote,        // prezzo del blocco, es. Q1-27 Base = 127.5
            LocalDate deliveryStart,       // dal delivery profile del contratto
            LocalDate deliveryEnd,
            String shapeProfileCode) {     // es. 'EEX_POWER_DE_HISTORICAL_3Y'

        // 1. Carica i fattori di forma applicabili al periodo/mercato/load type
        List<ShapeProfile> factors = shapeProfileRepository.findApplicable(
            shapeProfileCode, blockQuote.getMarket(), blockQuote.getLoadType(),
            deliveryStart, deliveryEnd
        );

        // 2. Calcola il fattore grezzo per ogni giorno del periodo
        Map<LocalDate, Double> rawWeightByDay = new LinkedHashMap<>();
        for (LocalDate day = deliveryStart; !day.isAfter(deliveryEnd); day = day.plusDays(1)) {
            rawWeightByDay.put(day, resolveWeight(factors, day));
        }

        // 3. NORMALIZZAZIONE: la media dei fattori sul periodo deve dare
        //    esattamente 1.0, altrimenti il prezzo medio derivato non torna
        //    al prezzo del blocco quotato (violazione dell'invariante di
        //    non-arbitraggio discusso fin dall'inizio)
        double averageRawWeight = rawWeightByDay.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElseThrow();

        // 4. Applica il fattore normalizzato al prezzo di blocco
        List<GranularCurvePoint> points = new ArrayList<>();
        for (Map.Entry<LocalDate, Double> entry : rawWeightByDay.entrySet()) {
            double normalizedWeight = entry.getValue() / averageRawWeight;
            double dailyPrice = blockQuote.getPrice() * normalizedWeight;

            points.add(new GranularCurvePoint(
                entry.getKey(),
                dailyPrice,
                blockQuote.getId(),
                factors.isEmpty() ? null : factors.get(0).getId() // semplificato, vedi nota sotto
            ));
        }

        return points;
    }

    /**
     * Combina i fattori day-of-week e month-of-year applicabili a un
     * singolo giorno. Se lo shape_profile ha righe granulari diverse
     * (solo dow, solo month, o entrambi), i fattori si moltiplicano.
     */
    private double resolveWeight(List<ShapeProfile> factors, LocalDate day) {
        double weight = 1.0;
        int dow = day.getDayOfWeek().getValue();
        int month = day.getMonthValue();

        for (ShapeProfile f : factors) {
            boolean dowMatches = f.getDayOfWeek() == null || f.getDayOfWeek() == dow;
            boolean monthMatches = f.getMonthOfYear() == null || f.getMonthOfYear() == month;
            if (dowMatches && monthMatches) {
                weight *= f.getWeightFactor();
            }
        }
        return weight;
    }
}