/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable discount curve (the snapshot is replaced atomically).
 *
 * Internally: days from the valuation date -> discount factor. Log-linear
 * interpolation on DFs (equivalent to a constant continuous forward rate
 * between nodes, with an ACT/365 time scale). Extrapolation uses a constant
 * continuous zero rate beyond the last node. Day count and compounding only
 * matter on input and on output (getZeroRate, getForwardRate).
 */
public final class YieldCurve implements DiscountCurve {

    private final LocalDate valuationDate;
    private final Currency currency;
    private final DaycountBasis daycount = DaycountBasis.ACT_365;

    // Immutable snapshot: days -> DF. Always contains day 0 with DF = 1.
    private volatile NavigableMap<Integer, CurveNode> dfs;

    private YieldCurve(LocalDate valuationDate, Currency currency, List<CurveNode> nodes) {
        this.valuationDate = Objects.requireNonNull(valuationDate, "valuationDate must not be null");
        this.currency = Objects.requireNonNull(currency, "currency must not be null");
        this.dfs = build(nodes);
    }

    // ------------------------------------------------------------------ construction
    public static YieldCurve fromDiscountFactors(LocalDate valuationDate, Currency currency, List<CurveNode> nodes) {
        return new YieldCurve(valuationDate, currency, nodes);
    }

    /**
     * Atomically replaces the nodes. Readers see either the old or the new
     * curve, never a mix.
     *
     * @param nodes
     */
    public void update(List<CurveNode> nodes) {
        this.dfs = build(nodes);
    }

    private static NavigableMap<Integer, CurveNode> build(List<CurveNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("No nodes provided for the curve.");
        }
        TreeMap<Integer, CurveNode> m = new TreeMap<>();
        m.put(0, new CurveNode(0, 1, null));
        for (CurveNode n : nodes) {
            if (m.put(n.days(), n) != null) {
                throw new IllegalArgumentException("Duplicate maturity at " + n.days() + " days");
            }
        }
        return Collections.unmodifiableNavigableMap(m);
    }

    // ------------------------------------------------------------------ discount factors
    @Override
    public double getDiscountFactor(LocalDate date) {
        return getDiscountFactor((int) ChronoUnit.DAYS.between(valuationDate, date));
    }

    public double getDiscountFactor(int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Date is before the valuation date");
        }
        final NavigableMap<Integer, CurveNode> snap = this.dfs;   // read the volatile field only once

        Map.Entry<Integer, CurveNode> low = snap.floorEntry(days);   // never null: day 0 is always present
        if (low.getKey() == days) {
            return low.getValue().df();
        }
        Map.Entry<Integer, CurveNode> high = snap.ceilingEntry(days);

        if (high == null) {   // beyond the last node: constant continuous zero rate
            double z = -Math.log(low.getValue().df()) / (low.getKey() / daycount.getTime());
            return Math.exp(-z * days / daycount.getTime());
        }
        double w = (double) (days - low.getKey()) / (high.getKey() - low.getKey());
        return low.getValue().df() * Math.pow(high.getValue().df() / low.getValue().df(), w);
    }

    // ------------------------------------------------------------------ static utilities
    /**
     * Discount factor equivalent to a given rate over a period of year fraction
     * tau.
     *
     * @param rate
     * @param tau
     * @param c
     * @return
     */
    public static double discountFactorFromRate(double rate, double tau, Compounding c) {
        return switch (c) {
            case CONTINUOUS ->
                Math.exp(-rate * tau);
            case SIMPLE ->
                1.0 / (1.0 + rate * tau);
            case COMPOUNDED ->
                Math.pow(1.0 + rate, -tau);
            case SIMPLE_THEN_COMPOUNDED ->
                tau <= 1.0
                ? 1.0 / (1.0 + rate * tau)
                : Math.pow(1.0 + rate, -tau);
        };
    }

    /**
     * Year fraction for day counts suitable for curve rates (no ACT/ACT: it
     * needs coupon dates).
     *
     * @param from
     * @param to
     * @param dc
     * @return
     */
    public static double yearFraction(LocalDate from, LocalDate to, DaycountBasis dc) {
        long d = ChronoUnit.DAYS.between(from, to);
        return switch (dc) {
            case ACT_360 ->
                d / 360.0;
            case ACT_365 ->
                d / 365.0;
            case NASD_30_360 ->
                thirty360(from, to, false);
            case EUR_30_360 ->
                thirty360(from, to, true);
            default ->
                throw new IllegalArgumentException("Day count not supported for curves: " + dc);
        };
    }

    // Simplified version: does not handle the end-of-February rules of US 30/360.
    private static double thirty360(LocalDate a, LocalDate b, boolean european) {
        int d1 = a.getDayOfMonth();
        int d2 = b.getDayOfMonth();
        if (european) {
            d1 = Math.min(d1, 30);
            d2 = Math.min(d2, 30);
        } else {
            if (d1 == 31) {
                d1 = 30;
            }
            if (d2 == 31 && d1 == 30) {
                d2 = 30;
            }
        }
        return (360.0 * (b.getYear() - a.getYear())
                + 30.0 * (b.getMonthValue() - a.getMonthValue())
                + (d2 - d1)) / 360.0;
    }

    public static LocalDate addOffset(LocalDate base, Offset o) {
        return switch (o.offsetType()) {
            case DAYS ->
                base.plusDays(o.step());
            case MONTHS ->
                base.plusMonths(o.step());
            case YEARS ->
                base.plusYears(o.step());
            default ->
                throw new IllegalArgumentException("Offset type not supported: " + o.offsetType());
        };
    }

    // ------------------------------------------------------------------ getters
    @Override
    public LocalDate getValuationDate() {
        return valuationDate;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Nodes (excluding day 0), ordered by maturity.
     *
     * @return
     */
    public List<CurveNode> getNodes() {
        return dfs.entrySet().stream()
                .filter(e -> e.getKey() > 0)
                .map(e -> new CurveNode(e.getKey(), e.getValue().df(), e.getValue().mq()))
                .collect(Collectors.toList());
    }

    public List<OrderedDiscountFactor> getOrderedDiscountFactors() {
        final NavigableMap<Integer, CurveNode> snap = this.dfs;
        return snap.entrySet().stream()
                .map(e -> new OrderedDiscountFactor(valuationDate.plusDays(e.getKey()), e.getValue().df(), e.getKey()))
                .collect(Collectors.toList());
    }
}
