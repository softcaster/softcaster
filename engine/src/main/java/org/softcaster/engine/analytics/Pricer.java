/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.PricingInput;
import org.softcaster.engine.dto.PricingResult;

public interface Pricer<In extends PricingInput, Out extends PricingResult> {
    Out calculate(In input);
}