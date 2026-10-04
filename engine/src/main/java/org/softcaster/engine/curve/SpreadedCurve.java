/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.softcaster.engine.enums.DaycountBasis;

public final class SpreadedCurve implements DiscountCurve {
    private final DiscountCurve base;
    private final SpreadProfile spreads;

    public SpreadedCurve(DiscountCurve base, SpreadProfile spreads) {
        this.base = Objects.requireNonNull(base);
        this.spreads = Objects.requireNonNull(spreads);
    }

    @Override public LocalDate getValuationDate() { 
        return base.getValuationDate(); 
    }

    @Override public double getDiscountFactor(LocalDate date) {
        // l t è sempre ACT/365 dalla data della curva, la stessa scala del solver.
        double t = ChronoUnit.DAYS.between(base.getValuationDate(), date) / DaycountBasis.ACT_365.getTime();
        return base.getDiscountFactor(date) * Math.exp(-spreads.at(t) * t);
    }
}