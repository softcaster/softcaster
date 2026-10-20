/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.Greeks;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.math.Gaussian;
// + import di Gaussian dal suo package

/**
 * Black-76 per opzioni europee su future e forward.
 *
 * T = giorni / 365, F = prezzo del future/forward D = DF_curva(valuation ->
 * expiry) se il premio e' scontato, 1 se e' regolato con margini d1 = (ln(F/K)
 * + sigma^2 T / 2) / (sigma sqrt T), d2 = d1 - sigma sqrt T call = D (F N(d1) -
 * K N(d2)) put = D (K N(-d2) - F N(-d1))
 *
 * Greche rispetto al forward F (non allo spot): delta = D N(d1) | -D N(-d1)
 * gamma = D n(d1) / (F sigma sqrt T) vega = D F n(d1) sqrt T theta = r V - D F
 * n(d1) sigma / (2 sqrt T) rho = -T V con r = -ln(D) / T (r = 0 se D = 1)
 */
public final class Black76Pricer implements Pricer<ForwardOptionInput, OptionResult> {

    @Override
    public OptionResult calculate(ForwardOptionInput in) {
        OptionContract c = in.contract();
        if (c.style() != ExerciseStyle.EUROPEAN) {
            throw new IllegalArgumentException("Black-76 prezza solo opzioni europee: " + in.instrumentId());
        }

        double f = in.forward();
        double k = c.strike();
        double t = in.timeToExpiry();
        double d = in.premiumDiscounted()
                ? in.discountCurve().getDiscountFactor(c.expiryDate())
                / in.discountCurve().getDiscountFactor(in.valuationDate())
                : 1.0;

        double sigmaSqrtT = in.volatility() * Math.sqrt(t);
        if (t <= 0 || sigmaSqrtT <= 0) {
            return atLimit(in, f, d, t);
        }

        double r = -Math.log(d) / t;
        double d1 = (Math.log(f / k) + 0.5 * sigmaSqrtT * sigmaSqrtT) / sigmaSqrtT;
        double d2 = d1 - sigmaSqrtT;
        double nd1 = Gaussian.pdf(d1);

        double price;
        double delta;
        if (c.isCall()) {
            price = d * (f * Gaussian.cdf(d1) - k * Gaussian.cdf(d2));
            delta = d * Gaussian.cdf(d1);
        } else {
            price = d * (k * Gaussian.cdf(-d2) - f * Gaussian.cdf(-d1));
            delta = -d * Gaussian.cdf(-d1);
        }
        double gamma = d * nd1 / (f * sigmaSqrtT);
        double vega = d * f * nd1 * Math.sqrt(t);
        double theta = r * price - d * f * nd1 * in.volatility() / (2 * Math.sqrt(t));
        double rho = -t * price;

        Greeks greeks = new Greeks(
                delta,
                gamma,
                vega / 100.0, // per punto di volatilita'
                theta / 365.0, // per giorno di calendario
                rho / 100.0);       // per punto percentuale di tasso

        return new OptionResult(in.instrumentId(), in.valuationDate(), price, price * c.notional(),
                f, d, t, greeks);
    }

    /**
     * Scadenza o volatilita' nulla: payoff deterministico sul forward.
     */
    private static OptionResult atLimit(ForwardOptionInput in, double f, double d, double t) {
        OptionContract c = in.contract();
        double k = c.strike();
        double payoff = c.isCall() ? Math.max(f - k, 0) : Math.max(k - f, 0);
        double price = d * payoff;
        double delta = payoff > 0 ? (c.isCall() ? d : -d) : 0.0;
        return new OptionResult(in.instrumentId(), in.valuationDate(), price, price * c.notional(),
                f, d, t, Greeks.atExpiry(delta));
    }
}
