/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 *
 * @author softc
 */
public record FRBInputData(String isin,
        LocalDate valuationDate,
        CashFlowSchedule schedule,
        double cleanPrice,
        FloatingCouponSpec floating) implements PricingInput {

}
