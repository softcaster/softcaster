/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.util.Objects;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;

/**
 * Raw market quote (deposit, swap par rate, ECB zero rate). Replaces
 * CurveNodeInput: it carries no discount factor. The rate is a decimal (0.0365
 * = 3.65%).
 */
public record MarketQuote(
        String symbol,
        Offset tenorOffset,
        double rate,
        DaycountBasis daycount,
        Compounding compounding,
        CurveNodeType nodeType) {

    public MarketQuote      {
        Objects.requireNonNull(symbol, "symbol must not be null");
        Objects.requireNonNull(tenorOffset, "tenorOffset must not be null");
        Objects.requireNonNull(daycount, "daycount must not be null");
        Objects.requireNonNull(compounding, "compounding must not be null");
        Objects.requireNonNull(nodeType, "nodeType must not be null");
        if (!Double.isFinite(rate)) {
            throw new IllegalArgumentException("Invalid rate for " + symbol + ": " + rate);
        }
    }
}
