/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.curve;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.core.data.YieldCurveSpread;
import org.softcaster.core.data.YieldCurveSpreadDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.curve.SpreadProfile;
import org.softcaster.engine.curve.SpreadedCurve;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.enums.Market;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Daily calibration job: z-spread of each bond against the BASE curve (no
 * spreads), median per bucket, and persistence into yield_curve_spread.
 *
 * NOT compiled against your project: every call marked "ADAPT" depends on
 * classes I have not seen (DAO types, getter names, IdentifiableEnum API, price
 * source).
 *
 * Put it in the same module as YieldCurveBuilder (it needs core entities and
 * engine classes).
 */
@Component("spreadCurveCalibrator")
public class SpreadCurveCalibrator {

    private static final double MIN_T = 0.5;       // years: shorter bonds give unstable spreads
    private static final double MAX_T = 30.0;      // years: beyond the ECB curve the base is extrapolated
    private static final double Z_MIN = -0.005;    // sanity range: -50 bp
    private static final double Z_MAX = 0.05;      // sanity range: +500 bp

    @Autowired
    SecurityMasterDataDAO smdDAO;                 // ADAPT: type of your smdDAO
    @Autowired
    BondCalculator bondCalculator;                // ADAPT
    @Autowired
    MarketDataService marketDataService;          // ADAPT
    @Autowired
    YieldCurveEntityDAO yieldCurveDAO;
    @Autowired
    YieldCurveSpreadDAO yieldCurveSpreadDAO;

    /**
     * Outcome of a calibration, for logging and monitoring.
     */
    public record Result(LocalDate asOfDate, SpreadBuckets.Bucket[] buckets, List<String> skipped,
            double meanResidual, double maxAbsResidual) {

    }

    private record Priced(SecurityMasterData smd, double cleanPrice) {

    }   // ADAPT: SecurityMasterData type

