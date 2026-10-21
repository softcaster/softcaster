package org.softcaster.engine.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;

/**
 * Builds (days, DF) nodes from market quotes.
 *
 * Short end: deposits (MONEY_MARKET, simple rate). Long end: exactly ONE of the
 * following families per curve: - SWAP par swap rates, annual fixed leg 30/360
 * (tau = 1) - OIS par overnight-index swap rates (EUR €STR, USD SOFR: annual
 * fixed leg ACT/360; GBP SONIA: ACT/365). Fixed-leg accrual is the actual
 * number of days of each year divided by 360 (or 365): DF_n = (1 - S * A_{n-1})
 * / (1 + S * tau_n), A = sum of tau_k * DF_k - PAR_YIELD par yields of coupon
 * bonds, semiannual coupons (e.g. BTP benchmarks) - ZERO_RATES zero rates,
 * converted with their own compounding / day count
 *
 * Simplifications: no spot lag, no business-day adjustment, regular coupon
 * periods, single-curve.
 */
public final class CurveBootstrapper {

    /**
     * How a PAR_YIELD quote is expressed by the data source.
     */
    public enum ParYieldConvention {
        /**
         * Annual effective yield: periodic (semiannual) coupon = (1 + y)^(1/2)
         * - 1.
         */
        ANNUAL_EFFECTIVE,
        /**
         * Nominal yield compounded semiannually: periodic coupon = y / 2.
         */
        SEMIANNUAL_NOMINAL
    }

    private static final int PAR_STEP_MONTHS = 6;

    private CurveBootstrapper() {
    }

    public static List<CurveNode> bootstrap(LocalDate valuationDate, List<MarketQuote> quotes) {
        return bootstrap(valuationDate, quotes, ParYieldConvention.ANNUAL_EFFECTIVE);
    }

    public static List<CurveNode> bootstrap(LocalDate valuationDate, List<MarketQuote> quotes,
            ParYieldConvention parConvention) {
        Objects.requireNonNull(valuationDate, "valuationDate must not be null");
        Objects.requireNonNull(parConvention, "parConvention must not be null");
        if (quotes == null || quotes.isEmpty()) {
            throw new IllegalArgumentException("No quotes provided.");
        }

        // 1. Split quotes by type
        List<MarketQuote> depos = new ArrayList<>();
        List<MarketQuote> zeroRates = new ArrayList<>();
        TreeMap<Integer, MarketQuote> swaps = new TreeMap<>();        // years -> quote
        TreeMap<Integer, MarketQuote> oisSwaps = new TreeMap<>();     // years -> quote
        TreeMap<Integer, MarketQuote> parYields = new TreeMap<>();    // months -> quote

        for (MarketQuote q : quotes) {
            switch (q.nodeType()) {
                case MONEY_MARKET ->
                    depos.add(q);
                case ZERO_RATES ->
                    zeroRates.add(q);
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
                case OIS -> {
                    Offset o = q.tenorOffset();
                    if (o.offsetType() != OffsetType.YEARS || o.step() <= 0) {
                        throw new IllegalArgumentException("OIS tenor must be a positive number of years: " + q.symbol());
                    }
                    if (q.daycount() != DaycountBasis.ACT_360 && q.daycount() != DaycountBasis.ACT_365) {
                        throw new IllegalArgumentException("OIS fixed leg must be ACT/360 or ACT/365: " + q.symbol());
                    }
                    if (!oisSwaps.isEmpty() && oisSwaps.firstEntry().getValue().daycount() != q.daycount()) {
                        throw new IllegalArgumentException("OIS quotes of one curve must share the day count: " + q.symbol());
                    }
                    if (oisSwaps.put((int) o.step(), q) != null) {
                        throw new IllegalArgumentException("Duplicate OIS: " + q.symbol());
                    }
                }
                case PAR_YIELD -> {
                    int months = tenorInMonths(q);
                    if (months <= 0 || months % PAR_STEP_MONTHS != 0) {
                        throw new IllegalArgumentException(
                                "Par-yield tenor must be a positive multiple of 6 months: " + q.symbol());
                    }
                    if (parYields.put(months, q) != null) {
                        throw new IllegalArgumentException("Duplicate par yield: " + q.symbol());
                    }
                }
                default ->
                    throw new IllegalArgumentException("Unsupported node type: " + q.nodeType());
            }
        }

        int families = (swaps.isEmpty() ? 0 : 1) + (oisSwaps.isEmpty() ? 0 : 1)
                + (parYields.isEmpty() ? 0 : 1) + (zeroRates.isEmpty() ? 0 : 1);
        if (families > 1) {
            throw new IllegalArgumentException(
                    "Swaps, OIS, par yields and zero rates cannot be mixed in the same curve.");
        }

        List<CurveNode> result = new ArrayList<>();

        // 2. Swaps or OIS (annual grid), then deposits shorter than the first swap
        int firstSwapDays = Integer.MAX_VALUE;
        if (!swaps.isEmpty() || !oisSwaps.isEmpty()) {
            List<CurveNode> swapNodes;
            if (!swaps.isEmpty()) {
                swapNodes = bootstrapSwaps(valuationDate, swaps, 0.0);                 // tau = 1 (30/360)
            } else {
                double basisDays = oisSwaps.firstEntry().getValue().daycount() == DaycountBasis.ACT_365 ? 365.0 : 360.0;
                swapNodes = bootstrapSwaps(valuationDate, oisSwaps, basisDays);        // tau = actual days / basis
            }
            result.addAll(swapNodes);
            firstSwapDays = swapNodes.get(0).days();
        }
        List<CurveNode> depoNodes = depositNodes(valuationDate, depos, firstSwapDays);
        result.addAll(depoNodes);

        // 3. Long end beyond the last deposit
        int cutoffDays = depoNodes.stream().mapToInt(CurveNode::days).max().orElse(0);
        if (!parYields.isEmpty()) {
            result.addAll(bootstrapParYields(valuationDate, parYields, depoNodes, cutoffDays, parConvention));
        }
        if (!zeroRates.isEmpty()) {
            result.addAll(zeroRateNodes(valuationDate, zeroRates, cutoffDays));
        }

        result.sort(Comparator.comparingInt(CurveNode::days));
        return result;
    }

