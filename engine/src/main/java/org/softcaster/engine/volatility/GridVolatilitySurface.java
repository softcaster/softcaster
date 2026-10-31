package org.softcaster.engine.volatility;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Superficie da una griglia di punti (scadenza, strike, volatilita').
 *
 * Per scadenza: smile lineare nello strike, piatto fuori dagli strike
 * quotati; una scadenza con il solo punto ATM ha volatilita' costante.
 * Tra le scadenze: interpolazione lineare della varianza totale sigma^2 t
 * (nessun arbitraggio di calendario se la varianza cresce); prima della prima
 * e dopo l'ultima scadenza si usa lo smile piu' vicino. Le scadenze non
 * successive alla data di riferimento sono scartate.
 */
public final class GridVolatilitySurface implements VolatilitySurface {

    private final LocalDate referenceDate;
    private final TreeMap<LocalDate, Smile> smiles = new TreeMap<>();

    public GridVolatilitySurface(LocalDate referenceDate, List<VolatilityNode> nodes) {
        this.referenceDate = Objects.requireNonNull(referenceDate, "referenceDate");
        Map<LocalDate, List<VolatilityNode>> byExpiry = new TreeMap<>();
        for (VolatilityNode n : nodes) {
            if (n.expiry().isAfter(referenceDate)) {
                byExpiry.computeIfAbsent(n.expiry(), e -> new ArrayList<>()).add(n);
            }
        }
        byExpiry.forEach((expiry, list) -> smiles.put(expiry, Smile.of(list)));
        if (smiles.isEmpty()) {
            throw new IllegalArgumentException("No expiry after " + referenceDate);
        }
    }

    @Override
    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    @Override
    public double volatility(LocalDate expiry, double strike) {
        Map.Entry<LocalDate, Smile> below = smiles.floorEntry(expiry);
        Map.Entry<LocalDate, Smile> above = smiles.ceilingEntry(expiry);
        if (below == null) {
            return above.getValue().volatility(strike);
        }
        if (above == null || below.getKey().equals(above.getKey())) {
            return below.getValue().volatility(strike);
        }
        double t = years(expiry);
        double t1 = years(below.getKey());
        double t2 = years(above.getKey());
        double v1 = below.getValue().volatility(strike);
        double v2 = above.getValue().volatility(strike);
        double w = v1 * v1 * t1 + (v2 * v2 * t2 - v1 * v1 * t1) * (t - t1) / (t2 - t1);
        return t > 0 && w > 0 ? Math.sqrt(w / t) : v1;
    }

    private double years(LocalDate date) {
        return ChronoUnit.DAYS.between(referenceDate, date) / 365.0;
    }

    /**
     * Smile di una scadenza: strike ordinati, oppure solo ATM.
     */
    private record Smile(double[] strikes, double[] vols, double atm) {

        static Smile of(List<VolatilityNode> nodes) {
            List<VolatilityNode> byStrike = nodes.stream()
                    .filter(n -> n.strike() != null)
                    .sorted((a, b) -> Double.compare(a.strike(), b.strike()))
                    .toList();
            double atm = nodes.stream().filter(n -> n.strike() == null)
                    .mapToDouble(VolatilityNode::volatility).findFirst().orElse(Double.NaN);
            double[] strikes = byStrike.stream().mapToDouble(VolatilityNode::strike).toArray();
            double[] vols = byStrike.stream().mapToDouble(VolatilityNode::volatility).toArray();
            return new Smile(strikes, vols, atm);
        }

        double volatility(double strike) {
            if (strikes.length == 0) {
                return atm;
            }
            if (strike <= strikes[0]) {
                return vols[0];
            }
            int last = strikes.length - 1;
            if (strike >= strikes[last]) {
                return vols[last];
            }
            int i = 1;
            while (strikes[i] < strike) {
                i++;
            }
            double w = (strike - strikes[i - 1]) / (strikes[i] - strikes[i - 1]);
            return vols[i - 1] + w * (vols[i] - vols[i - 1]);
        }
    }
}
