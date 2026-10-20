/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.EquityOptionInput;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.dto.Greeks;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.math.Gaussian;
// + import di Gaussian dal suo package

/**
 * Black-Scholes-Merton per opzioni europee su azioni e indici, con dividend
 * yield continuo.
 *
 * T = giorni / 365, DF = DF_curva(valuation -> expiry), r = -ln(DF) / T F = S
 * e^{-qT} / DF d1 = (ln(F/K) + sigma^2 T / 2) / (sigma sqrt T), d2 = d1 - sigma
 * sqrt T call = DF (F N(d1) - K N(d2)) put = DF (K N(-d2) - F N(-d1))
 *
 * Le opzioni americane vanno prezzate con CRRBinomialPricer: qui vengono
 * rifiutate.
 */
public final class BlackAndScholesPricer implements Pricer<EquityOptionInput, OptionResult> {

    @Override
    public OptionResult calculate(EquityOptionInput in) {
        OptionContract c = in.contract();
        if (c.style() != ExerciseStyle.EUROPEAN) {
            throw new IllegalArgumentException("Black-Scholes prezza solo opzioni europee: usare CRRBinomialPricer per "
                    + in.instrumentId());
        }

        double s = in.spot();
        double k = c.strike();
        double q = in.dividendYield();
        double t = in.timeToExpiry();
        double df = in.discountCurve().getDiscountFactor(c.expiryDate())
                / in.discountCurve().getDiscountFactor(in.valuationDate());
        double dq = t > 0 ? Math.exp(-q * t) : 1.0;           // sconto per i dividendi
        double forward = s * dq / df;

        // a scadenza (o con volatilita' nulla) vale il payoff sul forward, scontato
        double sigmaSqrtT = in.volatility() * Math.sqrt(t);
        if (t <= 0 || sigmaSqrtT <= 0) {
            return atLimit(in, forward, df, t);
        }

        double r = -Math.log(df) / t;                         // tasso continuo equivalente
        double d1 = (Math.log(forward / k) + 0.5 * sigmaSqrtT * sigmaSqrtT) / sigmaSqrtT;
        double d2 = d1 - sigmaSqrtT;
        double nd1 = Gaussian.pdf(d1);

        double price;
        double delta;
        double theta;
        double rho;
        if (c.isCall()) {
            price = s * dq * Gaussian.cdf(d1) - k * df * Gaussian.cdf(d2);
            delta = dq * Gaussian.cdf(d1);
            theta = -s * dq * nd1 * in.volatility() / (2 * Math.sqrt(t))
                    - r * k * df * Gaussian.cdf(d2)
                    + q * s * dq * Gaussian.cdf(d1);
            rho = k * t * df * Gaussian.cdf(d2);
        } else {
            price = k * df * Gaussian.cdf(-d2) - s * dq * Gaussian.cdf(-d1);
            delta = -dq * Gaussian.cdf(-d1);
            theta = -s * dq * nd1 * in.volatility() / (2 * Math.sqrt(t))
                    + r * k * df * Gaussian.cdf(-d2)
                    - q * s * dq * Gaussian.cdf(-d1);
            rho = -k * t * df * Gaussian.cdf(-d2);
        }
        double gamma = dq * nd1 / (s * sigmaSqrtT);
        double vega = s * dq * nd1 * Math.sqrt(t);

        Greeks greeks = new Greeks(
                delta,
                gamma,
                vega / 100.0, // per punto di volatilita'
                theta / 365.0, // per giorno di calendario
                rho / 100.0);       // per punto percentuale di tasso

        return new OptionResult(in.instrumentId(), in.valuationDate(), price, price * c.notional(),
                forward, df, t, greeks);
    }

    /**
     * Scadenza o volatilita' nulla: payoff deterministico sul forward,
     * scontato.
     */
    private static OptionResult atLimit(EquityOptionInput in, double forward, double df, double t) {
        OptionContract c = in.contract();
        double k = c.strike();
        boolean inTheMoney = c.isCall() ? forward > k : forward < k;
        double price = df * (c.isCall() ? Math.max(forward - k, 0) : Math.max(k - forward, 0));
        double dq = forward * df / in.spot();                 // e^{-qT}
        double delta = inTheMoney ? (c.isCall() ? dq : -dq) : 0.0;
        return new OptionResult(in.instrumentId(), in.valuationDate(), price, price * c.notional(),
                forward, df, t, Greeks.atExpiry(delta));
    }
}