    // ------------------------------------------------------------------ swaps
    /**
     * DF_n = (1 - S_n * A_{n-1}) / (1 + S_n * tau_n), A = sum of tau_k * DF_k
     * over the previous annual dates. basisDays = 0: tau = 1 for every year
     * (par swap, 30/360). basisDays = 360 or 365: tau = actual days of the year
     * / basisDays (OIS, fixed leg ACT/360 or ACT/365).
     */
    private static List<CurveNode> bootstrapSwaps(LocalDate valuationDate, TreeMap<Integer, MarketQuote> swaps,
            double basisDays) {
        List<CurveNode> out = new ArrayList<>();
        int maxYears = swaps.lastKey();
        double annuity = 0.0;
        LocalDate previous = valuationDate;
        for (int n = 1; n <= maxYears; n++) {
            LocalDate date = valuationDate.plusYears(n);
            double tau = basisDays > 0.0 ? ChronoUnit.DAYS.between(previous, date) / basisDays : 1.0;
            double s = interpolateRate(swaps, n);
            double df = (1.0 - s * annuity) / (1.0 + s * tau);
            if (!(df > 0.0)) {
                throw new IllegalStateException("Swap bootstrap failed at " + n + "Y: DF=" + df);
            }
            annuity += tau * df;
            int days = (int) ChronoUnit.DAYS.between(valuationDate, date);
            out.add(new CurveNode(days, df, swaps.get(n)));          // null on interpolated years
            previous = date;
        }
        return out;
    }

