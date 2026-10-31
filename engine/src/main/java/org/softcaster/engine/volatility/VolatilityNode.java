package org.softcaster.engine.volatility;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Punto della griglia di volatilita'.
 *
 * @param strike     null = at the money (usato solo se la scadenza non ha
 *                   punti per strike)
 * @param volatility annua, in decimale
 */
public record VolatilityNode(LocalDate expiry, Double strike, double volatility) {

    public VolatilityNode {
        Objects.requireNonNull(expiry, "expiry");
        if (volatility <= 0) {
            throw new IllegalArgumentException("volatility must be > 0");
        }
        if (strike != null && strike <= 0) {
            throw new IllegalArgumentException("strike must be > 0");
        }
    }
}
