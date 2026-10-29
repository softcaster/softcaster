package org.softcaster.engine.volatility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class GridVolatilitySurfaceTest {

    private static final LocalDate REF = LocalDate.of(2026, 10, 29);
    private static final LocalDate E1 = REF.plusDays(73);   // t1 = 0.2
    private static final LocalDate E2 = REF.plusDays(365);  // t2 = 1.0

    @Test
    void atmTermStructureInterpolatesTotalVariance() {
        GridVolatilitySurface s = new GridVolatilitySurface(REF, List.of(
                new VolatilityNode(E1, null, 0.10), new VolatilityNode(E2, null, 0.20)));
        // t = 0.6: w = 0.01*0.2 + (0.04 - 0.002) * 0.4 / 0.8 = 0.021, vol = sqrt(0.021 / 0.6)
        double expected = Math.sqrt(0.021 / 0.6);
        assertEquals(expected, s.volatility(REF.plusDays(219), 1.1), 1e-12);
        // fuori dalla griglia: smile piu' vicino, indipendente dallo strike
        assertEquals(0.10, s.volatility(REF.plusDays(10), 0.5), 0.);
        assertEquals(0.20, s.volatility(REF.plusDays(800), 2.0), 0.);
    }

    @Test
    void smileIsLinearInStrikeAndFlatOutside() {
        GridVolatilitySurface s = new GridVolatilitySurface(REF, List.of(
                new VolatilityNode(E1, 1.00, 0.12), new VolatilityNode(E1, 1.20, 0.08),
                new VolatilityNode(E1, null, 0.50)));   // ATM ignorato: ci sono gli strike
        assertEquals(0.10, s.volatility(E1, 1.10), 1e-12);
        assertEquals(0.12, s.volatility(E1, 0.90), 0.);
        assertEquals(0.08, s.volatility(E1, 1.50), 0.);
    }

    @Test
    void expiredPointsAreDropped() {
        assertThrows(IllegalArgumentException.class, () -> new GridVolatilitySurface(REF,
                List.of(new VolatilityNode(REF, null, 0.10))));
    }
}
