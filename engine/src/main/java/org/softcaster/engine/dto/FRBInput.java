/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

public record FRBInput(String code,
        LocalDate valuationDate,
        double cleanPrice,
        CashFlowSchedule schedule,
        FloatingCouponSpec floating) implements CashFlowInput {

}
