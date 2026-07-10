/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Frequency;

/**
 *
 * @author ep
 */
public class XRBInputData extends MarketInputData {

    private List<CashFlow> flows;
    private Frequency frequency;

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
    @Override
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    /**
     * @return the flows
     */
    public List<CashFlow> getFlows() {
        return flows;
    }

    /**
     * @param flows the flows to set
     */
    public void setFlows(List<CashFlow> flows) {
        this.flows = flows;
    }
}
