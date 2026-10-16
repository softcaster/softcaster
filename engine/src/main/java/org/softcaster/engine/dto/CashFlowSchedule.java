/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

public record CashFlowSchedule(List<CashFlow> flows,
        DaycountBasis daycount,
        Frequency frequency,
        Compounding compounding) {

    public CashFlowSchedule {
        // Copia difensiva, serve a rendere il record davvero immutabile.
        // I campi sono final, ma se un campo è una List, la lista stessa può ancora essere modificata.
        // Con la copia la lista in input non sara'mai modificata
        flows = List.copyOf(flows);
    }

}
