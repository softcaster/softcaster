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
public class BlackAndScholesPricer implements IOptionPricer {

    @Override
    public OptionCalcOutputData priceCall(OptionCalcInputData input) {

        OptionCalcOutputData output = new OptionCalcOutputData();
        double t = OptionUtil.getTimeToMaturity(input);

        double b = input.getStrike() * Math.exp(-input.getBcyRate() * t);
        double d1 = ((Math.log(input.getSpotPrice() / b)) / (input.getVolatility() * Math.pow(t, 0.5))) + 0.5 * input.getVolatility() * Math.pow(t, 0.5);
        double d2 = (Math.log(input.getSpotPrice() / b)) / (input.getVolatility() * Math.pow(t, 0.5)) - 0.5 * input.getVolatility() * Math.pow(t, 0.5);

        output.setPrice(input.getSpotPrice() * Gaussian.cdf(d1) - input.getStrike() * Math.exp(-input.getBcyRate() * (t)) * Gaussian.cdf(d2));

        return output;
    }

    @Override
    public OptionCalcOutputData pricePut(OptionCalcInputData input) {

        // By put-call parity, s + p = k + c, and therefore the price is
        OptionCalcOutputData output = priceCall(input);

        double t = OptionUtil.getTimeToMaturity(input);
        double b = input.getStrike() * Math.exp(-input.getBcyRate() * t);

        double price = b + output.getPrice() - input.getSpotPrice();
        output.setPrice(price);

        return output;
    }

}
