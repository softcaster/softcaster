/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;

/**
 *
 * @author softc
 */
public class XRBForwardInputData extends ForwardBaseInputData {
    
    // Lista cedole sottostante
    private List<CashFlow> underliyngCashFlows = null;
    private double conversionFactor = 1;
    
    /**
     * @return the underliyngCashFlows
     */
    public List<CashFlow> getUnderliyngCashFlows() {
        return underliyngCashFlows;
    }

    /**
     * @param underliyngCashFlows the underliyngCashFlows to set
     */
    public void setUnderliyngCashFlows(List<CashFlow> underliyngCashFlows) {
        this.underliyngCashFlows = underliyngCashFlows;
    }

    /**
     * @return the conversionFactot
     */
    public double getConversionFactor() {
        return conversionFactor;
    }

    /**
     * @param conversionFactot the conversionFactot to set
     */
    public void setConversionFactor(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }
}
