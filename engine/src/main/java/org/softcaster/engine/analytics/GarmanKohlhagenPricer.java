/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.FxOptionInput;
import org.softcaster.engine.dto.Greeks;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.math.Gaussian;
// + import di Gaussian dal suo package

/**
 * Garman-Kohlhagen per opzioni europee su cambi: Black-Scholes con il tasso
 * estero al posto del dividend yield.
 *
 * Convenzione di FxOptionInput: spot e strike in valuta domestica per 1 unita'
 * di valuta estera, prezzo in valuta domestica per 1 unita' di valuta estera.
 *
 * T = giorni / 365 DFd = DF_domestica(valuation -> expiry), DFf =
 * DF_estera(valuation -> expiry) F = S DFf / DFd (stesso forward di
 * FxForwardPricer) d1 = (ln(F/K) + sigma^2 T / 2) / (sigma sqrt T), d2 = d1 -
 * sigma sqrt T call = S DFf N(d1) - K DFd N(d2) put = K DFd N(-d2) - S DFf
 * N(-d1)
 *
 * Greche rispetto allo spot; rho rispetto al tasso DOMESTICO (curva estera
 * ferma). Le americane vanno prezzate con CRRBinomialPricer.
 */
public final class GarmanKohlhagenPricer implements Pricer<FxOptionInput, OptionResult> {

    @Override
    public OptionResult calculate(FxOptionInput in) {
        OptionContract c = in.contract();
        if (c.style() != ExerciseStyle.EUROPEAN) {
            throw new IllegalArgumentException("Garman-Kohlhagen prezza solo opzioni europee: usare CRRBinomialPricer per "
                    + in.instrumentId());
        }

        double s = in.spot();
        double k = c.strike();
        double t = in.timeToExpiry();
        double dfd = in.domesticCurve().getDiscountFactor(c.expiryDate())
                / in.domesticCurve().getDiscountFactor(in.valuationDate());
        double dff = in.foreignCurve().getDiscountFactor(c.expiryDate())
                / in.foreignCurve().getDiscountFactor(in.valuationDate());
        double forward = s * dff / dfd;

        double sigmaSqrtT = in.volatility() * Math.sqrt(t);
        if (t <= 0 || sigmaSqrtT <= 0) {
            return atLimit(in, forward, dfd, dff, t);
        }

        double rd = -Math.log(dfd) / t;
        double rf = -Math.log(dff) / t;
        double d1 = (Math.log(forward / k) + 0.5 * sigmaSqrtT * sigmaSqrtT) / sigmaSqrtT;
        double d2 = d1 - sigmaSqrtT;
        double nd1 = Gaussian.pdf(d1);
        double timeDecay = -s * dff * nd1 * in.volatility() / (2 * Math.sqrt(t));

        double price;
        double delta;
        double theta;
        double rho;
        if (c.isCall()) {
            price = s * dff * Gaussian.cdf(d1) - k * dfd * Gaussian.cdf(d2);
            delta = dff * Gaussian.cdf(d1);
            theta = timeDecay + rf * s * dff * Gaussian.cdf(d1) - rd * k * dfd * Gaussian.cdf(d2);
            rho = k * t * dfd * Gaussian.cdf(d2);
        } else {
            price = k * dfd * Gaussian.cdf(-d2) - s * dff * Gaussian.cdf(-d1);
            delta = -dff * Gaussian.cdf(-d1);
            theta = timeDecay - rf * s * dff * Gaussian.cdf(-d1) + rd * k * dfd * Gaussian.cdf(-d2);
            rho = -k * t * dfd * Gaussian.cdf(-d2);
        }
        double gamma = dff * nd1 / (s * sigmaSqrtT);
        double vega = s * dff * nd1 * Math.sqrt(t);

        Greeks greeks = new Greeks(
                delta,
                gamma,
                vega / 100.0, // per punto di volatilita'
                theta / 365.0, // per giorno di calendario
                rho / 100.0);       // per punto percentuale del tasso domestico

        return new OptionResult(in.instrumentId(), in.valuationDate(), price, price * c.notional(),
                forward, dfd, t, greeks);
    }

    /**
     * Scadenza o volatilita' nulla: payoff deterministico sul forward, scontato
     * in valuta domestica.
     */
    private static OptionResult atLimit(FxOptionInput in, double forward, double dfd, double dff, double t) {
        OptionContract c = in.contract();
        double k = c.strike();
        double payoff = c.isCall() ? Math.max(forward - k, 0) : Math.max(k - forward, 0);
        double price = dfd * payoff;
        double delta = payoff > 0 ? (c.isCall() ? dff : -dff) : 0.0;
        return new OptionResult(in.instrumentId(), in.valuationDate(), price, price * c.notional(),
                forward, dfd, t, Greeks.atExpiry(delta));
    }
}
