/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import org.softcaster.engine.dto.EquityOptionInput;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.FxOptionInput;
import org.softcaster.engine.dto.Greeks;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionInput;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.enums.ExerciseStyle;

/**
 * Albero binomiale ricombinante per opzioni europee, americane e bermudan su
 * azioni/indici, cambi e future/forward.
 *
 * Il sottostante e' ridotto a tre parametri: S = prezzo di partenza (spot,
 * oppure forward per ForwardOptionInput) r = tasso continuo di sconto del
 * premio b = cost of carry continuo, cioe' la crescita attesa del sottostante:
 * azione b = r - q, cambio b = r_dom - r_for, future/forward b = 0
 *
 * Nodi: S_{n,i} = S * exp(b n dt + sigma sqrt(dt) (2i - n)), i = 0..n E' la
 * spaziatura del CRR (u/d = exp(2 sigma sqrt dt)) con i nodi centrati sul
 * forward: la probabilita' p resta vicina a 0,5 per qualunque tasso e
 * volatilita', quindi l'albero non fallisce con tassi alti o volatilita' basse
 * (necessario per la volatilita' implicita).
 *
 * Greche: delta, gamma: dai nodi ai passi 1 e 2 dell'albero (rispetto a S)
 * vega, theta, rho: riprezzando con +1 punto di vol, +1 giorno, +1 punto di
 * tasso
 */
public final class CRRBinomialPricer implements Pricer<OptionInput, OptionResult> {

    private static final double MIN_SIGMA = 1e-8;   // volatilita' nulla trattata come quasi nulla
    private static final double ONE_DAY = 1.0 / 365.0;

    private final int steps;

    public CRRBinomialPricer(int steps) {
        if (steps < 2) {
            throw new IllegalArgumentException("Servono almeno 2 step (per il gamma)");
        }
        this.steps = steps;
    }

    @Override
    public OptionResult calculate(OptionInput in) {
        OptionContract c = in.contract();
        Params p = params(in);

        if (p.t <= 0) {
            double payoff = payoff(c, p.s);
            double delta = payoff > 0 ? (c.isCall() ? 1.0 : -1.0) : 0.0;
            return new OptionResult(in.instrumentId(), in.valuationDate(), payoff, payoff * c.notional(),
                    p.s, 1.0, 0.0, Greeks.atExpiry(delta));
        }

        Set<Integer> exerciseSteps = exerciseSteps(in, p.t);
        Tree base = tree(c, p, exerciseSteps, true);

        // greche per riprezzamento, differenze centrali (risultato per 1 punto di vol / di tasso)
        double h = 0.01;      // incremento ampio: riduce il rumore di discretizzazione dell albero
        double sigmaDown = Math.max(p.sigma - h, 0.0);
        double vega = (tree(c, p.withSigma(p.sigma + h), exerciseSteps, false).value
                - tree(c, p.withSigma(sigmaDown), exerciseSteps, false).value)
                / (p.sigma + h - sigmaDown) * 0.01;
        double carryBump = in instanceof ForwardOptionInput ? 0.0 : h;     // sul future il carry resta 0
        double rho = (tree(c, p.withRate(p.r + h, p.b + carryBump), exerciseSteps, false).value
                - tree(c, p.withRate(p.r - h, p.b - carryBump), exerciseSteps, false).value)
                / (2 * h) * 0.01;
        double theta = p.t > ONE_DAY
                ? tree(c, p.withTime(p.t - ONE_DAY), exerciseSteps(in, p.t - ONE_DAY), false).value - base.value
                : payoff(c, p.s) - base.value;

        Greeks greeks = new Greeks(base.delta, base.gamma, vega, theta, rho);
        double forward = p.s * Math.exp(p.b * p.t);
        double df = Math.exp(-p.r * p.t);
        return new OptionResult(in.instrumentId(), in.valuationDate(), base.value, base.value * c.notional(),
                forward, df, p.t, greeks);
    }

    // ------------------------------------------------------------------------
    // -- Albero
    // ------------------------------------------------------------------------
    private record Tree(double value, double delta, double gamma) {

    }

