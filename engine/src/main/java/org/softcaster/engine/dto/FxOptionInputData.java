/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import org.softcaster.engine.enums.OptionStyle;
import org.softcaster.engine.enums.OptionType;

/**
 *
 * @author softc
 */
public class FxOptionInputData extends FxForwardInputData implements IOptionInputData{

    private OptionData optionData;

    /**
     * @return the strike
     */
    public double getStrike() {
        return getOptionData().strike();
    }

    /**
     * @return the volatility
     */
    public double getVolatility() {
        return getOptionData().volatility();
    }

    /**
     * @return the type
     */
    public OptionType getOptionType() {
        return getOptionData().type();
    }

    public OptionStyle getOptionStyle() {
        return getOptionData().style();
    }

    /**
     * @return the optionData
     */
    public OptionData getOptionData() {
        return optionData;
    }

    /**
     * @param optionData the optionData to set
     */
    public void setOptionData(OptionData optionData) {
        this.optionData = optionData;
    }
}
