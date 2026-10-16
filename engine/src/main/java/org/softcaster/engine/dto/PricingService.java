/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.Objects;
import org.softcaster.engine.analytics.BondPricer;

public final class PricingService {

    private final BondPricer xrbPricer;
    private final BondPricer frbPricer;

    // uso normale: nessuna configurazione richiesta
    public PricingService() {
        this(new BondPricer(), new BondPricer()/*FRBPricer()*/);
    }

    // per test o configurazioni particolari
    public PricingService(BondPricer xrbPricer, BondPricer /*FRBPricer*/ frbPricer) {
        this.xrbPricer = Objects.requireNonNull(xrbPricer);
        this.frbPricer = Objects.requireNonNull(frbPricer);
    }

    public PricingResult price(PricingInput input) {
        if (input instanceof XRBInputData x) {
            return xrbPricer.calculate(x);
        }
        if (input instanceof FRBInputData f) {
            return frbPricer.calculate(f);
        }
        throw new IllegalArgumentException(
                "Strumento non supportato: " + input.getClass().getSimpleName());
    }
}
