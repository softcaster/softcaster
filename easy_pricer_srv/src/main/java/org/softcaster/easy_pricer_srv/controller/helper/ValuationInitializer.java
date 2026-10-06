/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller.helper;

import jakarta.transaction.Transactional;
import java.util.List;
import org.softcaster.core.data.YieldCurveEntity;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ValuationInitializer {

    @Autowired
    private YieldCurveEntityDAO yieldCurveDAO;

    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    @Transactional
    public void init() {

        List<YieldCurveEntity> curves = yieldCurveDAO.findAll();

        for (YieldCurveEntity yieldCurve : curves) {
            marketDataService.loadCurveCurveRates(
                    yieldCurve.getCode()
            );
        }
    }
}
