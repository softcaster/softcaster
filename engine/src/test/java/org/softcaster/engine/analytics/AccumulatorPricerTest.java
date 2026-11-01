package org.softcaster.engine.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.softcaster.engine.dto.AccumulatorFixingInput;
import org.softcaster.engine.dto.AccumulatorInput;
import org.softcaster.engine.dto.AccumulatorPendingSettlement;
import org.softcaster.engine.dto.AccumulatorResult;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.GuaranteeType;
import org.softcaster.engine.enums.KnockInEffect;
import org.softcaster.engine.math.Gaussian;

/**
 * AccumulatorPricer: con la barriera irraggiungibile ogni fixing e' un
 * forward allo strike piu' (leva - 1) opzioni dalla parte sfavorevole, con
 * formula chiusa (Black sul forward al fixing).
 */
class AccumulatorPricerTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 10, 29);
    private static final double SPOT = 1.15;
    private static final double RD = 0.035;  // USD
    private static final double RF = 0.020;  // EUR
    private static final double VOL = 0.08;
    private static final int FIXINGS = 60;
    private static final int PATHS = 200_000;

    private final AccumulatorPricer pricer = new AccumulatorPricer();

    @Test
    void accumulatorWithoutBarrierMatchesAnalytic() {
        AccumulatorInput in = input(AccumulatorType.ACCUMULATOR, 1.10, 100., null, null, 2.);
        AccumulatorResult r = pricer.calculate(in);
        double expected = analytic(in);
        assertEquals(expected, r.value(), 4 * r.standardError(), "MC " + r.value() + " analitico " + expected);
        assertEquals(0., r.knockOutProbability(), 0.);
    }

    @Test
    void decumulatorWithoutBarrierMatchesAnalytic() {
        AccumulatorInput in = input(AccumulatorType.DECUMULATOR, 1.20, 0.01, null, null, 2.);
        AccumulatorResult r = pricer.calculate(in);
        double expected = analytic(in);
        assertEquals(expected, r.value(), 4 * r.standardError(), "MC " + r.value() + " analitico " + expected);
    }

    @Test
    void sameSeedSameResult() {
        AccumulatorInput in = input(AccumulatorType.ACCUMULATOR, 1.10, 1.18, null, null, 2.);
        assertEquals(pricer.calculate(in).value(), pricer.calculate(in).value(), 0.);
    }

    @Test
    void knockOutCapsInTheMoneyAccumulator() {
        AccumulatorResult free = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.10, 100., null, null, 2.));
        AccumulatorResult capped = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.10, 1.18, null, null, 2.));
        assertTrue(capped.value() < free.value());
        assertTrue(capped.knockOutProbability() > 0. && capped.knockOutProbability() < 1.);
        assertTrue(capped.expectedQuantity() < free.expectedQuantity());
        // senza barriera e' una serie di forward comprati: delta positivo, gamma negativo
        // (leva sulle put vendute); vicino al knock-out il delta puo' diventare negativo
        assertTrue(free.delta() > 0. && free.gamma() < 0.);
        assertTrue(capped.delta() < free.delta());
    }

    @Test
    void knockInLeverageLiesBetweenNoLeverageAndFullLeverage() {
        double noLeverage = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.14, 1.20, null, null, 1.)).value();
        double knockIn = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.14, 1.20, 1.10, KnockInEffect.LEVERAGE, 2.)).value();
        double fullLeverage = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.14, 1.20, null, null, 2.)).value();
        assertTrue(fullLeverage < knockIn && knockIn < noLeverage,
                fullLeverage + " < " + knockIn + " < " + noLeverage);
    }

    @Test
    void leveragedGuaranteeOnAllFixingsIgnoresKnockOut() {
        // barriera vicina ma tutti i fixing garantiti: come senza barriera, con la leva
        AccumulatorInput in = input(AccumulatorType.ACCUMULATOR, 1.10, 1.16, null, null, 2., FIXINGS, GuaranteeType.LEVERAGED);
        AccumulatorResult r = pricer.calculate(in);
        assertEquals(analytic(in), r.value(), 4 * r.standardError());
        assertEquals(0., r.knockOutProbability(), 0.);
    }

    @Test
    void unleveragedGuaranteeOnAllFixingsIsAForwardStrip() {
        // quantita' base su ogni fixing: serie di forward allo strike, q (Fs - K) DF
        AccumulatorInput in = input(AccumulatorType.ACCUMULATOR, 1.10, 1.16, null, null, 2., FIXINGS, GuaranteeType.UNLEVERAGED);
        AccumulatorResult r = pricer.calculate(in);
        double expected = in.fixings().stream()
                .mapToDouble(f -> in.dailyQuantity() * (f.settlementForward() - in.strike()) * f.discountFactor())
                .sum();
        assertEquals(expected, r.value(), 4 * r.standardError() + 1e-6);
        assertEquals(FIXINGS * in.dailyQuantity(), r.expectedQuantity(), 1e-6);
    }

    @Test
    void guaranteeRaisesValueOfInTheMoneyAccumulator() {
        double none = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.12, 1.17, null, null, 2.)).value();
        double leveraged = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.12, 1.17, null, null, 2., 10,
                GuaranteeType.LEVERAGED)).value();
        double unleveraged = pricer.calculate(input(AccumulatorType.ACCUMULATOR, 1.12, 1.17, null, null, 2., 10,
                GuaranteeType.UNLEVERAGED)).value();
        assertTrue(none < leveraged && leveraged < unleveraged, none + " < " + leveraged + " < " + unleveraged);
    }

    @Test
    void pendingSettlementIsAForward() {
        AccumulatorInput in = new AccumulatorInput("T", TODAY, AccumulatorType.ACCUMULATOR, 1.10, 1.20,
                BarrierObservation.FIXING, null, null, 2., 100_000., 0, GuaranteeType.NONE, false, false, SPOT,
                List.of(),List.of(new AccumulatorPendingSettlement(1_000_000., 1.16, 0.99)), PATHS, 1L);
        AccumulatorResult r = pricer.calculate(in);
        assertEquals(1_000_000. * 0.06 * 0.99, r.value(), 1e-6);
        assertEquals(0., r.standardError(), 0.);
    }

    /**
     * Fixing nei giorni feriali dal giorno dopo, regolamento a T+2.
     */
    private static AccumulatorInput input(AccumulatorType type, double strike, double ko, Double ki,
            KnockInEffect kiEffect, double leverage) {
        return input(type, strike, ko, ki, kiEffect, leverage, 0, GuaranteeType.NONE);
    }

    private static AccumulatorInput input(AccumulatorType type, double strike, double ko, Double ki,
            KnockInEffect kiEffect, double leverage, int guaranteed, GuaranteeType guaranteeType) {
        List<AccumulatorFixingInput> fixings = new ArrayList<>();
        for (int i = 0; i < FIXINGS; i++) {
            int days = 7 * (i / 5) + i % 5 + 1;
            double t = days / 365.;
            double ts = (days + 2) / 365.;
            fixings.add(new AccumulatorFixingInput(i, t, forward(t), forward(ts), Math.exp(-RD * ts), i % 5 == 4,
                    volatility(t)));
        }
        return new AccumulatorInput("T", TODAY, type, strike, ko, BarrierObservation.FIXING, ki, kiEffect,
                leverage, 100_000., guaranteed, guaranteeType, false, false, SPOT, fixings, List.of(), PATHS, 42L);
    }

    /**
     * Struttura a termine crescente: la formula chiusa usa la volatilita' di
     * ogni fixing, il Monte Carlo gli incrementi di varianza.
     */
    private static double volatility(double t) {
        return VOL + 0.03 * t;
    }

    private static double forward(double t) {
        return SPOT * Math.exp((RD - RF) * t);
    }

    /**
     * Senza barriere: A = E[(S Fs/F - K) 1{S > K}], B = E[(S Fs/F - K) 1{S < K}];
     * accumulator q (A + lev B), decumulator -q (B + lev A), scontati.
     */
    private static double analytic(AccumulatorInput in) {
        double value = 0.;
        double k = in.strike();
        for (AccumulatorFixingInput f : in.fixings()) {
            double sd = f.volatility() * Math.sqrt(f.time());
            double d1 = (Math.log(f.forward() / k) + 0.5 * sd * sd) / sd;
            double d2 = d1 - sd;
            double fs = f.settlementForward();
            double above = fs * Gaussian.cdf(d1) - k * Gaussian.cdf(d2);
            double below = fs * Gaussian.cdf(-d1) - k * Gaussian.cdf(-d2);
            double perUnit = in.type() == AccumulatorType.ACCUMULATOR
                    ? above + in.leverage() * below
                    : -(below + in.leverage() * above);
            value += in.dailyQuantity() * perUnit * f.discountFactor();
        }
        return value;
    }
}
