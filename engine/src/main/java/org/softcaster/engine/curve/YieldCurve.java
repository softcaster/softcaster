/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.engine.math.MathUtil;

public class YieldCurve {

    private final LocalDate valuationDate; // officialDate
    private final Currency currency;       // La divisa della curva (es. EUR, USD)

    // Struttura interna core: mappa i giorni dal valuationDate al Discount Factor
    // // Struttura thread-safe concorrente e ordinata 
    private final ConcurrentSkipListMap<Integer, CurveNodeInput> discountFactors = new ConcurrentSkipListMap<>();

    /**
     * Costruttore della YieldCurve
     *
     * @param officialDate Data di valutazione a partire dalla quale calcolare
     * le scadenze
     * @param currency Valuta di riferimento della curva
     * @param rawNodes
     */
    public YieldCurve(LocalDate officialDate, Currency currency, List<CurveNodeInput> rawNodes) {
        this.valuationDate = officialDate;
        this.currency = currency;

        // Il giorno 0 (oggi) ha sempre un fattore di sconto pari a 1.0
        CurveNodeInput todayInput = new CurveNodeInput("", new Offset(0, OffsetType.DAYS), 0, 1, DaycountBasis.ACT_365, Compounding.COMPOUNDED);
        this.discountFactors.put(0, todayInput);

        // Costruisci i Discount Factors partendo dagli input
        buildCurve(rawNodes);
    }

    private void buildCurve(List<CurveNodeInput> rawNodes) {
        for (CurveNodeInput node : rawNodes) {
            // 1. Calcola la data effettiva del nodo interpretando la stringa di offset (es. "1 MONTH")
            LocalDate maturityDate = parseTenorOffset(this.valuationDate, node.tenorOffset());

            // 2. Calcola i giorni effettivi assoluti (ACT) rispetto alla data di valutazione
            int days = (int) java.time.temporal.ChronoUnit.DAYS.between(this.valuationDate, maturityDate);

            // 3. Converte il tasso nel rispettivo Discount Factor in base al suo regime
            double t = days / node.daycount().getTime();
            double df = MathUtil.getDiscountFactor(node.compounding(), node.rate(), t);

            CurveNodeInput finalizedNode = node.withDiscountFactor(df);
            this.discountFactors.put(days, finalizedNode);
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

        // 1. Svuota la struttura 
        this.discountFactors.clear();

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
            return discountFactors.get(targetDays).discountFactor();
        }

        Map.Entry<Integer, CurveNodeInput> low = discountFactors.floorEntry(targetDays);
        Map.Entry<Integer, CurveNodeInput> high = discountFactors.ceilingEntry(targetDays);

        if (low == null) {
            return high.getValue().discountFactor();
        }
        if (high == null) {
            return low.getValue().discountFactor();
        }

        int t0 = low.getKey();
        double df0 = low.getValue().discountFactor();
        int t1 = high.getKey();
        double df1 = high.getValue().discountFactor();

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

    public List<OrderedDiscountFactor> getOrderedDiscountFactors() {
        if (valuationDate == null) {
            throw new IllegalArgumentException("La data ufficiale non può essere nulla.");
        }

        // Sfruttiamo lo stream della mappa. Essendo una ConcurrentSkipListMap,
        // l'entrySet() viene già iterato in ordine crescente di giorni (chiave Integer).
        return discountFactors.entrySet().stream()
                .map(entry -> {
                    int daysToAdd = entry.getKey();
                    // Aggiunge i giorni alla data ufficiale di riferimento
                    LocalDate calculatedDate = valuationDate.plusDays(daysToAdd);

                    return new OrderedDiscountFactor(calculatedDate, entry.getValue().discountFactor(), daysToAdd);
                })
                .collect(Collectors.toList());
    }

    public Collection<CurveNodeInput> getAllNodes() {
        // Restituisce una vista non modificabile dei valori della ConcurrentSkipListMap
        return java.util.Collections.unmodifiableCollection(this.discountFactors.values());
    }
}
