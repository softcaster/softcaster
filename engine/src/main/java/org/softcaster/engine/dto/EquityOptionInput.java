/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.Objects;
import org.softcaster.engine.curve.DiscountCurve;

/**
 * Opzione su azione o indice (Black-Scholes-Merton, albero binomiale CRR per le
 * americane).
 *
 * @param spot prezzo a pronti del sottostante
 * @param dividendYield rendimento da dividendo continuo, in DECIMALE (0 se
 * assente)
 * @param discountCurve curva per scontare il payoff e per il tasso privo di
 * rischio
 */
public record EquityOptionInput(
        String instrumentId,
        LocalDate valuationDate,
        double spot,
        double dividendYield,
        DiscountCurve discountCurve,
        OptionContract contract,
        double volatility) implements OptionInput {

    public EquityOptionInput {
        Objects.requireNonNull(discountCurve, "discountCurve");
        if (spot <= 0) {
            throw new IllegalArgumentException("spot deve essere > 0");
        }
        OptionInput.validate(valuationDate, contract, volatility);
    }

    @Override
    public EquityOptionInput withVolatility(double volatility) {
        return new EquityOptionInput(instrumentId, valuationDate, spot, dividendYield,
                discountCurve, contract, volatility);
    }
}
