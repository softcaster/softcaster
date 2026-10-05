/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.curve;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Computes z-spread buckets (e.g. 1Y, 2Y, 5Y...) from a table of bonds with
 * their z-spreads. The calculation works on SpreadPoint objects, so it is
 * independent of where the data comes from (CSV today, database later).
 */
public final class SpreadBuckets {

    private SpreadBuckets() {
    }

    /**
     * zSpread is a continuous ACT/365 decimal (0.0095 = 95 bp); null if the
     * bucket could not be computed.
     */
    public record Bucket(int years, Double zSpread, int nBonds) {

        public static Bucket empty(int years) {
            return new Bucket(years, null, 0);
        }
    }

    /**
     * One bond: t = ACT/365 years from the official date to maturity, z = its
     * z-spread.
     */
    public record SpreadPoint(String code, double t, double z) {

    }

    // ------------------------------------------------------------------ CSV reader
    /**
     * Reads a CSV with a header row containing the columns "Code", "Maturity
     * Date" (yyyy-MM-dd) and "Z-Spread". With a delimiter other than ',' the
     * decimal comma is accepted ("0,0095"). Quoted fields containing the
     * delimiter are not supported.
     */
    public static List<SpreadPoint> readCsv(Path file, char delimiter, LocalDate officialDate,
            Set<String> excludedCodes) throws IOException {
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Empty file: " + file);
        }
        String[] header = split(lines.get(0), delimiter);
        int iCode = indexOf(header, "Code");
        int iMat = indexOf(header, "Maturity Date");
        int iZ = indexOf(header, "Z-Spread");

        List<SpreadPoint> out = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            String[] f = split(line, delimiter);
            try {
                String code = f[iCode].trim();
                if (excludedCodes.contains(code)) {
                    continue;
                }
                LocalDate maturity = LocalDate.parse(f[iMat].trim());
                double z = parseNumber(f[iZ], delimiter);
                double t = ChronoUnit.DAYS.between(officialDate, maturity) / 365.0;
                out.add(new SpreadPoint(code, t, z));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Invalid row at line " + (i + 1) + ": " + line, e);
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ calculation
    /**
     * For each bucket T, takes the median z-spread of the bonds whose t lies
     * within T +/- window, where window = min(max(0.5, 0.2 * T), 2.0) years.
     * Buckets with fewer than minBonds bonds are returned with zSpread = null.
     * Returns a new array: the template is not modified.
     * @param points
     * @param template
     * @param minBonds
     * @param minT
     * @param maxT
     * @return 
     */
    public static Bucket[] compute(List<SpreadPoint> points, Bucket[] template,
            int minBonds, double minT, double maxT) {
        Bucket[] out = new Bucket[template.length];
        for (int i = 0; i < template.length; i++) {
            int years = template[i].years();
            double window = Math.min(Math.max(0.5, 0.2 * years), 2.0);

            double[] z = points.stream()
                    .filter(p -> p.t() >= minT && p.t() <= maxT) // exclude very short and very long bonds
                    .filter(p -> Math.abs(p.t() - years) <= window)
                    .mapToDouble(SpreadPoint::z)
                    .sorted()
                    .toArray();

            out[i] = (z.length < minBonds)
                    ? new Bucket(years, null, z.length)
                    : new Bucket(years, median(z), z.length);
        }
        return out;
    }

    // ------------------------------------------------------------------ helpers
    private static double median(double[] sorted) {
        int n = sorted.length;
        return (n % 2 == 1) ? sorted[n / 2] : 0.5 * (sorted[n / 2 - 1] + sorted[n / 2]);
    }

    private static String[] split(String line, char delimiter) {
        return line.split(java.util.regex.Pattern.quote(String.valueOf(delimiter)), -1);
    }

    private static int indexOf(String[] header, String name) {
        for (int i = 0; i < header.length; i++) {
            if (header[i].trim().equalsIgnoreCase(name)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Column not found: " + name);
    }

    private static double parseNumber(String s, char delimiter) {
        String x = s.trim();
        if (delimiter != ',') {
            x = x.replace(',', '.');
        }
        return Double.parseDouble(x);
    }
}
