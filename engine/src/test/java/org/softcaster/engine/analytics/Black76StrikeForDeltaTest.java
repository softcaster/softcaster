package org.softcaster.engine.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;

/**
 * Black76Pricer.strikeForCallDelta: lo strike ricavato da un delta ridà quel
 * delta (forward, non scontato).
 */
class Black76StrikeForDeltaTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 16);

    @Test
    void strikeGivesBackTheDelta() {
        double forward = 1.18300323;
        double vol = 0.0946787646;
        LocalDate expiry = TODAY.plusDays(31);
        double t = ChronoUnit.DAYS.between(TODAY, expiry) / 365.;
        for (double delta : new double[]{0.10, 0.25, 0.50, 0.75, 0.90}) {
            double k = Black76Pricer.strikeForCallDelta(forward, vol, t, delta);
            ForwardOptionInput in = new ForwardOptionInput("C", TODAY, forward, noDiscount(), false,
                    new OptionContract(OptionType.CALL, ExerciseStyle.EUROPEAN, k, expiry, 1., List.of()), vol);
            assertEquals(delta, new Black76Pricer().calculate(in).greeks().delta(), 1e-7, "delta " + delta);
        }
    }

    @Test
    void lowCallDeltaIsHighStrike() {
        double t = 30 / 365.;
        assertTrue(Black76Pricer.strikeForCallDelta(1.10, 0.07, t, 0.10) > 1.10);
        assertTrue(Black76Pricer.strikeForCallDelta(1.10, 0.07, t, 0.90) < 1.10);
    }

    @Test
    void cmeQuikStrikeExample() {
        // QuikStrike: MO1Z6 call 1.1075, vol 5.64%, 21 giorni, future 1.10613 -> delta 0.47 (arrotondato)
        double k = Black76Pricer.strikeForCallDelta(1.10613, 0.0564, 21 / 365., 0.47);
        assertEquals(1.1075, k, 0.0005);
    }

    private static DiscountCurve noDiscount() {
        return new DiscountCurve() {
            @Override
            public LocalDate getValuationDate() {
                return TODAY;
            }

            @Override
            public Currency getCurrency() {
                return null;
            }

            @Override
            public int[] getBucketYears() {
                return new int[0];
            }

            @Override
            public double getDiscountFactor(LocalDate date) {
                return 1.0;
            }
        };
    }
}
