/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller.helper;

import jakarta.transaction.Transactional;
import java.util.List;
import org.softcaster.core.data.YieldCurve;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ValuationInitializer {

    @Autowired
    private YieldCurveDAO yieldCurveDAO;

    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    @Transactional
    public void init() {

        List<YieldCurve> curves = yieldCurveDAO.findAll();

        for (YieldCurve yieldCurve : curves) {
            marketDataService.loadCurveCurveRates(
                    yieldCurve.getCode()
            );
        }
    }
}
