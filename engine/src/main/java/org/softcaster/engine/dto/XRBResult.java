/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

public record XRBResult(String instrumentId, LocalDate valuationDate,
        CashFlowAnalytics analytics) implements CashFlowResult {

}
