/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.Objects;
import org.softcaster.engine.analytics.XRBPricer;

public final class PricingService {

    private final XRBPricer xrbPricer;
    private final XRBPricer frbPricer;

    // uso normale: nessuna configurazione richiesta
    public PricingService() {
        this(new XRBPricer(), new XRBPricer()/*FRBPricer()*/);
    }

    // per test o configurazioni particolari
    public PricingService(XRBPricer xrbPricer, XRBPricer /*FRBPricer*/ frbPricer) {
        this.xrbPricer = Objects.requireNonNull(xrbPricer);
        this.frbPricer = Objects.requireNonNull(frbPricer);
    }

    public PricingResult price(PricingInput input) {
        if (input instanceof XRBInput x) {
            return xrbPricer.calculate(x);
        }
        if (input instanceof FRBInput f) {
            return frbPricer.calculate(f);
        }
        throw new IllegalArgumentException(
                "Strumento non supportato: " + input.getClass().getSimpleName());
    }
}
