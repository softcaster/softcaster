/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_core.data.YieldCurveDAO;
import org.softcaster.marketdataprovider.CmeGroup.CmeGroupProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;

/**
 *
 * @author softc
 */
public class SofrImportMgr implements IImportMgr {

    private static SofrImportMgr _instance = null;

    // Statico per essere usato dal Singleton
    private static YieldCurveDAO yieldCurveDAO;
    
    @Override
    public void start(IProgressInfo progressInfo) {
        try {
            CmeGroupProvider provider = CmeGroupProvider.getInstance();
            ConnectionParam param = new ConnectionParam();
            param.baseUrl = "https://www.cmegroup.com";
            param.url = "https://www.cmegroup.com/services/sofr-strip-rates/";
            param.extraParams.add("SOFR");
            param.market = MARKETS.YIELDS;
            provider.refresh(param);
            YieldCurveImportMgr.saveNodes(provider.quotes(MARKETS.YIELDS),"SOFR",yieldCurveDAO);
        } catch (MarketDataProviderException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
    }

    public static SofrImportMgr getInstance(YieldCurveDAO dao) {
        if (_instance == null) {
            _instance = new SofrImportMgr();
            yieldCurveDAO = dao;
        }
        return _instance;
    }
    
    @Override
    public String getImportInfo() {
        return "SOFR";
    }
}
