/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.OptionInput;
import org.softcaster.engine.dto.OptionResult;

/**
 * Volatilita' implicita per qualunque pricer di opzioni europee.
 *
 * Newton-Raphson con il vega analitico del pricer, protetto da un intervallo
 * [MIN_VOL, MAX_VOL] che si restringe a ogni passo (bisezione quando Newton
 * uscirebbe dall'intervallo): converge sempre, e la volatilita' non diventa mai negativa.
 *
 * Il prezzo obiettivo deve stare tra i limiti di non arbitraggio, cioe' tra il prezzo
 * a volatilita' minima (valore intrinseco scontato sul forward) e quello a volatilita' massima.
 */
public final class ImpliedVolatility {

    private static final double MIN_VOL = 1e-6;
    private static final double MAX_VOL = 5.0;       // 500%
    private static final double PRICE_TOLERANCE = 1e-10;
    private static final int MAX_ITERATIONS = 100;

    private ImpliedVolatility() {
    }

    /**
     * @param <I>
     * @param pricer      pricer del modello (Black-Scholes, Garman-Kohlhagen, Black-76...)
     * @param input       input dell'opzione: la volatilita' contenuta e' usata come primo tentativo
     *                    se positiva, altrimenti si parte dal 20%
     * @param targetPrice prezzo unitario di mercato dell'opzione
     * @return 
     */
    @SuppressWarnings("unchecked")
    public static <I extends OptionInput> double solve(Pricer<I, OptionResult> pricer, I input, double targetPrice) {
        if (input.timeToExpiry() <= 0) {
            throw new IllegalArgumentException("Volatilita' implicita non definita a scadenza");
        }

        double lowPrice = pricer.calculate((I) input.withVolatility(MIN_VOL)).price();
        double highPrice = pricer.calculate((I) input.withVolatility(MAX_VOL)).price();
        if (targetPrice <= lowPrice || targetPrice >= highPrice) {
            throw new ArbitrageViolationException(targetPrice, lowPrice);
        }

        double lo = MIN_VOL;
        double hi = MAX_VOL;
        double vol = input.volatility() > MIN_VOL ? input.volatility() : 0.20;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            OptionResult r = pricer.calculate((I) input.withVolatility(vol));
            double diff = r.price() - targetPrice;
            if (Math.abs(diff) < PRICE_TOLERANCE) {
                return vol;
            }
            // il prezzo cresce con la volatilita': restringo l'intervallo
            if (diff > 0) {
                hi = vol;
            } else {
                lo = vol;
            }
            double vega = r.greeks().vega() * 100.0;  // per unita' di volatilita'
            double next = vega > 0 ? vol - diff / vega : Double.NaN;
            vol = (next > lo && next < hi) ? next : 0.5 * (lo + hi);
            if (hi - lo < 1e-12) {
                return vol;
            }
        }
        throw new IllegalStateException("Volatilita' implicita non convergente per " + input.instrumentId());
    }
}