    /**
     * @param spreadCurveCode curve the spreads belong to (e.g. "ITA")
     * @param baseCurveCode base curve, WITHOUT spreads (e.g. "ECBYC"); stored
     * with the spreads
     * @param bondCodes ISINs of the eligible plain fixed-coupon bonds (no
     * inflation-linked, BTP Italia/Valore/Futura/Piu, CCT)
     * @param excludedCodes ISINs to leave out (e.g. known outliers)
     * @param bucketYears bucket tenors in years, ascending
     * @param minBonds minimum bonds per bucket
     * @param minValidBuckets the calibration is rejected if fewer buckets are
     * valid (protects the stored data)
     * @return
     */
    @Transactional
    public Result calibrate(String spreadCurveCode, String baseCurveCode, Collection<String> bondCodes,
            Set<String> excludedCodes, int[] bucketYears, int minBonds, int minValidBuckets) {

        LocalDate officialDate = marketDataService.getOfficialDate();
        DiscountCurve base = marketDataService.getYieldCurve(baseCurveCode);
        if (base == null) {
            throw new IllegalStateException("Base curve not loaded: " + baseCurveCode);
        }
        org.softcaster.core.data.YieldCurveEntity dbCurve = yieldCurveDAO.findByCode(spreadCurveCode);
        if (dbCurve == null) {
            throw new IllegalArgumentException("Unknown curve: " + spreadCurveCode);
        }

        // 1. z-spread of each bond against the base curve
        List<SpreadBuckets.SpreadPoint> points = new ArrayList<>();
        Map<String, Priced> priced = new LinkedHashMap<>();
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

                double t = ChronoUnit.DAYS.between(officialDate, smd.getMaturityDate().toLocalDate()) / 365.0;   // ADAPT: maturity getter
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
                double z = bondCalculator.getZSpread(smd, dirty, settlement, (YieldCurve) base);

                if (z < Z_MIN || z > Z_MAX) {
                    skipped.add(code + ": z-spread out of range (" + z + ")");
                    continue;
                }

                points.add(new SpreadBuckets.SpreadPoint(code, t, z));
                priced.put(code, new Priced(smd, clean));
            } catch (RuntimeException e) {
                skipped.add(code + ": " + e.getMessage());
            }
        }

        // 2. buckets
        SpreadBuckets.Bucket[] template = Arrays.stream(bucketYears)
                .mapToObj(SpreadBuckets.Bucket::empty).toArray(SpreadBuckets.Bucket[]::new);
        SpreadBuckets.Bucket[] buckets = SpreadBuckets.compute(points, template, minBonds, MIN_T, MAX_T);

        long valid = Arrays.stream(buckets).filter(b -> b.zSpread() != null).count();
        if (valid < minValidBuckets) {
            throw new IllegalStateException("Calibration rejected: only " + valid + " valid buckets (min "
                    + minValidBuckets + "), " + points.size() + " bonds priced. Skipped: " + skipped);
        }

        // 3. repricing check with the curve we are about to store (diagnostic, does not block the save)
        Map<Offset, Double> bucketMap = new LinkedHashMap<>();
        for (SpreadBuckets.Bucket b : buckets) {
            if (b.zSpread() != null) {
                bucketMap.put(new Offset(b.years(), OffsetType.YEARS), b.zSpread());
            }
        }
        DiscountCurve ita = new SpreadedCurve(base, SpreadProfile.of(officialDate, bucketMap));
        double sum = 0, maxAbs = 0;
        for (Priced p : priced.values()) {
            double model = bondCalculator.calculatePrice(p.smd(), officialDate, (YieldCurve) ita);              // ADAPT: returns CLEAN price
            double residual = model - p.cleanPrice();
            sum += residual;
            maxAbs = Math.max(maxAbs, Math.abs(residual));
        }
        double meanResidual = priced.isEmpty() ? 0.0 : sum / priced.size();

        // 4. persist: replace today's calibration atomically (same transaction)
        yieldCurveSpreadDAO.deleteByCurveAndDate(dbCurve.getIdYieldCurve(), officialDate);
        List<YieldCurveSpread> rows = new ArrayList<>();
        for (SpreadBuckets.Bucket b : buckets) {
            if (b.zSpread() == null) {
                continue;                                                      // weak bucket: not stored
            }
            YieldCurveSpread r = new YieldCurveSpread();
            r.setSpreadCurveId(dbCurve.getIdYieldCurve());
            r.setAsOfDate(officialDate);
            r.setBaseCurveCode(baseCurveCode);
            r.setOffsetType((short) OffsetType.YEARS.getId());                                      // ADAPT: IdentifiableEnum API
            r.setOffsetValue((short) b.years());
            r.setzSpread(b.zSpread());
            r.setnBonds(b.nBonds());
            rows.add(r);
        }
        yieldCurveSpreadDAO.saveAll(rows);

        LoggerMgr.logInfo("Spread calibration " + spreadCurveCode + " on " + officialDate + ": "
                + points.size() + " bonds, " + rows.size() + " buckets, mean residual " + meanResidual
                + ", max |residual| " + maxAbs + ", skipped " + skipped.size());
        return new Result(officialDate, buckets, skipped, meanResidual, maxAbs);
    }

    private SpreadBuckets.SpreadPoint calcZSpread(LocalDate officialDate, DiscountCurve curve, double cleanPrice, MasterData data) {
        if (data instanceof SecurityMasterData) {
            SecurityMasterData smd = smdDAO.findByCodeWithCashFlowAndHolidays(data.getCode());
            if (smd.getCashFlows().isEmpty()) {
                System.out.println("Instrument: " + smd.getCode() + " has not cashflow!");
                return null;
            }
            if (!smd.getCurrency().getIsoCode().equals(curve.getCurrency().getCurrencyCode())) {
                return null;
            }
            double t = ChronoUnit.DAYS.between(officialDate, data.getMaturityDate().toLocalDate()) / 365.0;
            if (t < MIN_T || t > MAX_T) {
                return null;
            }
            if (curve != null) {
                org.softcaster.easy_pricer_mds_core.Calendar calendar = new org.softcaster.easy_pricer_mds_core.Calendar(smd.getCurrency());
                LocalDate valuationDate = calendar.getNextBusinessDate(officialDate, smd.getBusinessDays());
                double accrual = bondCalculator.getAccruals(smd, valuationDate);
                double dirtyPrice = cleanPrice + accrual;
                double z = bondCalculator.getZSpread(smd, dirtyPrice, valuationDate, curve);
                return new SpreadBuckets.SpreadPoint(data.getCode(), t, z);
            }
        }
        return null;
    }

    public SpreadBuckets.Bucket[] calibrate(AbstractProvider provider, LocalDate officialDate, DiscountCurve curve, int[] bucketYears, int minBonds) {
        List<SecurityMasterData> dataList = smdDAO.findAllByAssetClass("XRB");

        List<SpreadBuckets.SpreadPoint> points = new ArrayList<>();
        for (MasterData data : dataList) {
            Node node = provider.getMktQuote(data.getCode(), Market.BONDS);
            double cleanPrice = node != null ? node.getData().bid() : 0.;
            if (NumberUtils.isZero(cleanPrice)) {
                continue;
            }

            SpreadBuckets.SpreadPoint point = calcZSpread(officialDate, curve, cleanPrice, data);
            if (point != null) {
                points.add(point);
            }
        }

        // buckets
        SpreadBuckets.Bucket[] template = Arrays.stream(bucketYears)
                .mapToObj(SpreadBuckets.Bucket::empty).toArray(SpreadBuckets.Bucket[]::new);
        SpreadBuckets.Bucket[] buckets = SpreadBuckets.compute(points, template, minBonds, MIN_T, MAX_T);

        return buckets;
    }

    public SpreadBuckets.Bucket[] calibrate(List<InstrumentMktData> instruments, LocalDate officialDate, DiscountCurve curve, int[] bucketYears, int minBonds) {

        List<SpreadBuckets.SpreadPoint> points = new ArrayList<>();
        SecurityMasterData data;
        for (InstrumentMktData instrument : instruments) {
            data = smdDAO.findByCodeWithCashFlowAndHolidays(instrument.code());
            SpreadBuckets.SpreadPoint point = calcZSpread(officialDate, curve, instrument.mktPrice(), data);
            if (point != null) {
                points.add(point);
            }
        }

        // buckets
        SpreadBuckets.Bucket[] template = Arrays.stream(bucketYears)
                .mapToObj(SpreadBuckets.Bucket::empty).toArray(SpreadBuckets.Bucket[]::new);
        SpreadBuckets.Bucket[] buckets = SpreadBuckets.compute(points, template, minBonds, MIN_T, MAX_T);

        return buckets;
    }
}
