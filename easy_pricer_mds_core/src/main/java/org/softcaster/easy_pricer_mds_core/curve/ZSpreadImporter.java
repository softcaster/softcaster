/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.curve;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.softcaster.core.data.YieldCurveSpread;
import org.softcaster.core.data.YieldCurveSpreadDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.provider.euronext.BorsaItalianaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("zSpreadImporter")
public class ZSpreadImporter {

    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;
    @Autowired
    @Qualifier("yieldCurveBuilder")
    private YieldCurveBuilder yieldCurveBuilder;
    @Autowired
    @Qualifier("spreadCurveCalibrator")
    private SpreadCurveCalibrator calibrator;
    @Autowired
    YieldCurveSpreadDAO yieldCurveSpreadDAO;

    public void importZSpread(String idCurve) {
        org.softcaster.engine.curve.DiscountCurve discountCurve = yieldCurveBuilder.buildDiscountCurve(idCurve, marketDataService.getOfficialDate());
        BorsaItalianaProvider provider = BorsaItalianaProvider.getInstance();
        LocalDate officialDate = marketDataService.getOfficialDate();

        int[] bucketYears = discountCurve.getBucketYears();
        int minBonds = 1;
        SpreadBuckets.Bucket[] buckets = calibrator.calibrate(provider, officialDate, discountCurve, bucketYears, minBonds);
        if (buckets != null && buckets.length > 0) {
           Map<Offset, Double> bucketMap = new LinkedHashMap<>();
            for (SpreadBuckets.Bucket b : buckets) {
                if (b.zSpread() != null) {
                    bucketMap.put(new Offset(b.years(), OffsetType.YEARS), b.zSpread());
                }
            }
                    List<YieldCurveSpread> rows = new ArrayList<>();
        for (SpreadBuckets.Bucket b : buckets) {
            if (b.zSpread() == null) {
                continue; // weak bucket: not stored
            }
            YieldCurveSpread r = new YieldCurveSpread();
            r.setSpreadCurveId(1);
            r.setAsOfDate(officialDate);
            r.setBaseCurveCode("");
            r.setOffsetType((short) OffsetType.YEARS.getId());                                     
            r.setOffsetValue((short) b.years());
            r.setzSpread(b.zSpread());
            r.setnBonds(b.nBonds());
            rows.add(r);
        }
        //yieldCurveSpreadDAO.saveAll(rows);

        }
    }
}
