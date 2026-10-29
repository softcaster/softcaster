package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 * Risultato di AccumulatorPricer, nella valuta di regolamento e dal punto di
 * vista di chi detiene il contratto.
 *
 * @param value               valore attuale: fixing futuri + quantita' da
 *                            regolare
 * @param standardError       errore standard Monte Carlo di value
 * @param pendingValue        parte di value dovuta alle quantita' gia'
 *                            maturate (deterministica)
 * @param expectedQuantity    quantita' attesa ancora da maturare (leva inclusa)
 * @param knockOutProbability probabilita' di knock-out sui fixing futuri
 * @param delta               dValue/dSpot (unita' di sottostante)
 * @param gamma               d2Value/dSpot2
 * @param vega                variazione di value per 1 punto di volatilita'
 * @param paths               sentieri simulati
 */
public record AccumulatorResult(
        String instrumentId,
        LocalDate valuationDate,
        double value,
        double standardError,
        double pendingValue,
        double expectedQuantity,
        double knockOutProbability,
        double delta,
        double gamma,
        double vega,
        int paths) implements PricingResult {
}
