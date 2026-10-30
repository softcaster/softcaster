package org.softcaster.engine.volatility;

import java.time.LocalDate;

/**
 * Volatilita' implicita per scadenza e strike, annua in decimale.
 */
public interface VolatilitySurface {

    /**
     * Data da cui si misura il tempo alle scadenze (ACT/365).
     */
    LocalDate getReferenceDate();

    double volatility(LocalDate expiry, double strike);
}
