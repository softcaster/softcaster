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
import org.softcaster.engine.dto.FxOptionInput;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.math.Gaussian;

/**
 * Garman-Kohlhagen: senza date di regolamento la formula classica sulla
 * scadenza; con regolamento dello spot e consegna, il forward di
 * FxForwardPricer e lo sconto tra le stesse date (Black-76 sul forward).
 */
class GarmanKohlhagenPricerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 13);
    private static final LocalDate SPOT_DATE = LocalDate.of(2026, 11, 17);
    private static final LocalDate EXPIRY = LocalDate.of(2027, 2, 12);
    private static final LocalDate DELIVERY = LocalDate.of(2027, 2, 16);
    private static final double SPOT = 1.1023;
    private static final double STRIKE = 1.12;
    private static final double VOL = 0.08;
    private static final DiscountCurve USD = flat(0.040);
    private static final DiscountCurve EUR = flat(0.020);

    private final GarmanKohlhagenPricer pricer = new GarmanKohlhagenPricer();

    @Test
    void withoutSettlementDatesMatchesClassicFormula() {
        double t = ChronoUnit.DAYS.between(TODAY, EXPIRY) / 365.0;
        double dfd = Math.exp(-0.040 * t);
        double dff = Math.exp(-0.020 * t);
        double f = SPOT * dff / dfd;
        double d1 = (Math.log(f / STRIKE) + 0.5 * VOL * VOL * t) / (VOL * Math.sqrt(t));
        double d2 = d1 - VOL * Math.sqrt(t);
        double expected = SPOT * dff * Gaussian.cdf(d1) - STRIKE * dfd * Gaussian.cdf(d2);

        OptionResult r = pricer.calculate(new FxOptionInput("C", TODAY, SPOT, USD, EUR, contract(OptionType.CALL), VOL));
        assertEquals(expected, r.price(), 1e-12);
    }

    @Test
    void withSettlementDatesUsesForwardToDelivery() {
        FxOptionInput call = input(OptionType.CALL);
        OptionResult r = pricer.calculate(call);

        double forward = new FxForwardPricer().forwardRate(call.forwardInput());
        assertEquals(forward, r.forward(), 1e-12);

        // Black-76 sul forward alla consegna, scontato tra regolamento spot e consegna
        double dfd = USD.getDiscountFactor(DELIVERY) / USD.getDiscountFactor(SPOT_DATE);
        double t = ChronoUnit.DAYS.between(TODAY, EXPIRY) / 365.0;
        double d1 = (Math.log(forward / STRIKE) + 0.5 * VOL * VOL * t) / (VOL * Math.sqrt(t));
        double d2 = d1 - VOL * Math.sqrt(t);
        assertEquals(dfd * (forward * Gaussian.cdf(d1) - STRIKE * Gaussian.cdf(d2)), r.price(), 1e-12);
    }

    @Test
    void putCallParityOnForward() {
        OptionResult call = pricer.calculate(input(OptionType.CALL));
        OptionResult put = pricer.calculate(input(OptionType.PUT));
        double dfd = USD.getDiscountFactor(DELIVERY) / USD.getDiscountFactor(SPOT_DATE);
        assertEquals(dfd * (call.forward() - STRIKE), call.price() - put.price(), 1e-12);
    }

    @Test
    void binomialEuropeanConvergesToGarmanKohlhagen() {
        FxOptionInput call = input(OptionType.CALL);
        double gk = pricer.calculate(call).price();
        double crr = new CRRBinomialPricer(2000).calculate(call).price();
        assertEquals(gk, crr, 2e-5);
    }

    @Test
    void settlementDatesChangeThePriceSlightly() {
        double classic = pricer.calculate(new FxOptionInput("C", TODAY, SPOT, USD, EUR, contract(OptionType.CALL), VOL)).price();
        double withDates = pricer.calculate(input(OptionType.CALL)).price();
        assertTrue(Math.abs(withDates - classic) < 1e-4, "classic " + classic + " with dates " + withDates);
    }

    @Test
    void blackOnFutureWithSameForwardGivesSamePrice() {
        FxOptionInput call = input(OptionType.CALL);
        OptionResult gk = pricer.calculate(call);
        // Black-76 con lo stesso forward e lo stesso sconto: sconto fino alla consegna dal regolamento spot
        DiscountCurve shifted = new ShiftedCurve(USD, SPOT_DATE, DELIVERY, EXPIRY);
        OptionResult black = new Black76Pricer().calculate(new ForwardOptionInput("C", TODAY, gk.forward(), shifted, true,
                contract(OptionType.CALL), VOL));
        assertEquals(gk.price(), black.price(), 1e-12);
    }

    // ------------------------------------------------------------------------

    private static FxOptionInput input(OptionType type) {
        return new FxOptionInput("C", TODAY, SPOT, USD, EUR, contract(type), VOL, SPOT_DATE, DELIVERY);
    }

    private static OptionContract contract(OptionType type) {
        return new OptionContract(type, ExerciseStyle.EUROPEAN, STRIKE, EXPIRY, 1., List.of());
    }

    private static DiscountCurve flat(double rate) {
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
                return Math.exp(-rate * ChronoUnit.DAYS.between(TODAY, date) / 365.0);
            }
        };
    }

    /**
     * Curva che tra la valutazione e la scadenza restituisce lo sconto della
     * curva base tra regolamento spot e consegna: Black-76 sconta da valutazione
     * a scadenza.
     */
    private record ShiftedCurve(DiscountCurve base, LocalDate spotDate, LocalDate delivery, LocalDate expiry)
            implements DiscountCurve {

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
            if (date.equals(expiry)) {
                return base.getDiscountFactor(delivery) / base.getDiscountFactor(spotDate);
            }
            return 1.0;
        }
    }
}
