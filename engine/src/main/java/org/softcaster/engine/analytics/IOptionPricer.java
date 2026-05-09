/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.OptionCalcInputData;
import org.softcaster.engine.dto.OptionCalcOutputData;

/**
 *
 * @author ep
 */
public interface IOptionPricer {
    public OptionCalcOutputData priceCall(OptionCalcInputData input);
    public OptionCalcOutputData pricePut(OptionCalcInputData input);
}