    // ------------------------------------------------------------------ deposits
    private static List<CurveNode> depositNodes(LocalDate valuationDate, List<MarketQuote> depos, int dropFromDays) {
        List<CurveNode> out = new ArrayList<>();
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
            if (days >= dropFromDays) {
                continue;   // covered by the long end
            }
            double tau = YieldCurve.yearFraction(valuationDate, mat, d.daycount());
            out.add(new CurveNode(days, YieldCurve.discountFactorFromRate(d.rate(), tau, Compounding.SIMPLE), d));
        }
        out.sort(Comparator.comparingInt(CurveNode::days));
        return out;
    }

    // ------------------------------------------------------------------ par yields
    /**
     * Semiannual grid 6M, 12M, ... up to the last quoted tenor. A par bond with
     * periodic coupon c satisfies: DF_n = (1 - c * A_{n-1}) / (1 + c), where
     * A_{n-1} is the sum of the DFs of all previous coupon dates. Grid dates up
     * to the last deposit use the deposit DFs (log-linear), so the join is
     * consistent. Par yields are interpolated linearly in tenor between quotes
     * (flat before the first quote).
     */
    private static List<CurveNode> bootstrapParYields(LocalDate valuationDate,
            TreeMap<Integer, MarketQuote> parYields,
            List<CurveNode> depoNodes, int cutoffDays,
            ParYieldConvention convention) {
        List<CurveNode> out = new ArrayList<>();
        int maxMonths = parYields.lastKey();
        double annuity = 0.0;

        for (int months = PAR_STEP_MONTHS; months <= maxMonths; months += PAR_STEP_MONTHS) {
            int days = (int) ChronoUnit.DAYS.between(valuationDate, valuationDate.plusMonths(months));

            if (days <= cutoffDays) {                       // covered by deposits
                annuity += interpolateDf(depoNodes, days);
                continue;
            }
            double y = interpolateRate(parYields, months);
            double c = (convention == ParYieldConvention.ANNUAL_EFFECTIVE)
                    ? Math.pow(1.0 + y, 0.5) - 1.0
                    : y / 2.0;
            double df = (1.0 - c * annuity) / (1.0 + c);
            if (!(df > 0.0)) {
                throw new IllegalStateException("Par-yield bootstrap failed at " + months + "M: DF=" + df);
            }
            annuity += df;
            out.add(new CurveNode(days, df, parYields.get(months)));   // null on interpolated grid points
        }
        return out;
    }

    // ------------------------------------------------------------------ zero rates
    private static List<CurveNode> zeroRateNodes(LocalDate valuationDate, List<MarketQuote> zeroRates, int cutoffDays) {
        List<CurveNode> out = new ArrayList<>();
        for (MarketQuote zr : zeroRates) {
            if (zr.daycount() != DaycountBasis.ACT_360 && zr.daycount() != DaycountBasis.ACT_365) {
                throw new IllegalArgumentException("Unsupported zero-rate day count: " + zr.symbol());
            }
            if (zr.compounding() != Compounding.COMPOUNDED && zr.compounding() != Compounding.CONTINUOUS) {
                throw new IllegalArgumentException(
                        "Zero rates must use COMPOUNDED or CONTINUOUS compounding: " + zr.symbol());
            }
            LocalDate mat = YieldCurve.addOffset(valuationDate, zr.tenorOffset());
            int days = (int) ChronoUnit.DAYS.between(valuationDate, mat);
            if (days <= 0) {
                throw new IllegalArgumentException("Zero-rate maturity must be positive: " + zr.symbol());
            }
            if (days <= cutoffDays) {
                continue;   // covered by deposits
            }
            double tau = YieldCurve.yearFraction(valuationDate, mat, zr.daycount());
            out.add(new CurveNode(days, YieldCurve.discountFactorFromRate(zr.rate(), tau, zr.compounding()), zr));
        }
        return out;
    }

    // ------------------------------------------------------------------ helpers
    private static int tenorInMonths(MarketQuote q) {
        Offset o = q.tenorOffset();
        return switch (o.offsetType()) {
            case MONTHS ->
                (int) o.step();
            case YEARS ->
                (int) (12 * o.step());
            default ->
                throw new IllegalArgumentException("Par-yield tenor must be in months or years: " + q.symbol());
        };
    }

    /**
     * Quote at 'key' if present, otherwise linear interpolation (flat outside
     * the quoted range).
     */
    private static double interpolateRate(TreeMap<Integer, MarketQuote> quotes, int key) {
        MarketQuote exact = quotes.get(key);
        if (exact != null) {
            return exact.rate();
        }

        Map.Entry<Integer, MarketQuote> lo = quotes.floorEntry(key);
        Map.Entry<Integer, MarketQuote> hi = quotes.ceilingEntry(key);
        if (lo == null) {
            return hi.getValue().rate();
        }
        if (hi == null) {
            return lo.getValue().rate();
        }

        double w = (double) (key - lo.getKey()) / (hi.getKey() - lo.getKey());
        return lo.getValue().rate() + w * (hi.getValue().rate() - lo.getValue().rate());
    }

    /**
     * Log-linear DF between sorted nodes, with DF(0) = 1. 'days' must not
     * exceed the last node.
     */
    private static double interpolateDf(List<CurveNode> sorted, int days) {
        int prevDays = 0;
        double prevDf = 1.0;
        for (CurveNode n : sorted) {
            if (n.days() == days) {
                return n.df();
            }
            if (n.days() > days) {
                double w = (double) (days - prevDays) / (n.days() - prevDays);
                return prevDf * Math.pow(n.df() / prevDf, w);
            }
            prevDays = n.days();
            prevDf = n.df();
        }
        throw new IllegalStateException("Day " + days + " is beyond the last deposit node");
    }
}
