/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;

public record GranularCurvePoint(
        LocalDate deliveryDate,
        double price,
        Integer sourceMarketQuoteId, // tracciabilita': da quale market_quote deriva
        Integer sourceShapeProfileId // tracciabilita': da quale shape_profile deriva
        ) {

}
