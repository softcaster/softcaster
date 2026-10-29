package org.softcaster.engine.analytics;

import java.util.List;
import java.util.SplittableRandom;
import java.util.stream.IntStream;
import org.softcaster.engine.dto.AccumulatorFixingInput;
import org.softcaster.engine.dto.AccumulatorInput;
import org.softcaster.engine.dto.AccumulatorPendingSettlement;
import org.softcaster.engine.dto.AccumulatorResult;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.KnockInEffect;

/**
 * Accumulator / decumulator con Monte Carlo lognormale (Black / Garman-
 * Kohlhagen con struttura a termine della volatilita') sui forward forniti
 * dal chiamante.
 *
 * Sottostante al fixing i: S_i = F_i exp(X_i - V_i / 2), con V_i = sigma_i^2
 * t_i la varianza totale alla scadenza del fixing e X un moto browniano nel
 * tempo-varianza, costruito per incrementi V_i - V_(i-1) (sentieri
 * antitetici; un incremento negativo, arbitraggio di calendario nei dati,
 * vale 0). A ogni fixing, nell'ordine:
 * 1. knock-out (dal fixing guaranteedFixings in poi; alle sole fine periodo
 *    se osservato al regolamento; in continuo con la correzione di Broadie-
 *    Glasserman-Kou sulla barriera): il contratto termina, maturando il fixing
 *    corrente solo se koAccrual;
 * 2. knock-in (osservato al fixing);
 * 3. maturazione: q = dailyQuantity nei fixing favorevoli, dailyQuantity x
 *    leverage negli sfavorevoli (leva 1 prima del knock-in LEVERAGE, nessuna
 *    maturazione prima del knock-in CONTRACT). La quantita' si regola allo
 *    strike alla data di regolamento del periodo: vale
 *    sign q (S_i Fs_i / F_i - K) DF_i, con Fs_i/F_i il passaggio dal forward
 *    al fixing a quello al regolamento.
 *
 * Le quantita' gia' maturate e non regolate valgono sign q (Fs - K) DF.
 *
 * Riproducibile: ogni coppia di sentieri ha un generatore con seme derivato
 * da (seed, indice), indipendente dai thread. Le greche usano gli stessi
 * numeri casuali (spot +-1%, volatilita' di tutti i fixing +-1 punto).
 */
public final class AccumulatorPricer implements Pricer<AccumulatorInput, AccumulatorResult> {

    // correzione di Broadie-Glasserman-Kou per barriere continue osservate in modo discreto
    private static final double BGK = 0.5826;
    private static final double SPOT_BUMP = 0.01;
    private static final double VOL_BUMP = 0.01;

    @Override
    public AccumulatorResult calculate(AccumulatorInput in) {
        Simulation base = simulate(in, 1.0, 0.);
        double h = SPOT_BUMP * in.spot();
        Simulation up = simulate(in, 1.0 + SPOT_BUMP, 0.);
        Simulation down = simulate(in, 1.0 - SPOT_BUMP, 0.);
        double delta = (up.value - down.value) / (2 * h);
        double gamma = (up.value - 2 * base.value + down.value) / (h * h);

        double vega;
        Simulation volUp = simulate(in, 1.0, VOL_BUMP);
        double minVol = in.fixings().stream().mapToDouble(AccumulatorFixingInput::volatility).min().orElse(0.);
        if (minVol > VOL_BUMP) {
            Simulation volDown = simulate(in, 1.0, -VOL_BUMP);
            vega = (volUp.value - volDown.value) / 2;
        } else {
            vega = volUp.value - base.value;
        }

        return new AccumulatorResult(in.instrumentId(), in.valuationDate(), base.value, base.standardError,
                base.pendingValue, base.expectedQuantity, base.knockOutProbability, delta, gamma, vega,
                in.fixings().isEmpty() ? 0 : 2 * pairs(in));
    }

    private record Simulation(double value, double standardError, double pendingValue,
            double expectedQuantity, double knockOutProbability) {
    }

    /**
     * Valore con forward moltiplicati per scale e volatilita' dei fixing
     * spostate di volShift.
     */
    private static Simulation simulate(AccumulatorInput in, double scale, double volShift) {
        int sign = in.type().getSign();
        double pendingValue = 0.;
        for (AccumulatorPendingSettlement p : in.pending()) {
            pendingValue += sign * p.quantity() * (p.settlementForward() * scale - in.strike()) * p.discountFactor();
        }
        List<AccumulatorFixingInput> fixings = in.fixings();
        if (fixings.isEmpty()) {
            return new Simulation(pendingValue, 0., pendingValue, 0., 0.);
        }

        Path path = new Path(in, scale, volShift);
        int pairs = pairs(in);
        // per coppia: media antitetica del valore, quantita' e knock-out
        double[] sums = IntStream.range(0, pairs).parallel()
                .mapToObj(p -> path.pair(p))
                .reduce(new double[4], AccumulatorPricer::add);

        double mean = sums[0] / pairs;
        double variance = Math.max(sums[1] / pairs - mean * mean, 0.);
        double standardError = Math.sqrt(variance / pairs);
        return new Simulation(pendingValue + mean, standardError, pendingValue,
                sums[2] / pairs, sums[3] / pairs);
    }

