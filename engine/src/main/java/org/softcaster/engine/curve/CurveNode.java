/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

/**
 * What the curve stores: calendar days from the valuation date, the discount factor, and the
 * market quote the node comes from (null for nodes derived by interpolation on the bootstrap grid).
 */
public record CurveNode(int days, double df, MarketQuote mq) {

    public CurveNode {
        if (days < 0) {
            throw new IllegalArgumentException("days must be >= 0: " + days);
        }
        if (!Double.isFinite(df) || df <= 0.0) {
            throw new IllegalArgumentException("Invalid discount factor at " + days + " days: " + df);
        }
        // mq is intentionally nullable
    }

    /** Node without an associated quote (derived or implicit). */
    public CurveNode(int days, double df) {
        this(days, df, null);
    }

    /** True if the node comes directly from a market quote. */
    public boolean isMarketNode() {
        return mq != null;
    }
}
