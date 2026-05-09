/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.OptionCalcInputData;
import org.softcaster.engine.dto.OptionCalcOutputData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.math.Gaussian;
import org.softcaster.engine.math.MathUtil;
import org.softcaster.engine.math.OptionUtil;

/**
 *
 * @author ep
 */
public class BlackAndScholesPricer implements IOptionPricer {

    @Override
    public OptionCalcOutputData priceCall(OptionCalcInputData input) {
        OptionCalcOutputData output = new OptionCalcOutputData();
        double t = OptionUtil.getTimeToMaturity(input);
        double s = input.getSpotPrice();
        double k = input.getStrike();
        double r = input.getBcyRate();
        double vol = input.getVolatility();

        // se t è zero, il prezzo dell'opzione è semplicemente il suo valore intrinseco
        // Max[(S-K),0)
        if (t <= 0) {
            output.setPrice((s - k > 0 ? (s - k) : 0));
            return output;
        }

        double b = k * Math.exp(-r * t);
        double sigmaSqrtT = vol * Math.sqrt(t);

        double d1 = (Math.log(s / b) / sigmaSqrtT) + 0.5 * sigmaSqrtT;
        double d2 = d1 - sigmaSqrtT;

        // Prezzo
        output.setPrice(s * Gaussian.cdf(d1) - b * Gaussian.cdf(d2));

        // Greche 
        output.setDelta(Gaussian.cdf(d1));
        output.setGamma(Gaussian.pdf(d1) / (s * sigmaSqrtT));
        output.setVega(s * Gaussian.pdf(d1) * Math.sqrt(t) / 100.0);
        output.setTheta(-((s * Gaussian.pdf(d1) * vol) / (2 * Math.sqrt(t)) + r * b * Gaussian.cdf(d2)) / 365.0);

        return output;
    }

    @Override
    public OptionCalcOutputData pricePut(OptionCalcInputData input) {
        // 1. Calcoliamo la Call (che popola Prezzo e Greche della Call)
        OptionCalcOutputData output = priceCall(input);

        double t = OptionUtil.getTimeToMaturity(input);
        double r = input.getBcyRate();
        double k = input.getStrike();
        double s = input.getSpotPrice();

        if (t <= 0) {
            output.setPrice((k - s > 0 ? (k - s) : 0));
            return output;
        }

        // --- CORREZIONE PREZZO (Put-Call Parity) ---
        double b = k * Math.exp(-r * t);
        double callPrice = output.getPrice();
        double putPrice = b + callPrice - s;
        output.setPrice(putPrice);

        // --- CORREZIONE GRECHE ---
        // Delta Put = Delta Call - 1
        // (Se Delta Call è 0.6, Delta Put è -0.4)
        output.setDelta(output.getDelta() - 1.0);

        // Theta Put = Theta Call + r * K * e^(-rT)
        // In termini giornalieri (diviso 365)
        double thetaAdjustment = (r * b) / 365.0;
        output.setTheta(output.getTheta() + thetaAdjustment);

        // Gamma e Vega rimangono invariati rispetto alla Call
        return output;
    }

    public double calculateImpliedVolatility(OptionCalcInputData input, double targetPrice, OptionType type) {

        double t = OptionUtil.getTimeToMaturity(input);
        double s = input.getSpotPrice();
        double k = input.getStrike();
        // 1. Validazione finanziaria preventiva
        double intrinsicValue = type == OptionType.CALL ? Math.max(0, s - k) : Math.max(0, k - s);

        if (targetPrice <= intrinsicValue) {
            throw new ArbitrageViolationException(targetPrice, intrinsicValue);
        }
        
        MathUtil.Function1 ivFunction = new MathUtil.Function1() {

            @Override
            public double f(double vol) {
                // Creiamo un input temporaneo variando solo la volatilità
                OptionCalcInputData tempInput = cloneInput(input);
                tempInput.setVolatility(vol);

                double currentPrice = type == OptionType.CALL
                        ? priceCall(tempInput).getPrice()
                        : pricePut(tempInput).getPrice();

                // La radice è dove PrezzoModello - PrezzoMercato = 0
                return currentPrice - targetPrice;
            }

            @Override
            public double f(double x, Compounding compounding) {
                return f(x);
            }
        };

        // Partiamo da un guess del 20% (0.20)
        // Newton-Raphson è molto veloce qui perché il Vega è sempre positivo
        return MathUtil.rootNewton(ivFunction, 0.20, Compounding.COMPOUNDED);
    }

    private OptionCalcInputData cloneInput(OptionCalcInputData original) {
        OptionCalcInputData copy = new OptionCalcInputData();
        copy.setSpotPrice(original.getSpotPrice());
        copy.setStrike(original.getStrike());
        copy.setBcyRate(original.getBcyRate());
        copy.setSettlementDate(original.getSettlementDate());
        copy.setMaturityDate(original.getMaturityDate());
        copy.setDaycount(original.getDaycount());
        return copy;
    }
}
