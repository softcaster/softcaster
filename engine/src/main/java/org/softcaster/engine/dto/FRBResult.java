/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.List;

public record FRBResult(String instrumentId, LocalDate valuationDate,
        CashFlowAnalytics analytics,
        double discountMargin,
        double marginDuration,
        double realMarginDuration,
        List<ProjectedCoupon> projectedCoupons) implements CashFlowResult {

}
