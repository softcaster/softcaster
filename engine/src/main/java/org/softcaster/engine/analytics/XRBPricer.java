/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.CashFlowAnalytics;
import org.softcaster.engine.dto.FRBInput;
import org.softcaster.engine.dto.PricingResult;
import org.softcaster.engine.dto.XRBInput;
import org.softcaster.engine.dto.XRBResult;

public class XRBPricer extends AbstractFixedIncomePricer<XRBInput, XRBResult> {

    @Override
    public XRBResult calculate(XRBInput input) {
        // ytm
        double ytm = calculateYtm(input.schedule().flows(), input.cleanPrice(), input.valuationDate(),
                input.schedule().daycount(), input.schedule().compounding(), input.schedule().frequency());

        // accruals 
        double accruedInterest = calculateAccruedInterest(input.schedule().flows(), input.valuationDate(),
                input.schedule().daycount(), input.schedule().frequency());

        // mod duration
        double modifiedDuration = calculateModifiedDuration(input.schedule().flows(), ytm, input.valuationDate(),
                input.schedule().daycount(), input.schedule().compounding(), input.schedule().frequency());

        // dv01
        double dv01 = input.cleanPrice() + accruedInterest * modifiedDuration * 0.0001;

        // Prepara result
        CashFlowAnalytics analytics = new CashFlowAnalytics(
                input.cleanPrice() + accruedInterest, accruedInterest,
                ytm, modifiedDuration, 0., dv01, null);

        XRBResult result = new XRBResult(input.code(), input.valuationDate(), analytics);
        return result;
    }

    public PricingResult calculate(FRBInput input) {
        return null;
    }
}
