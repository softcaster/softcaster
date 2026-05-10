/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;

/**
 *
 * @author softc
 */
public class BondForwardInputData extends ForwardBaseInputData {
    
    // Lista cedole sottostante
    private List<CashFlow> underliyngCashFlows = null;
    
    private Compounding compounding;

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
