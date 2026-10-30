package org.softcaster.easy_pricer_mds_core.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Valutazione di un accumulator / decumulator, nella valuta di regolamento e
 * dal punto di vista di chi detiene il contratto (BUY). Per un contratto
 * venduto i valori cambiano segno.
 */
public class AccumulatorPricingResponse implements Serializable {

    public String code;
    public String model;
    public LocalDate valuationDate;
    public double spot;
    // media delle volatilita' dei fixing futuri
    public double volatility;
    // "flat" (indicata nella richiesta) oppure superficie e data di rilevazione
    public String volatilitySource;

    public double value;
    public double standardError;
    // parte deterministica: quantita' maturate da regolare
    public double pendingValue;
    // valore per unita' di quantita' massima: prezzo della transazione a premio equo
    public double unitValue;

    public double maxQuantity;
    public double expectedQuantity;
    public double knockOutProbability;
    public int totalFixings;
    public int futureFixings;

    public double delta;
    public double gamma;
    public double vega;
    public int paths;
}
