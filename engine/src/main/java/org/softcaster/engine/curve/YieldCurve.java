/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import org.softcaster.engine.math.MathUtil;

public class YieldCurve {

    private final LocalDate valuationDate; // officialDate
    private final Currency currency;       // La divisa della curva (es. EUR, USD)

    // Struttura interna core: mappa i giorni dal valuationDate al Discount Factor
    // // Struttura thread-safe concorrente e ordinata 
    private final ConcurrentSkipListMap<Integer, Double> discountFactors = new ConcurrentSkipListMap<>();

    /**
     * Costruttore della YieldCurve
     *
     * @param officialDate Data di valutazione a partire dalla quale calcolare
     * le scadenze
     * @param currency Valuta di riferimento della curva
     * @param inputs Lista di nodi (tassi + offset) forniti in input
     */
    public YieldCurve(LocalDate officialDate, Currency currency, List<CurveNodeInput> inputs) {
        this.valuationDate = officialDate;
        this.currency = currency;

        // Il giorno 0 (oggi) ha sempre un fattore di sconto pari a 1.0
        this.discountFactors.put(0, 1.0);

        // Costruisci i Discount Factors partendo dagli input
        buildCurve(inputs);
    }

    private void buildCurve(List<CurveNodeInput> inputs) {
        for (CurveNodeInput node : inputs) {
            // 1. Calcola la data effettiva del nodo interpretando la stringa di offset (es. "1 MONTH")
            LocalDate maturityDate = parseTenorOffset(this.valuationDate, node.tenorOffset());

            // 2. Calcola i giorni effettivi assoluti (ACT) rispetto alla data di valutazione
            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(this.valuationDate, maturityDate);

            // 3. Converti il tasso nel rispettivo Discount Factor in base al suo regime
            double t = days / node.daycount().getTime();
            double df = MathUtil.getDiscountFactor(node.compounding(), node.rate(), t);

            this.discountFactors.put(days, df);
        }
    }

    /**
     * Aggiorna i fattori di sconto della curva sostituendo i vecchi valori con
     * i nuovi input ricevuti dal provider.
     *
     * @param newInputs La nuova lista di nodi aggiornati dal provider
     */
    public synchronized void updateCurve(List<CurveNodeInput> newInputs) {
        if (newInputs == null || newInputs.isEmpty()) {
            throw new IllegalArgumentException("Update failed.");
        }

        // 1. Svuota la struttura mantenendo solo il punto fermo a T=0
        this.discountFactors.clear();
        this.discountFactors.put(0, 1.0);

        // 2. Ricostruisci i fattori di sconto usando la logica esistente
        this.buildCurve(newInputs);
    }

    /**
     * Parsing rudimentale ma efficace dell'offset per calcolare la data futura
     */
    private LocalDate parseTenorOffset(LocalDate baseDate, Offset offset) {

        return switch (offset.offsetType()) {
            case DAYS ->
                baseDate.plusDays(offset.step());
            case MONTHS ->
                baseDate.plusMonths(offset.step());
            case YEARS ->
                baseDate.plusYears(offset.step());
            //case NONE ->{};
            default ->
                throw new IllegalArgumentException("Offset not supported: " + offset.offsetType().getDescription());
        };
    }

    public double getDiscountFactor(LocalDate targetDate) {
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(this.valuationDate, targetDate);
        return getDiscountFactor(days);
    }

    /**
     * Interpola log-linearmente la curva per restituire il Discount Factor a un
     * giorno generico T
     *
     * @param targetDays
     * @return
     */
    public double getDiscountFactor(int targetDays) {
        if (discountFactors.containsKey(targetDays)) {
            return discountFactors.get(targetDays);
        }

        Map.Entry<Integer, Double> low = discountFactors.floorEntry(targetDays);
        Map.Entry<Integer, Double> high = discountFactors.ceilingEntry(targetDays);

        if (low == null) {
            return high.getValue();
        }
        if (high == null) {
            return low.getValue();
        }

        int t0 = low.getKey();
        double df0 = low.getValue();
        int t1 = high.getKey();
        double df1 = high.getValue();

        double weight = (double) (targetDays - t0) / (double) (t1 - t0);
        double logDf = Math.log(df0) + (Math.log(df1) - Math.log(df0)) * weight;

        return Math.exp(logDf);
    }

    // Getters di servizio
    public LocalDate getValuationDate() {
        return valuationDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    /**
     * Calcola il tasso Forward implicito composto continuo (ACT/365) tra due
     * date future.
     *
     * @param startDate Data di inizio del periodo Forward (deve essere >=
     * valuationDate)
     * @param endDate Data di fine del periodo Forward (deve essere > startDate)
     * @return Il tasso Forward continuo annualizzato (es. 0.0365 per il 3.65%)
     */
    public double getContinuousForwardRate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isBefore(this.valuationDate)) {
            throw new IllegalArgumentException("La data di inizio Forward non può essere antecedente alla data di valutazione della curva.");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("La data di fine Forward deve essere successiva alla data di inizio.");
        }

        // 1. Calcola i giorni relativi dalla data di valutazione (T) ai due nodi futuri
        int daysToStart = (int) java.time.temporal.ChronoUnit.DAYS.between(this.valuationDate, startDate);
        int daysToEnd = (int) java.time.temporal.ChronoUnit.DAYS.between(this.valuationDate, endDate);

        // 2. Estrae i rispettivi fattori di sconto (sfruttando l'interpolazione log-lineare interna)
        double dfStart = this.getDiscountFactor(daysToStart);
        double dfEnd = this.getDiscountFactor(daysToEnd);

        // 3. Calcola il tenor (frazione d'anno) del periodo Forward su base ACT/365
        double forwardTenor365 = (double) (daysToEnd - daysToStart) / 365.0;

        // 4. Applica la formula di non arbitraggio dei DF continui: ln(DF_start / DF_end) / Tenor
        return Math.log(dfStart / dfEnd) / forwardTenor365;
    }
}
