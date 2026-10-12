/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.batches;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CalibrationProfile;
import org.softcaster.core.data.CalibrationProfileDAO;
import org.softcaster.core.data.CalibrationProfileItem;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.curve.SpreadCurveCalibrator;
import static org.softcaster.easy_pricer_mds_core.curve.SpreadCurveCalibrator.MAX_T;
import static org.softcaster.easy_pricer_mds_core.curve.SpreadCurveCalibrator.MIN_T;
import org.softcaster.easy_pricer_mds_core.curve.SpreadCurveCalibrator.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("zSpreadHelper")
public class ZSpreadHelper {

    @Autowired
    InstrumentQuoteDAO instrumentQuoteDAO;
    @Autowired
    CalibrationProfileDAO calibrationProfileDAO;
    @Autowired
    MarketDataService marketDataService;          
    @Autowired
    SpreadCurveCalibrator calibrator;

    private static final Logger log = LoggerFactory.getLogger(ZSpreadHelper.class);

    public void calibrateZSpread(String spreadCurve) {
        log.info("===  Starting z-spread calibration ... ===\n");
        Result result = calibrator.calibrate(spreadCurve);  
        log.info("=== {} ===\n", result.message());
}
    
    public void updateCalibrationItems(String profileCode) {
        log.info("===  Starting update calibration items ... ===\n");

        LocalDate officialDate = marketDataService.getOfficialDate();
        
        CalibrationProfile profile = calibrationProfileDAO.findByCalibrationProfileCode(profileCode);
        if (profile == null) {
            log.error("CalibrationProfile is null!");
            throw new BatchException("CalibrationProfile is null!");
        }

        CalibrationProfileItem item;
        List<InstrumentQuote> quotes = instrumentQuoteDAO.findByAssetClass("XRB");
        int count = 0;
        for (InstrumentQuote quote : quotes) {
            // check coerenza maturity
            LocalDate maturity = quote.getMasterData().getMaturityDate().toLocalDate();
            if (validate(maturity, officialDate)) {
                // check coerenza divisa
                if (profile.getCurrency().getIsoCode().trim().equalsIgnoreCase(quote.getMasterData().getCurrency().getIsoCode().trim())) {
                    item = new CalibrationProfileItem();
                    if (item != null) {
                        item.setCalibrationProfile(profile.getCalibrationProfileId());
                        item.setInstrumentQuote(quote.getIdInstrumentQuote());
                        item.setMasterData(quote.getMasterData().getIdMasterData());
                        item.setBid(quote.getBid());
                        item.setAsk(quote.getAsk());
                        item.setCode(quote.getMasterData().getCode());
                        profile.addItem(item);
                        count++;
                    }
                }
            }
        }
        calibrationProfileDAO.saveOrUpdate(profile);
        log.info("===  Updated {} items ===\n", count);
    }

    private boolean validate(LocalDate maturity, LocalDate officialDate) {
        double t = ChronoUnit.DAYS.between(officialDate, (Temporal) maturity) / 365.0;
        return !(t < MIN_T || t > MAX_T);
    }
}
