/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.IOptionInputData;
import org.softcaster.engine.dto.OptionCalcOutputData;

public interface IOptionPricer<T extends IOptionInputData> {

    OptionCalcOutputData priceCall(T input);

    OptionCalcOutputData pricePut(T input);
}
