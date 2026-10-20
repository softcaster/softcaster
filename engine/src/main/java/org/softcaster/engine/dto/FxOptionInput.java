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
 * @param domesticCurve curva della valuta domestica: sconta il payoff
 * @param foreignCurve curva della valuta estera: rendimento del sottostante
 */
public record FxOptionInput(
        String instrumentId,
        LocalDate valuationDate,
        double spot,
        DiscountCurve domesticCurve,
        DiscountCurve foreignCurve,
        OptionContract contract,
        double volatility) implements OptionInput {

    public FxOptionInput {
        Objects.requireNonNull(domesticCurve, "domesticCurve");
        Objects.requireNonNull(foreignCurve, "foreignCurve");
        if (spot <= 0) {
            throw new IllegalArgumentException("spot deve essere > 0");
        }
        OptionInput.validate(valuationDate, contract, volatility);
    }

    @Override
    public FxOptionInput withVolatility(double volatility) {
        return new FxOptionInput(instrumentId, valuationDate, spot, domesticCurve,
                foreignCurve, contract, volatility);
    }
}
