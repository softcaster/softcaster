/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.List;

public record CashFlowAnalytics(
        double dirtyPrice,  
        double accruedInterest,
        double yield,
        double modifiedDuration,
        double convexity,
        double dv01,
        List<CashFlowDetail> flows) {      // flussi con DF, PV, ecc.

    public CashFlowAnalytics {
        if (flows != null) {
            flows = List.copyOf(flows);
        }
    }
}
