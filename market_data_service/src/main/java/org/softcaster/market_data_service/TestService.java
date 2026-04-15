/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package org.softcaster.market_data_service;

import java.io.File;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.MarketDataProviderHelper;
import org.softcaster.marketdataprovider.REQUEST_TYPE;

/**
 *
 * @author softc
 */
public class TestService {

    public static void main(String[] args) {

        // Inizializzazione Logger
        MarketDataProviderHelper.initializeLogger();

        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        MarketDataProviderHelper.initializePython();

        try {
            MarketDataService mds = MarketDataService.getInstance();
            mds.updateMarketData();
            double rate = mds.getSpotPrice("EURUSD",REQUEST_TYPE.MIDDLE);
            System.out.println(rate);
            
            rate = mds.getSpotPrice("6EM6",REQUEST_TYPE.MIDDLE);
            System.out.println(rate);

            org.softcaster.commons.types.Date settlement = new org.softcaster.commons.types.Date();
            settlement.addDays(20);
            rate = mds.getYieldCurveRate("EURIBOR", settlement);
            System.out.println(rate);

            settlement.addDays(20);
            rate = mds.getYieldCurveRate("EURIBOR", settlement);
            System.out.println(rate);
            
            settlement = new org.softcaster.commons.types.Date();
            settlement.addYears(1);
            settlement.addMonths(1);
            rate = mds.getYieldCurveRate("EURIRS", settlement);
            System.out.println(rate);
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }
}
