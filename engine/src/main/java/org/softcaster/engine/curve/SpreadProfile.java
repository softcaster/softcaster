/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

/** Immutable spread profile: continuous z-spread as a function of ACT/365 years from the curve date. */
public final class SpreadProfile {
    private final double[] t;
    private final double[] s;

    private SpreadProfile(double[] t, double[] s) { this.t = t; this.s = s; }

    /** buckets: tenor -> z-spread (decimal). The point (0, 0) is added automatically.
     * @param valuationDate
     * @param buckets
     * @return  */
    public static SpreadProfile of(LocalDate valuationDate, Map<Offset, Double> buckets) {
        TreeMap<Double, Double> m = new TreeMap<>();
        m.put(0.0, 0.0);
        for (var e : buckets.entrySet()) {
            LocalDate mat = YieldCurve.addOffset(valuationDate, e.getKey());   // make it public
            double years = ChronoUnit.DAYS.between(valuationDate, mat) / 365.0;
            if (years <= 0 || m.put(years, e.getValue()) != null) {
                throw new IllegalArgumentException("Invalid or duplicate spread tenor: " + e.getKey());
            }
        }
        return new SpreadProfile(
                m.keySet().stream().mapToDouble(Double::doubleValue).toArray(),
                m.values().stream().mapToDouble(Double::doubleValue).toArray());
    }

    public double at(double years) {
        int n = t.length;
        if (years <= t[0]) return s[0];
        if (years >= t[n - 1]) return s[n - 1];          // flat beyond the last bucket
        int i = 1;
        while (t[i] < years) i++;
        double w = (years - t[i - 1]) / (t[i] - t[i - 1]);
        return s[i - 1] + w * (s[i] - s[i - 1]);
    }
}
