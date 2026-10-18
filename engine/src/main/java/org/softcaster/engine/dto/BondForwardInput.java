/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

public record BondForwardInput(
        String instrumentId,
        LocalDate valuationDate,
        LocalDate deliveryDate,
        XRBInput underlying, // composizione: riuso il bond che ho già
        double contractPrice,
        String repoCurveId) implements ForwardInput {

}
