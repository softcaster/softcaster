/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

/**
 * What the curve stores: calendar days from the valuation date and the discount factor.
 * The DF is a pure number, with no day count or compounding attached.
 * MarketQuote mq serve per salvare la curva su db
 */
public record CurveNode(int days, double df, MarketQuote mq) {

    public CurveNode {
        if (days < 0) {
            throw new IllegalArgumentException("days must be >= 0 : " + days);
        }
        if (!Double.isFinite(df) || df <= 0.0) {
            throw new IllegalArgumentException("Invalid discount factor at " + days + " days: " + df);
        }
    }
}