    private static int pairs(AccumulatorInput in) {
        return (in.paths() + 1) / 2;
    }

    private static double[] add(double[] a, double[] b) {
        return new double[]{a[0] + b[0], a[1] + b[1], a[2] + b[2], a[3] + b[3]};
    }

    /**
     * Dati per fixing precalcolati, condivisi dai thread in sola lettura.
     */
    private static final class Path {

        private final AccumulatorInput in;
        private final AccumulatorType type;
        private final double[] lnForward;
        // deviazione standard dell'incremento di X tra due fixing
        private final double[] sqrtDv;
        private final double[] koLevel;
        private final double[] settlementRatio;
        private final double[] discountFactor;
        private final boolean[] koObserved;

        Path(AccumulatorInput in, double scale, double volShift) {
            this.in = in;
            this.type = in.type();
            List<AccumulatorFixingInput> fixings = in.fixings();
            int n = fixings.size();
            lnForward = new double[n];
            sqrtDv = new double[n];
            koLevel = new double[n];
            settlementRatio = new double[n];
            discountFactor = new double[n];
            koObserved = new boolean[n];
            double variance = 0.;
            for (int i = 0; i < n; i++) {
                AccumulatorFixingInput f = fixings.get(i);
                double vol = Math.max(f.volatility() + volShift, 0.);
                // varianza totale non decrescente: X resta un moto browniano
                double total = Math.max(vol * vol * f.time(), variance);
                sqrtDv[i] = Math.sqrt(total - variance);
                variance = total;
                lnForward[i] = Math.log(f.forward() * scale) - 0.5 * variance;
                // in continuo la barriera si avvicina allo spot: piu' probabile toccarla
                koLevel[i] = in.koObservation() == BarrierObservation.CONTINUOUS
                        ? in.koBarrier() * Math.exp(-type.getSign() * BGK * sqrtDv[i])
                        : in.koBarrier();
                settlementRatio[i] = f.settlementForward() / f.forward();
                discountFactor[i] = f.discountFactor();
                koObserved[i] = f.index() >= in.guaranteedFixings()
                        && (in.koObservation() != BarrierObservation.SETTLEMENT || f.periodEnd());
            }
        }

        /**
         * Coppia antitetica p: {valore medio, valore medio^2, quantita' media,
         * knock-out medio}.
         */
        double[] pair(int p) {
            SplittableRandom rng = new SplittableRandom(in.seed() ^ (p * 0x9E3779B97F4A7C15L));
            double[] z = new double[lnForward.length];
            for (int i = 0; i < z.length; i++) {
                z[i] = rng.nextGaussian();
            }
            double[] a = run(z, 1.);
            double[] b = run(z, -1.);
            double value = 0.5 * (a[0] + b[0]);
            return new double[]{value, value * value, 0.5 * (a[1] + b[1]), 0.5 * (a[2] + b[2])};
        }

        /**
         * Un sentiero: {valore, quantita' maturata, 1 se knock-out}.
         */
        private double[] run(double[] z, double antithetic) {
            double x = 0.;
            boolean knockedIn = in.knockedIn();
            double value = 0.;
            double quantity = 0.;
            for (int i = 0; i < z.length; i++) {
                x += sqrtDv[i] * z[i] * antithetic;
                double s = Math.exp(lnForward[i] + x);
                if (koObserved[i] && type.isKnockedOut(s, koLevel[i])) {
                    if (in.koAccrual()) {
                        double q = quantity(s, knockedIn);
                        value += accrual(i, s, q);
                        quantity += q;
                    }
                    return new double[]{value, quantity, 1.};
                }
                if (in.kiBarrier() != null && !knockedIn && type.isKnockedIn(s, in.kiBarrier())) {
                    knockedIn = true;
                }
                double q = quantity(s, knockedIn);
                value += accrual(i, s, q);
                quantity += q;
            }
            return new double[]{value, quantity, 0.};
        }

        private double quantity(double s, boolean knockedIn) {
            if (in.kiEffect() == KnockInEffect.CONTRACT && !knockedIn) {
                return 0.;
            }
            if (type.isFavourable(s, in.strike())) {
                return in.dailyQuantity();
            }
            boolean leveraged = in.kiEffect() != KnockInEffect.LEVERAGE || knockedIn;
            return in.dailyQuantity() * (leveraged ? in.leverage() : 1.);
        }

        private double accrual(int i, double s, double q) {
            return type.getSign() * q * (s * settlementRatio[i] - in.strike()) * discountFactor[i];
        }
    }
}
