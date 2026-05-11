/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.FxOptionInputData;
import org.softcaster.engine.dto.MarketOutputData;
import org.softcaster.engine.dto.OptionOutputData;
import org.softcaster.engine.enums.OptionType;
import static org.softcaster.engine.enums.OptionType.CALL;
import org.softcaster.engine.math.Gaussian;
import org.softcaster.engine.math.OptionUtil;

public class Black76Pricer implements IOptionPricer<FxOptionInputData> {

    private OptionOutputData price(FxOptionInputData input) {
        // F = Prezzo Forward o Future del sottostante
        double F = input.getSpotPrice(); // Nota che questo e'un prezzo Forward/Futures
        double K = input.getStrike();
        double r = input.getDomesticRate();
        double sigma = input.getVolatility();
        double t = OptionUtil.getTimeToMaturity(input);

        if (t <= 0.) {
            OptionOutputData output = new OptionOutputData();
            double intrincicValue = 0.;
            switch (input.getOptionType()) {
                case CALL -> {
                    intrincicValue = ((F - K) > 0 ? (F - K) : 0.);
                    output.setPrice(intrincicValue);
                    return output;
                }
                case PUT -> {
                    intrincicValue = ((K - F) > 0 ? (K - F) : 0.);
                    output.setPrice(intrincicValue);
                    return output;
                }
            }
        }
        double sigmaSqrtT = sigma * Math.sqrt(t);

        // Nel Black-76, d1 non include il tasso r nell'argomento del logaritmo
        double d1 = (Math.log(F / K) + 0.5 * sigma * sigma * t) / sigmaSqrtT;
        double d2 = d1 - sigmaSqrtT;

        double discountFactor = Math.exp(-r * t);
        double price;

        if (input.getOptionType() == OptionType.CALL) {
            price = discountFactor * (F * Gaussian.cdf(d1) - K * Gaussian.cdf(d2));
        } else {
            price = -discountFactor * (K * Gaussian.cdf(-d2) - F * Gaussian.cdf(-d1));
        }

        OptionOutputData output = new OptionOutputData();
        output.setPrice(price);

        // Greche nel Black-76 (Sconto applicato al delta)
        double deltaSign = (input.getOptionType() == OptionType.CALL) ? 1.0 : -1.0;
        output.setDelta(discountFactor * Gaussian.cdf(d1 * deltaSign));

        return output;
    }

    @Override
    public MarketOutputData priceCall(FxOptionInputData input) {
        return price(input);
    }

    @Override
    public MarketOutputData pricePut(FxOptionInputData input) {
        return price(input);
    }
}
