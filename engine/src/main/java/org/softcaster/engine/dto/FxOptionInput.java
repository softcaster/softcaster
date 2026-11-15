/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.Objects;
import org.softcaster.engine.curve.DiscountCurve;

/**
 * Opzione su cambio (Garman-Kohlhagen).
 *
 * Stessa convenzione di FxForwardInput: spot e strike in unita' di valuta
 * domestica (quote) per 1 unita' di valuta estera (base). Es. EURUSD = 1.08:
 * base EUR, domestica USD. Il prezzo dell'opzione e' in valuta domestica per 1
 * unita' di valuta estera.
 *
 * Date della convenzione FX: il premio si paga al regolamento dello spot
 * (spotDate, valuationDate + giorni di regolamento della coppia), il payoff si
 * regola alla consegna (deliveryDate, scadenza + giorni di regolamento). Il
 * forward e' quello alla consegna (FxForwardPricer su spotDate -> deliveryDate)
 * e lo sconto e' tra le stesse date; la volatilita' matura da valuationDate
 * alla scadenza. Senza date (null): spotDate = valuationDate, deliveryDate =
 * scadenza.
 *
 * @param domesticCurve curva della valuta domestica: sconta il payoff
 * @param foreignCurve curva della valuta estera: rendimento del sottostante
 * @param spotDate regolamento dello spot e del premio
 * @param deliveryDate consegna del sottostante all'esercizio
 */
public record FxOptionInput(
        String instrumentId,
        LocalDate valuationDate,
        double spot,
        DiscountCurve domesticCurve,
        DiscountCurve foreignCurve,
        OptionContract contract,
        double volatility,
        LocalDate spotDate,
        LocalDate deliveryDate) implements OptionInput {

    public FxOptionInput {
        Objects.requireNonNull(domesticCurve, "domesticCurve");
        Objects.requireNonNull(foreignCurve, "foreignCurve");
        if (spot <= 0) {
            throw new IllegalArgumentException("spot deve essere > 0");
        }
        OptionInput.validate(valuationDate, contract, volatility);
        spotDate = spotDate != null ? spotDate : valuationDate;
        deliveryDate = deliveryDate != null ? deliveryDate : contract.expiryDate();
        if (deliveryDate.isBefore(spotDate)) {
            throw new IllegalArgumentException("deliveryDate " + deliveryDate + " prima di spotDate " + spotDate);
        }
    }

    /**
     * Senza date di regolamento: sconto e forward dalla valutazione alla
     * scadenza.
     */
    public FxOptionInput(String instrumentId, LocalDate valuationDate, double spot, DiscountCurve domesticCurve,
            DiscountCurve foreignCurve, OptionContract contract, double volatility) {
        this(instrumentId, valuationDate, spot, domesticCurve, foreignCurve, contract, volatility, null, null);
    }

    /**
     * Input del forward FX tra le date di regolamento: lo stesso forward di
     * FxForwardPricer.
     */
    public FxForwardInput forwardInput() {
        return new FxForwardInput(instrumentId, spotDate, deliveryDate, spot, domesticCurve, foreignCurve, null);
    }

    @Override
    public FxOptionInput withVolatility(double volatility) {
        return new FxOptionInput(instrumentId, valuationDate, spot, domesticCurve,
                foreignCurve, contract, volatility, spotDate, deliveryDate);
    }
}
