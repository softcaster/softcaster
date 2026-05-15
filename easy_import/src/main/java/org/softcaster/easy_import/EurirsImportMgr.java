/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import org.softcaster.marketdataprovider.sole24h.Sole24hProvider;

/**
 *
 * @author softc
 */
public class EurirsImportMgr implements IImportMgr {

    private static EurirsImportMgr _instance = null;

    // Statico per essere usato dal Singleton
    private static YieldCurveDAO yieldCurveDAO;

    @Override
    public void start(IProgressInfo progressInfo) {

        Sole24hProvider provider = Sole24hProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.ilsole24ore.com/";
        param.url = "https://mercatiwdg.ilsole24ore.com/FinanzaMercati/WidgetSelector/listino?widgetConfiguration=FMIRS";
        param.extraParams.add("EURIRS");
        param.market = MARKETS.YIELDS;

        try {
            provider.refresh(param);
            List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
            YieldCurveImportMgr.saveNodes(rates,"EURIRS",yieldCurveDAO);
        } catch (MarketDataProviderException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
    }

    public static EurirsImportMgr getInstance(YieldCurveDAO dao) {
        if (_instance == null) {
            _instance = new EurirsImportMgr();
            yieldCurveDAO = dao;
        }
        return _instance;
    }

    @Override
    public String getImportInfo() {
        return "EURIRS";
    }
}
