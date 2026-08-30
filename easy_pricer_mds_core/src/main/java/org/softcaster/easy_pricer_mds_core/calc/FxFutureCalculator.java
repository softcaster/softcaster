/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.calc;

//@Service("fxFutureCalculator")
import org.softcaster.engine.analytics.FxForwardPricer;
import org.softcaster.engine.dto.ForwardBaseInputData;
import org.softcaster.engine.dto.ForwardBaseOutputData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class FxFutureCalculator {

    @Autowired
    @Qualifier("fxFwdPricer")
    private FxForwardPricer fxForwardPricer;

    public ForwardBaseOutputData fxFwdValuation(ForwardBaseInputData input) {

        ForwardBaseOutputData output = new ForwardBaseOutputData();

        if (fxForwardPricer != null) {
            double price = fxForwardPricer.forwardPrice(input);
            output.setPrice(price);
            output.setBasis(price -input.getReferencePrice());
        }
        return output;
    }
}
