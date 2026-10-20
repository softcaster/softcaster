/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.Objects;
import org.softcaster.engine.curve.DiscountCurve;

/**
 * Opzione su un prezzo a termine (Black-76): opzioni su future, e su forward
 * (es. opzione su BTP valutata sul prezzo forward calcolato dal
 * BondForwardPricer).
 *
 * @param forward prezzo del future o del forward con scadenza coerente con
 * l'opzione
 * @param discountCurve curva per scontare il payoff alla scadenza dell'opzione
 * @param premiumDiscounted true se il premio si paga subito (opzioni con premio
 * "equity-style"): il prezzo e' scontato con la curva. false per le opzioni su
 * future con margini "futures-style" (premio regolato tramite margini
 * giornalieri): nessuno sconto. Verificare la regola del mercato su cui e'
 * quotata l'opzione.
 */
public record ForwardOptionInput(
        String instrumentId,
        LocalDate valuationDate,
        double forward,
        DiscountCurve discountCurve,
        boolean premiumDiscounted,
        OptionContract contract,
        double volatility) implements OptionInput {

    public ForwardOptionInput {
        Objects.requireNonNull(discountCurve, "discountCurve");
        if (forward <= 0) {
            throw new IllegalArgumentException("forward deve essere > 0");
        }
        OptionInput.validate(valuationDate, contract, volatility);
    }

    @Override
    public ForwardOptionInput withVolatility(double volatility) {
        return new ForwardOptionInput(instrumentId, valuationDate, forward, discountCurve,
                premiumDiscounted, contract, volatility);
    }
}
