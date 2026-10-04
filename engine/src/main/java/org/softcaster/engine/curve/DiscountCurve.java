/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;

public interface DiscountCurve {

    LocalDate getValuationDate();

    double getDiscountFactor(LocalDate date);

    default double getZeroRate(LocalDate date, DaycountBasis dc, Compounding c) {
        return getForwardRate(getValuationDate(), date, dc, c);
    }

    default double getForwardRate(LocalDate start, LocalDate end, DaycountBasis dc, Compounding c) {
        if (start.isBefore(getValuationDate())) {
            throw new IllegalArgumentException("Start date is before the valuation date");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End date must be after the start date");
        }
        double tau = YieldCurve.yearFraction(start, end, dc);
        double growth = getDiscountFactor(start) / getDiscountFactor(end);
        return switch (c) {
            case CONTINUOUS ->
                Math.log(growth) / tau;
            case SIMPLE ->
                (growth - 1.0) / tau;
            case COMPOUNDED ->
                Math.pow(growth, 1.0 / tau) - 1.0;
            case SIMPLE_THEN_COMPOUNDED ->
                tau <= 1.0 ? (growth - 1.0) / tau : Math.pow(growth, 1.0 / tau) - 1.0;
        };
    }
}
