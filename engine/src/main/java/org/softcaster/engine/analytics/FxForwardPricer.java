/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.ForwardBaseInputData;
import org.softcaster.engine.math.MathUtil;
import org.softcaster.engine.math.OptionUtil;

/**
 *
 * @author softc
 */
public class FxForwardPricer {

    // il cambio forward è il “prezzo relativo futuro” delle due valute dopo aver tenuto conto dei rendimenti finanziari.
    public double forwardPrice(ForwardBaseInputData input) {
        double S = input.getSpotPrice();
        double t = OptionUtil.getTimeToMaturity(input);
        double domesticDF = MathUtil.getDiscountFactor(input.getCompounding(), input.getDomesticRate(), t);
        double foreignDF = MathUtil.getDiscountFactor(input.getCompounding(), input.getForeignRate(), t);

        // Nota che con i DF domestic e foreign si invertono rispetto alla formulazione classica
        // F = S*[(1+ domesticRate * t) / (1+ foreignRate * t)]
        double F = S * (foreignDF / domesticDF);
        return F;
    }

    public double forwardPrice2(ForwardBaseInputData input) {
        double domesticDF = input.getDomesticRateCurve().getDiscountFactor(input.getMaturityDate());
        double foreignDF = input.getForeignRateCurve().getDiscountFactor(input.getMaturityDate());

        // Nota che con i DF domestic e foreign si invertono rispetto alla formulazione classica
        // F = S*[(1+ domesticRate * t) / (1+ foreignRate * t)]
        double F = input.getSpotPrice() * (foreignDF / domesticDF);
        return F;
    }

}
