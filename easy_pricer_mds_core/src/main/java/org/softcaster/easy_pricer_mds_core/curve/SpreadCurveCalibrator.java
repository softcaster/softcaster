/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;
import org.softcaster.core.data.CalibrationProfile;
import org.softcaster.core.data.CalibrationProfileItem;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.SpreadCurveEntity;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveSpread;
import org.softcaster.core.data.YieldCurveSpreadDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.SpreadProfile;
import org.softcaster.engine.curve.SpreadedCurve;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Daily calibration job for a spread curve (e.g. ITA_SPREADED = ECB + spreads).
 *
 * 1. z-spread of each bond against the BASE curve (no spreads): used to discard
 * bad bonds and to count the bonds that support each bucket 2. bucket grid =
 * whole-year nodes of the base curve, up to the last bucket that has at least
 * one bond 3. least-squares fit of ALL the bucket spreads together (SpreadFit):
 * price residuals in spread units plus a curvature penalty. This removes the
 * bias of the "median z-spread per bucket" method (the z-spread of a coupon
 * bond is an average of the spreads of its flows) and gives buckets with few or
 * no bonds a smooth value 4. persistence in yield_curve_spread (same
 * transaction: delete the day's rows, insert the new ones)
 *
 * The stored buckets are spreads of the discount curve (zero-curve spreads): at
 * long maturities they are higher than the z-spreads of the individual bonds.
 * This is expected, do not compare the two directly.
 *
 * NOT compiled against your project: every call marked ADAPT depends on classes
 * I have not seen. Put it in the same module as YieldCurveBuilder (it needs
 * core entities and engine classes). Here "YieldCurve" is the engine class.
 */
@Component("spreadCurveCalibrator")
public class SpreadCurveCalibrator {

    public static final double MIN_T = 0.5;        // years: shorter bonds give unstable spreads
    public static final double MAX_T = 30.0;       // years: beyond the ECB curve the base is extrapolated
    private static final double Z_MIN = -0.005;     // sanity range for the z-spread vs the base: -50 bp
    private static final double Z_MAX = 0.05;       // sanity range: +500 bp
    private static final double SMOOTHING = 1.0;    // lambda; best out-of-sample value in a 5-fold test on 46 BTPs
    // (0.1..10 behave similarly; 1000 is already too stiff)
    private static final int FIT_MAX_ITERATIONS = 10;
    private static final double FIT_TOLERANCE_BP = 0.01;

    @Autowired
    SecurityMasterDataDAO smdDAO;                 
    @Autowired
    CashFlowInstrumentCalculator bondCalculator;  
    @Autowired
    MarketDataService marketDataService;          
    @Autowired
    SpreadCurveEntityDAO spreadCurveDAO;
    @Autowired
    YieldCurveSpreadDAO yieldCurveSpreadDAO;

    /**
     * Outcome of a calibration, for logging and monitoring (price residuals are
     * in-sample, on the calibration bonds).
     */
    public record Result(LocalDate asOfDate, SpreadBuckets.Bucket[] buckets, List<String> skipped,
            double meanResidual, double meanAbsResidual, double maxAbsResidual, String message) {

    }

    private record Priced(String code, SecurityMasterData smd, double cleanMarket) {

    }    

    /**
     * @param spreadCurveCode code in spread_curve (e.g. "ITA_SPREADED"): base
     * curve, minBonds and minValidBuckets come from it
     * @param bondCodes ISINs of the eligible plain fixed-coupon bonds. Leave
     * out inflation-linked, BTP Italia/Valore/Futura/Piu, CCT and any bond of
     * another currency (e.g. US Treasuries)
     * @param excludedCodes ISINs to skip (known outliers, bonds whose cash
     * flows are not generated correctly)
     * @return
     */
    @Transactional
    public Result calibrate(String spreadCurveCode, Collection<String> bondCodes, Set<String> excludedCodes) {

        SpreadCurveEntity sc = spreadCurveDAO.findByCodeWithBase(spreadCurveCode);
        if (sc == null) {
            throw new IllegalArgumentException("Unknown spread curve: " + spreadCurveCode);
        }
        final String baseCode = sc.getBaseCurve().getCode();
        final LocalDate officialDate = marketDataService.getOfficialDate();

        YieldCurve base = marketDataService.getYieldCurve(baseCode);          // spread-free, concrete type
        if (!base.getValuationDate().equals(officialDate)) {
            throw new IllegalStateException("Base curve " + baseCode + " is dated " + base.getValuationDate()
                    + ", expected " + officialDate);
        }
        int[] allYears = base.getBucketYears();
        if (allYears.length == 0) {
            throw new IllegalStateException("Base curve " + baseCode + " has no whole-year nodes");
        }

        // 1. z-spread of each bond against the base curve
        List<Priced> priced = new ArrayList<>();
        List<SpreadBuckets.SpreadPoint> points = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        for (String code : bondCodes) {
            if (excludedCodes.contains(code)) {
                continue;
            }
            try {
                SecurityMasterData smd = smdDAO.findByCodeWithCashFlowAndHolidays(code);
                if (smd == null) {
                    skipped.add(code + ": not in master data");
                    continue;
                }

                double t = ChronoUnit.DAYS.between(officialDate, (Temporal) smd.getMaturityDate()) / 365.0;   // ADAPT: maturity getter
                if (t < MIN_T || t > MAX_T) {
                    skipped.add(code + ": maturity out of range (t=" + t + ")");
                    continue;
                }

                Double clean = marketDataService.getSpotPrice(code, RequestType.BID);               // ADAPT: price source and side
                if (clean == null || !(clean > 0)) {
                    skipped.add(code + ": no price");
                    continue;
                }

                org.softcaster.easy_pricer_mds_core.Calendar calendar = new org.softcaster.easy_pricer_mds_core.Calendar(smd.getCurrency());
                LocalDate settlement = calendar.getNextBusinessDate(officialDate, smd.getBusinessDays());
                double dirty = clean + bondCalculator.getAccruals(smd, settlement);                // accrual at SETTLEMENT date
                double z = bondCalculator.getZSpread(smd, dirty, settlement, base);

                if (z < Z_MIN || z > Z_MAX) {
                    skipped.add(code + ": z-spread out of range (" + z + ")");
                    continue;
                }

                priced.add(new Priced(code, smd, clean));
                points.add(new SpreadBuckets.SpreadPoint(code, t, z));
            } catch (RuntimeException e) {
                skipped.add(code + ": " + e.getMessage());
            }
        }

        // 2. bucket grid: every whole-year node of the base up to the last bucket with at least one bond
        SpreadBuckets.Bucket[] template = Arrays.stream(allYears)
                .mapToObj(SpreadBuckets.Bucket::empty).toArray(SpreadBuckets.Bucket[]::new);
        SpreadBuckets.Bucket[] support = SpreadBuckets.compute(points, template, 1, MIN_T, MAX_T);   // only for bond counts
        int last = -1;
        for (int i = 0; i < support.length; i++) {
            if (support[i].nBonds() >= 1) {
                last = i;
            }
        }
        if (last < 2) {
            throw new IllegalStateException("Calibration rejected: fewer than 3 buckets have bonds. Skipped: " + skipped);
        }
        final int m = last + 1;
        final int[] years = Arrays.copyOf(allYears, m);
        final double[] tenors = Arrays.stream(years).asDoubleStream().toArray();

        long supported = Arrays.stream(support, 0, m).filter(b -> b.nBonds() >= sc.getMinBonds()).count();
        if (supported < sc.getMinValidBuckets()) {
            throw new IllegalStateException("Calibration rejected: only " + supported + " buckets with at least "
                    + sc.getMinBonds() + " bonds (min " + sc.getMinValidBuckets() + "). Skipped: " + skipped);
        }

        // 3. least-squares fit of all the buckets together
        final double[] market = priced.stream().mapToDouble(Priced::cleanMarket).toArray();
        SpreadFit.Pricer pricer = spreadsBp -> modelPrices(priced, base, officialDate, years, spreadsBp);
        double[] fitted = SpreadFit.fit(tenors, market, pricer, SMOOTHING, FIT_MAX_ITERATIONS, FIT_TOLERANCE_BP);

        double[] model = pricer.prices(fitted);
        double sum = 0.0, sumAbs = 0.0, maxAbs = 0.0;
        int n = 0;
        for (int i = 0; i < model.length; i++) {
            if (!Double.isFinite(model[i])) {
                continue;
            }
            double r = model[i] - market[i];
            sum += r;
            sumAbs += Math.abs(r);
            maxAbs = Math.max(maxAbs, Math.abs(r));
            n++;
        }
        double meanResidual = n == 0 ? 0.0 : sum / n;
        double meanAbsResidual = n == 0 ? 0.0 : sumAbs / n;

        SpreadBuckets.Bucket[] buckets = new SpreadBuckets.Bucket[m];
        for (int j = 0; j < m; j++) {
            buckets[j] = new SpreadBuckets.Bucket(years[j], fitted[j] / 1e4, support[j].nBonds());   // bp -> decimal
        }

        // 4. persist: replace today's calibration atomically (same transaction)
        yieldCurveSpreadDAO.deleteByCurveAndDate(sc.getIdSpreadCurve(), officialDate);
        List<YieldCurveSpread> rows = new ArrayList<>();
        for (SpreadBuckets.Bucket b : buckets) {
            YieldCurveSpread r = new YieldCurveSpread();
            r.setSpreadCurveId(sc.getIdSpreadCurve());
            r.setAsOfDate(officialDate);
            r.setBaseCurveCode(baseCode);
            r.setOffsetType((short) OffsetType.YEARS.getId());                                      // ADAPT: IdentifiableEnum API
            r.setOffsetValue((short) b.years());
            r.setzSpread(b.zSpread());
            r.setnBonds(b.nBonds());                                                                // 0 = bucket between bonds, smoothed
            rows.add(r);
        }
        yieldCurveSpreadDAO.saveAll(rows);

        String message = "Spread calibration " + spreadCurveCode + " on " + officialDate + ": "
                + points.size() + " bonds, " + rows.size() + " buckets (" + supported + " supported by >= "
                + sc.getMinBonds() + " bonds), mean residual " + meanResidual + ", mean |residual| " + meanAbsResidual
                + ", max |residual| " + maxAbs + ", skipped " + skipped.size();
        return new Result(officialDate, buckets, skipped, meanResidual, meanAbsResidual, maxAbs, message);
    }

    public Result calibrate(String spreadCurveCode) {

        SpreadCurveEntity sc = spreadCurveDAO.findByCodeWithBase(spreadCurveCode);
        if (sc == null) {
            throw new IllegalArgumentException("Unknown spread curve: " + spreadCurveCode);
        }
        final String baseCode = sc.getBaseCurve().getCode();
        final LocalDate officialDate = marketDataService.getOfficialDate();

        YieldCurve base = marketDataService.getYieldCurve(baseCode);          // spread-free, concrete type
        if (!base.getValuationDate().equals(officialDate)) {
            throw new IllegalStateException("Base curve " + baseCode + " is dated " + base.getValuationDate()
                    + ", expected " + officialDate);
        }
        int[] allYears = base.getBucketYears();
        if (allYears.length == 0) {
            throw new IllegalStateException("Base curve " + baseCode + " has no whole-year nodes");
        }

        // 1. z-spread of each bond against the base curve
        List<Priced> priced = new ArrayList<>();
        List<SpreadBuckets.SpreadPoint> points = new ArrayList<>();
        List<String> skipped = new ArrayList<>();

        CalibrationProfile profile = sc.getCalibrationProfile();
        for (CalibrationProfileItem item : profile.getItems()) {
            try {
                SecurityMasterData smd = smdDAO.findByIdWithCashFlowAndHolidays(item.getMasterData());
                if (smd == null) {
                    skipped.add(item.getCode() + ": not in master data");
                    continue;
                }

                Double clean = item.getBid();
                if (clean == null || !(clean > 0)) {
                    skipped.add(item.getCode() + ": no price");
                    continue;
                }

                org.softcaster.easy_pricer_mds_core.Calendar calendar = new org.softcaster.easy_pricer_mds_core.Calendar(smd.getCurrency());
                LocalDate settlement = calendar.getNextBusinessDate(officialDate, smd.getBusinessDays());
                double dirty = clean + bondCalculator.getAccruals(smd, settlement);                // accrual at SETTLEMENT date
                double z = bondCalculator.getZSpread(smd, dirty, settlement, base);

                if (z < Z_MIN || z > Z_MAX) {
                    skipped.add(item.getCode() + ": z-spread out of range (" + z + ")");
                    continue;
                }

                double t = ChronoUnit.DAYS.between(officialDate, smd.getMaturityDate().toLocalDate()) / 365.0;   // ADAPT: maturity getter

                priced.add(new Priced(item.getCode(), smd, clean));
                points.add(new SpreadBuckets.SpreadPoint(item.getCode(), t, z));
            } catch (RuntimeException e) {
                skipped.add(item.getCode() + ": " + e.getMessage());
            }
        }

        // 2. bucket grid: every whole-year node of the base up to the last bucket with at least one bond
        SpreadBuckets.Bucket[] template = Arrays.stream(allYears)
                .mapToObj(SpreadBuckets.Bucket::empty).toArray(SpreadBuckets.Bucket[]::new);
        SpreadBuckets.Bucket[] support = SpreadBuckets.compute(points, template, 1, MIN_T, MAX_T);   // only for bond counts
        int last = -1;
        for (int i = 0; i < support.length; i++) {
            if (support[i].nBonds() >= 1) {
                last = i;
            }
        }
        if (last < 2) {
            throw new IllegalStateException("Calibration rejected: fewer than 3 buckets have bonds. Skipped: " + skipped);
        }
        final int m = last + 1;
        final int[] years = Arrays.copyOf(allYears, m);
        final double[] tenors = Arrays.stream(years).asDoubleStream().toArray();

        long supported = Arrays.stream(support, 0, m).filter(b -> b.nBonds() >= sc.getMinBonds()).count();
        if (supported < sc.getMinValidBuckets()) {
            throw new IllegalStateException("Calibration rejected: only " + supported + " buckets with at least "
                    + sc.getMinBonds() + " bonds (min " + sc.getMinValidBuckets() + "). Skipped: " + skipped);
        }

        // 3. least-squares fit of all the buckets together
        final double[] market = priced.stream().mapToDouble(Priced::cleanMarket).toArray();
        SpreadFit.Pricer pricer = spreadsBp -> modelPrices(priced, base, officialDate, years, spreadsBp);
        double[] fitted = SpreadFit.fit(tenors, market, pricer, SMOOTHING, FIT_MAX_ITERATIONS, FIT_TOLERANCE_BP);

        double[] model = pricer.prices(fitted);
        double sum = 0.0, sumAbs = 0.0, maxAbs = 0.0;
        int n = 0;
        for (int i = 0; i < model.length; i++) {
            if (!Double.isFinite(model[i])) {
                continue;
            }
            double r = model[i] - market[i];
            sum += r;
            sumAbs += Math.abs(r);
            maxAbs = Math.max(maxAbs, Math.abs(r));
            n++;
        }
        double meanResidual = n == 0 ? 0.0 : sum / n;
        double meanAbsResidual = n == 0 ? 0.0 : sumAbs / n;

        SpreadBuckets.Bucket[] buckets = new SpreadBuckets.Bucket[m];
        for (int j = 0; j < m; j++) {
            buckets[j] = new SpreadBuckets.Bucket(years[j], fitted[j] / 1e4, support[j].nBonds());   // bp -> decimal
        }

        // 4. persist: replace today's calibration atomically (same transaction)
        yieldCurveSpreadDAO.deleteByCurveAndDate(sc.getIdSpreadCurve(), officialDate);
        List<YieldCurveSpread> rows = new ArrayList<>();
        for (SpreadBuckets.Bucket b : buckets) {
            YieldCurveSpread r = new YieldCurveSpread();
            r.setSpreadCurveId(sc.getIdSpreadCurve());
            r.setAsOfDate(officialDate);
            r.setBaseCurveCode(baseCode);
            r.setOffsetType((short) OffsetType.YEARS.getId());                                      // ADAPT: IdentifiableEnum API
            r.setOffsetValue((short) b.years());
            r.setzSpread(b.zSpread());
            r.setnBonds(b.nBonds());                                                                // 0 = bucket between bonds, smoothed
            rows.add(r);
        }
        yieldCurveSpreadDAO.saveAll(rows);

        String message
                = "Spread calibration " + spreadCurveCode + " on " + officialDate + ": "
                + points.size() + " bonds, " + rows.size() + " buckets (" + supported + " supported by >= "
                + sc.getMinBonds() + " bonds), mean residual " + meanResidual + ", mean |residual| " + meanAbsResidual
                + ", max |residual| " + maxAbs + ", skipped " + skipped.size();
        return new Result(officialDate, buckets, skipped, meanResidual, meanAbsResidual, maxAbs, message);
    }

    // ------------------------------------------------------------------ helpers
    /**
     * Model CLEAN prices of all the calibration bonds with the given bucket
     * spreads (bp). NaN if a bond cannot be priced.
     */
    private double[] modelPrices(List<Priced> priced, YieldCurve base, LocalDate officialDate,
            int[] years, double[] spreadsBp) {
        Map<Offset, Double> map = new LinkedHashMap<>();
        for (int j = 0; j < years.length; j++) {
            map.put(new Offset(years[j], OffsetType.YEARS), spreadsBp[j] / 1e4);
        }
        DiscountCurve curve = new SpreadedCurve(base, SpreadProfile.of(officialDate, map));   // built once per spread vector

        double[] out = new double[priced.size()];
        for (int i = 0; i < out.length; i++) {
            try {
                out[i] = bondCalculator.calculatePrice(priced.get(i).smd(), officialDate, curve);   // ADAPT: returns CLEAN price
            } catch (RuntimeException e) {
                out[i] = Double.NaN;
            }
        }
        return out;
    }
}
