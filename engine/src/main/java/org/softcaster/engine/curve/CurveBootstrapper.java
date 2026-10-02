/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;

/**
 * Bootstraps a projection curve from deposits (MONEY_MARKET, simple rate) and
 * swap par rates (SWAP, annual fixed leg 30/360).
 *
 * Output: (days, DF) nodes ready for YieldCurve.fromDiscountFactors.
 *
 * Simplifications: no spot lag, no business-day adjustment, fixed leg with tau
 * = 1 exactly, single-curve.
 */
public final class CurveBootstrapper {

    private CurveBootstrapper() {
    }

    public static List<CurveNode> bootstrap(LocalDate valuationDate, List<MarketQuote> quotes) {
        Objects.requireNonNull(valuationDate, "valuationDate must not be null");
        if (quotes == null || quotes.isEmpty()) {
            throw new IllegalArgumentException("No quotes provided.");
        }

        // 1. Split deposits, swaps and zero_rates
        List<MarketQuote> zeroRates = new ArrayList<>();
        List<MarketQuote> depos = new ArrayList<>();
        TreeMap<Integer, MarketQuote> swaps = new TreeMap<>();   // years -> quote
        for (MarketQuote q : quotes) {
            switch (q.nodeType()) {
                case ZERO_RATES ->
                    zeroRates.add(q);
                case MONEY_MARKET ->
                    depos.add(q);
                case SWAP -> {
                    Offset o = q.tenorOffset();
                    if (o.offsetType() != OffsetType.YEARS || o.step() <= 0) {
                        throw new IllegalArgumentException("Swap tenor must be a positive number of years: " + q.symbol());
                    }
                    if (q.daycount() != DaycountBasis.NASD_30_360 && q.daycount() != DaycountBasis.EUR_30_360) {
                        throw new IllegalArgumentException("Swap fixed leg must be 30/360: " + q.symbol());
                    }
                    if (swaps.put((int) o.step(), q) != null) {
                        throw new IllegalArgumentException("Duplicate swap: " + q.symbol());
                    }
                }
                default ->
                    throw new IllegalArgumentException("Unsupported node type: " + q.nodeType());
            }
        }

        List<CurveNode> result = new ArrayList<>();

        // 2. Swaps: sequential bootstrap on the annual grid 1..maxYears
        //    DF_n = (1 - S_n * annuity_{n-1}) / (1 + S_n), annuity = sum of previous annual DFs
        int firstSwapDays = Integer.MAX_VALUE;
        if (!swaps.isEmpty()) {
            int maxYears = swaps.lastKey();
            double annuity = 0.0;
            for (int n = 1; n <= maxYears; n++) {
                double s = parRate(swaps, n);
                double df = (1.0 - s * annuity) / (1.0 + s);
                if (!(df > 0.0)) {
                    throw new IllegalStateException("Bootstrap failed at " + n + "Y: DF=" + df);
                }
                annuity += df;
                int days = (int) ChronoUnit.DAYS.between(valuationDate, valuationDate.plusYears(n));
                result.add(new CurveNode(days, df, swaps.get(n)));
            }
            firstSwapDays = result.get(0).days();
        }

        // 3. Deposits: DF = 1 / (1 + r * days/basis); those maturing on or after the first swap are covered by swaps
        for (MarketQuote d : depos) {
            if (d.daycount() != DaycountBasis.ACT_360 && d.daycount() != DaycountBasis.ACT_365) {
                throw new IllegalArgumentException("Unsupported deposit day count: " + d.symbol());
            }
            if (d.compounding() != Compounding.SIMPLE) {
                throw new IllegalArgumentException("Deposit must use SIMPLE compounding: " + d.symbol());
            }
            LocalDate mat = YieldCurve.addOffset(valuationDate, d.tenorOffset());
            int days = (int) ChronoUnit.DAYS.between(valuationDate, mat);
            if (days <= 0) {
                throw new IllegalArgumentException("Deposit maturity must be positive: " + d.symbol());
            }
            if (days >= firstSwapDays) {
                continue;
            }
            double tau = YieldCurve.yearFraction(valuationDate, mat, d.daycount());
            result.add(new CurveNode(days, YieldCurve.discountFactorFromRate(d.rate(), tau, d.compounding()), d));
        }

        // 4. Zero rates: DF depends on each quote's compounding/day count
        for (MarketQuote zr : zeroRates) {
            if (zr.daycount() != DaycountBasis.ACT_360 && zr.daycount() != DaycountBasis.ACT_365) {
                throw new IllegalArgumentException("Unsupported zero-rate day count: " + zr.symbol());
            }
            if (zr.compounding() != Compounding.COMPOUNDED && zr.compounding() != Compounding.CONTINUOUS) {
                throw new IllegalArgumentException("Zero rates must use COMPOUNDED or CONTINUOUS compounding: " + zr.symbol());
            }
            LocalDate mat = YieldCurve.addOffset(valuationDate, zr.tenorOffset());
            int days = (int) ChronoUnit.DAYS.between(valuationDate, mat);
            if (days <= 0) {
                throw new IllegalArgumentException("Zero-rate maturity must be positive: " + zr.symbol());
            }
            double tau = YieldCurve.yearFraction(valuationDate, mat, zr.daycount());
            result.add(new CurveNode(days, YieldCurve.discountFactorFromRate(zr.rate(), tau, zr.compounding()), zr));
        }

        result.sort(Comparator.comparingInt(CurveNode::days));
        return result;
    }

    /**
     * Par rate at year n: the exact quote if present, otherwise linear
     * interpolation.
     */
    private static double parRate(TreeMap<Integer, MarketQuote> swaps, int n) {
        MarketQuote exact = swaps.get(n);
        if (exact != null) {
            return exact.rate();
        }

        Map.Entry<Integer, MarketQuote> lo = swaps.floorEntry(n);
        Map.Entry<Integer, MarketQuote> hi = swaps.ceilingEntry(n);   // not null: n <= maxYears
        if (lo == null) {
            return hi.getValue().rate();                  // before the first pillar: flat
        }
        double w = (double) (n - lo.getKey()) / (hi.getKey() - lo.getKey());
        return lo.getValue().rate() + w * (hi.getValue().rate() - lo.getValue().rate());
    }
}