    private Tree tree(OptionContract c, Params p, Set<Integer> exerciseSteps, boolean withGreeks) {
        double dt = p.t / steps;
        double sigma = Math.max(p.sigma, MIN_SIGMA);
        double jump = sigma * Math.sqrt(dt);
        double prob = (1.0 - Math.exp(-jump)) / (Math.exp(jump) - Math.exp(-jump));
        double disc = Math.exp(-p.r * dt);
        boolean american = c.style() == ExerciseStyle.AMERICAN;

        double[] v = new double[steps + 1];
        for (int i = 0; i <= steps; i++) {
            v[i] = payoff(c, node(p, dt, jump, steps, i));
        }

        double[] level1 = null;
        double[] level2 = null;
        for (int n = steps - 1; n >= 0; n--) {
            boolean canExercise = american || exerciseSteps.contains(n);
            for (int i = 0; i <= n; i++) {
                double cont = disc * (prob * v[i + 1] + (1.0 - prob) * v[i]);
                v[i] = canExercise ? Math.max(cont, payoff(c, node(p, dt, jump, n, i))) : cont;
            }
            if (withGreeks && n == 2) {
                level2 = new double[]{v[0], v[1], v[2]};
            }
            if (withGreeks && n == 1) {
                level1 = new double[]{v[0], v[1]};
            }
        }

        if (!withGreeks) {
            return new Tree(v[0], Double.NaN, Double.NaN);
        }
        double s10 = node(p, dt, jump, 1, 0);
        double s11 = node(p, dt, jump, 1, 1);
        double s20 = node(p, dt, jump, 2, 0);
        double s21 = node(p, dt, jump, 2, 1);
        double s22 = node(p, dt, jump, 2, 2);
        double delta = (level1[1] - level1[0]) / (s11 - s10);
        double gamma = ((level2[2] - level2[1]) / (s22 - s21) - (level2[1] - level2[0]) / (s21 - s20))
                / (0.5 * (s22 - s20));
        return new Tree(v[0], delta, gamma);
    }

    /**
     * Prezzo del sottostante al nodo (n, i).
     */
    private static double node(Params p, double dt, double jump, int n, int i) {
        return p.s * Math.exp(p.b * n * dt + jump * (2 * i - n));
    }

    private static double payoff(OptionContract c, double s) {
        return c.isCall() ? Math.max(0.0, s - c.strike()) : Math.max(0.0, c.strike() - s);
    }

    /**
     * Step dell'albero corrispondenti alle date di esercizio (solo BERMUDAN),
     * calcolati una volta.
     */
    private Set<Integer> exerciseSteps(OptionInput in, double t) {
        Set<Integer> result = new HashSet<>();
        if (in.contract().style() != ExerciseStyle.BERMUDAN || t <= 0) {
            return result;
        }
        for (LocalDate date : in.contract().exerciseDates()) {
            double tDate = ChronoUnit.DAYS.between(in.valuationDate(), date) / 365.0;
            if (tDate >= 0 && tDate <= t) {          // date gia' passate ignorate
                result.add((int) Math.round(tDate / t * steps));
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    // -- Parametri del sottostante
    // ------------------------------------------------------------------------
    private record Params(double s, double r, double b, double sigma, double t) {

        Params withSigma(double newSigma) {
            return new Params(s, r, b, newSigma, t);
        }

        Params withRate(double newR, double newB) {
            return new Params(s, newR, newB, sigma, t);
        }

        Params withTime(double newT) {
            return new Params(s, r, b, sigma, newT);
        }
    }

    /**
     * Riduce i tre tipi di input a (S, r, b, sigma, T). Scritto con instanceof
     * per Java 17.
     */
    private static Params params(OptionInput in) {
        double t = in.timeToExpiry();
        LocalDate val = in.valuationDate();
        LocalDate exp = in.contract().expiryDate();

        if (in instanceof EquityOptionInput eq) {
            double r = rate(eq.discountCurve().getDiscountFactor(exp) / eq.discountCurve().getDiscountFactor(val), t);
            return new Params(eq.spot(), r, r - eq.dividendYield(), in.volatility(), t);
        }
        if (in instanceof FxOptionInput fx) {
            double rd = rate(fx.domesticCurve().getDiscountFactor(exp) / fx.domesticCurve().getDiscountFactor(val), t);
            double rf = rate(fx.foreignCurve().getDiscountFactor(exp) / fx.foreignCurve().getDiscountFactor(val), t);
            return new Params(fx.spot(), rd, rd - rf, in.volatility(), t);
        }
        if (in instanceof ForwardOptionInput fw) {
            double r = fw.premiumDiscounted()
                    ? rate(fw.discountCurve().getDiscountFactor(exp) / fw.discountCurve().getDiscountFactor(val), t)
                    : 0.0;
            return new Params(fw.forward(), r, 0.0, in.volatility(), t);
        }
        throw new IllegalArgumentException("Tipo di input non gestito: " + in.getClass().getSimpleName());
    }

    /**
     * Tasso continuo equivalente a un fattore di sconto.
     */
    private static double rate(double df, double t) {
        return t > 0 ? -Math.log(df) / t : 0.0;
    }
}
