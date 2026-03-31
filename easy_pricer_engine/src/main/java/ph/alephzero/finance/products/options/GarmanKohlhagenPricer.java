/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.options;

import ph.alephzero.finance.products.options.math.Gaussian;
import ph.alephzero.finance.products.options.math.OptionUtil;

/**
 *
 * @author ep
 */
public class GarmanKohlhagenPricer implements IOptionPricer {

    @Override
    public OptionCalcOutputData priceCall(OptionCalcInputData input) {

        OptionCalcOutputData output = new OptionCalcOutputData();

        double t = OptionUtil.getTimeToMaturity(input);

        double d1 = ((Math.log(input.getSpotPrice() / input.getStrike()) + (input.getBcyRate() - input.getCcyRate() + Math.pow(input.getVolatility(), 2) / 2.0) * t))
                / (input.getVolatility() * Math.pow(t, 0.5));
        double d2 = d1 - input.getVolatility() * Math.pow(t, 0.5);

        //
        // premium
        //
        output.setPrice(input.getSpotPrice() * Math.exp(-input.getCcyRate() * t) * Gaussian.cdf(d1) - input.getStrike() * Math.exp(-input.getBcyRate() * t) * Gaussian.cdf(d2));

        // derivata di N(x) -> Gaussian.cdf(x) 
        double dNx = 1 / Math.pow(2 * Math.PI, 0.5);

        //
        // greeks
        //
        output.setDelta(Math.exp(-input.getCcyRate() * t) * Gaussian.cdf(d1));
        output.setTheta(dNx * Math.exp(-(Math.pow(d1, 2) / 2.)));
        output.setGamma((dNx * Math.exp(-input.getCcyRate() * t)) / (input.getSpotPrice() * Math.pow(t, 0.5)));
        output.setVega(input.getSpotPrice() * Math.pow(t, 0.5) * dNx * Math.exp(-input.getBcyRate() * t));
        output.setRhoD(input.getStrike() * t * Math.exp(-input.getBcyRate() * t) * Gaussian.cdf(d2));
        output.setRhoF(-t * Math.exp(-input.getCcyRate() * t) * input.getSpotPrice() * Gaussian.cdf(d1));

        return output;
    }

    @Override
    public OptionCalcOutputData pricePut(OptionCalcInputData input) {
        OptionCalcOutputData output = new OptionCalcOutputData();

        double t = OptionUtil.getTimeToMaturity(input);

        double d1 = ((Math.log(input.getSpotPrice() / input.getStrike()) + (input.getBcyRate()-input.getCcyRate() + Math.pow(input.getVolatility(),2)/2.0)*t)) 
                / (input.getVolatility() * Math.pow(t, 0.5));
        double d2 = d1 - input.getVolatility() * Math.pow(t, 0.5);

        //
        // greeks
        //
        //double price = k * Math.exp(-rd * t) * Gaussian.cdf(-d2) - s*Math.exp(-rf * t) * Gaussian.cdf(-d1);
        output.setPrice(input.getStrike() * Math.exp(-input.getBcyRate() * t) * Gaussian.cdf(-d2) - input.getSpotPrice()*Math.exp(-input.getCcyRate() * t) * Gaussian.cdf(-d1));

        return output;
    }

}
