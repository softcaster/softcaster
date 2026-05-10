/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.dto.IOptionInputData;
import org.softcaster.engine.dto.MarketOutputData;

public interface IOptionPricer<T extends IOptionInputData> {

    MarketOutputData priceCall(T input);

    MarketOutputData pricePut(T input);
}
