/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.FxOptionInputData;
import org.softcaster.engine.dto.OptionCalcOutputData;
import org.softcaster.engine.dto.OptionData;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.math.Gaussian;
import org.softcaster.engine.math.MathUtil;
import org.softcaster.engine.math.OptionUtil;

/**
 *
 * @author ep
 */
public class GarmanKohlhagenPricer implements IOptionPricer<FxOptionInputData> {

    @Override
    public OptionCalcOutputData priceCall(FxOptionInputData input) {
        OptionCalcOutputData output = new OptionCalcOutputData();
        double t = OptionUtil.getTimeToMaturity(input);
        double s = input.getSpotPrice();
        double k = input.getStrike();
        double rd = input.getDomesticRate(); // Domestic
        double rf = input.getForeignRate(); // Foreign
        double vol = input.getVolatility();
        double sqrtT = Math.sqrt(t);

        double d1 = (Math.log(s / k) + (rd - rf + 0.5 * vol * vol) * t) / (vol * sqrtT);
        double d2 = d1 - vol * sqrtT;

        // Premium
        double dfForeign = Math.exp(-rf * t);
        double dfDomestic = Math.exp(-rd * t);
        output.setPrice(s * dfForeign * Gaussian.cdf(d1) - k * dfDomestic * Gaussian.cdf(d2));

        // PDF di d1 (necessaria per le greche)
        double nd1 = Gaussian.pdf(d1);

        // Greeks
        //output.setDelta(dfForeign * Gaussian.cdf(d1));
        output.setDelta(Math.exp(-input.getForeignRate() * t) * Gaussian.cdf(d1));
        output.setGamma((nd1 * dfForeign) / (s * vol * sqrtT));
        output.setVega(s * dfForeign * sqrtT * nd1 / 100.0); // Diviso 100 per 1% vol

        // Theta (versione semplificata per FX)
        double theta = -(s * dfForeign * nd1 * vol) / (2 * sqrtT)
                + rf * s * dfForeign * Gaussian.cdf(d1)
                - rd * k * dfDomestic * Gaussian.cdf(d2);
        output.setTheta(theta / 365.0);

        return output;
    }

    @Override
    public OptionCalcOutputData pricePut(FxOptionInputData input) {
        OptionCalcOutputData output = new OptionCalcOutputData();

        double t = OptionUtil.getTimeToMaturity(input);
        double s = input.getSpotPrice();
        double k = input.getStrike();
        double rd = input.getDomesticRate(); // Domestic Rate (es. USD)
        double rf = input.getForeignRate(); // Foreign Rate (es. EUR)
        double vol = input.getVolatility();
        double sqrtT = Math.sqrt(t);

        // d1 e d2 sono identici alla Call
        double d1 = (Math.log(s / k) + (rd - rf + 0.5 * vol * vol) * t) / (vol * sqrtT);
        double d2 = d1 - vol * sqrtT;

        double dfForeign = Math.exp(-rf * t);
        double dfDomestic = Math.exp(-rd * t);

        // --- PREMIUM PUT ---
        // Formula: K * e^(-rd*t) * N(-d2) - S * e^(-rf*t) * N(-d1)
        double price = k * dfDomestic * Gaussian.cdf(-d2) - s * dfForeign * Gaussian.cdf(-d1);
        output.setPrice(price);

        // --- GREEKS PUT ---
        double nd1 = Gaussian.pdf(d1);

        // Delta Put = e^(-rf*t) * (N(d1) - 1)
        output.setDelta(dfForeign * (Gaussian.cdf(d1) - 1.0));

        // Gamma e Vega sono identici alla Call
        output.setGamma((nd1 * dfForeign) / (s * vol * sqrtT));
        output.setVega(s * dfForeign * sqrtT * nd1 / 100.0);

        // Theta Put
        double theta = -(s * dfForeign * nd1 * vol) / (2 * sqrtT)
                - rf * s * dfForeign * Gaussian.cdf(-d1)
                + rd * k * dfDomestic * Gaussian.cdf(-d2);
        output.setTheta(theta / 365.0);

        return output;
    }

    public double calculateImpliedVolatility(FxOptionInputData input, double targetPrice) {

        // 1. Definiamo la funzione obiettivo: f(vol) = PrezzoFX(vol) - PrezzoMercato
        MathUtil.Function1 ivFunction = new MathUtil.Function1() {
            @Override
            public double f(double vol) {
                // Creiamo un clone dell'input per non sporcare l'originale
                FxOptionInputData tempInput = createInputWithNewVol(input, vol);
                // Calcoliamo il prezzo in base al tipo (Call o Put)
                OptionCalcOutputData output = (input.getOptionType() == OptionType.CALL)
                        ? priceCall(tempInput)
                        : pricePut(tempInput);

                return output.getPrice() - targetPrice;
            }

            @Override
            public double f(double x, Compounding compounding) {
                return f(x);
            }

        };

        // 2. Risoluzione tramite Newton-Raphson
        // Partiamo da un guess standard (es. 15% o 0.15)
        try {
            return MathUtil.rootNewton(ivFunction, 0.15, Compounding.COMPOUNDED);
        } catch (Exception e) {
            throw new PricingException("FX Implied Volatility failed to converge. "
                    + "The market price might be outside arbitrage bounds.");
        }
    }

    // Dentro GarmanKohlhagenPricer o dove calcoli la IV
    private FxOptionInputData createInputWithNewVol(FxOptionInputData original, double newVol) {
        // Creiamo un nuovo record basato su quello vecchio ma con vol diversa
        OptionData newOptionData = new OptionData(
                original.getOptionData().strike(),
                newVol, // Nuova volatilità calcolata da Newton
                original.getOptionData().style(),
                original.getOptionData().type()
        );

        // Creiamo un nuovo oggetto di input (o usiamo un costruttore/setter per rimpiazzare il record)
        FxOptionInputData newInput = cloneInput(original); // Il tuo metodo clone
        newInput.setOptionData(newOptionData);
        return newInput;
    }

    // Metodo di supporto per il cloning dei dati di input
    private FxOptionInputData cloneInput(FxOptionInputData src) {
        FxOptionInputData dest = new FxOptionInputData();
        dest.setSpotPrice(src.getSpotPrice());
        dest.setOptionData(src.getOptionData());
        dest.setDomesticRate(src.getDomesticRate()); // Domestic
        dest.setForeignRate(src.getForeignRate()); // Foreign
        dest.setValuationDate(src.getValuationDate());
        dest.setMaturityDate(src.getMaturityDate());
        dest.setDaycount(src.getDaycount());
        return dest;
    }
}
