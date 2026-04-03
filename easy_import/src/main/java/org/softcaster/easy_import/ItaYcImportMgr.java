/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_core.data.YieldCurveDAO;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import org.softcaster.marketdataprovider.investingcom.InvestingComProvider;

/**
 *
 * @author ep
 */
public class ItaYcImportMgr implements IImportMgr {

    private static ItaYcImportMgr _instance = null;

    // Statico per essere usato dal Singleton
    private static YieldCurveDAO yieldCurveDAO;

    @Override
    public void start(IProgressInfo progressInfo) {
        InvestingComProvider provider = InvestingComProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.investing.com";
        param.url = "https://www.investing.com/rates-bonds/italy-government-bonds";
        param.extraParams.add("ITAYC");
        param.today = new org.softcaster.commons.types.Date();
        param.market = MARKETS.YIELDS;

        try {
            provider.refresh(param);
            List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
            YieldCurveImportMgr.saveNodes(rates,"ITAYC",yieldCurveDAO);
        } catch (MarketDataProviderException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
    }

    public static ItaYcImportMgr getInstance(YieldCurveDAO dao) {
        if (_instance == null) {
            _instance = new ItaYcImportMgr();
            yieldCurveDAO = dao;
        }
        return _instance;
    }

    /**
     *
     * @return
     */
    @Override
    public String getImportInfo() {
        return "ITAYC";
    }
}
