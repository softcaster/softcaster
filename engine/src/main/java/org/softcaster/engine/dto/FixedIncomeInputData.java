/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author softc
 */
public class FixedIncomeInputData extends MarketInputData {

    private Frequency frequency;
    private Compounding compounding;

    /**
     * @return the frequency
     */
    @Override
    public Frequency getFrequency() {
        return frequency;
    }

    /**
     * @param frequency the frequency to set
     */
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the compounding
     */
    public Compounding getCompounding() {
        return compounding;
    }

    /**
     * @param compounding the compounding to set
     */
    public void setCompounding(Compounding compounding) {
        this.compounding = compounding;
    }
}
