/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_import.beans.Yield_curve;
import org.softcaster.easy_import.beans.Yield_curveDAO;
import org.softcaster.easy_import.beans.Yield_curve_item;
import org.softcaster.easy_import.beans.Yield_curve_itemDAO;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import org.softcaster.marketdataprovider.YieldNode;
import org.softcaster.marketdataprovider.euribor.EuriborRatesProvider;

/**
 *
 * @author softc
 */
public class EuriborImportMgr implements IImportMgr {

    private static EuriborImportMgr _instance = null;
    private final Yield_curveDAO ycDAO = new Yield_curveDAO();
    private final Yield_curve_itemDAO ycItemDAO = new Yield_curve_itemDAO();

    private void saveNodes(List<DataNode> rates) {
        Yield_curve yc = new Yield_curve();
        yc.setCode("EURIBOR");
        ycDAO.loadByIdx(yc);
        if (yc != null) {
            Yield_curve_item ycItem = null;
            for (DataNode node : rates) {
                if (node instanceof YieldNode yieldNode) {
                    ycItem = new Yield_curve_item();
                    ycItem.setRic(yieldNode.getRic());
                    ycItem.setAsk(yieldNode.getAsk() / 100.);
                    ycItem.setBid(yieldNode.getBid() / 100.);
                    ycItem.setOffset_type(yieldNode.getOffsetType().ordinal());
                    ycItem.setOffset_value(yieldNode.getOffset());
                    ycItem.setYield_curve(yc.getId_yield_curve());
                    ycItemDAO.insertOrUpdate(ycItem);
                }
            }
        }
    }

    @Override
    public void start(IProgressInfo progressInfo) {

        EuriborRatesProvider provider = EuriborRatesProvider.getInstance();
        ConnectionParam param = new ConnectionParam();
        param.baseUrl = "https://www.euribor-rates.eu/it/";
        param.url = "https://www.euribor-rates.eu/it/tassi-euribor-aggiornati/";
        param.extraParams.add("EURIBOR");
        param.today = new org.softcaster.commons.types.Date();
        param.market = MARKETS.YIELDS;

        try {
            provider.refresh(param);
            List<DataNode> rates = provider.quotes(MARKETS.YIELDS);
            saveNodes(rates);
        } catch (MarketDataProviderException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
        ycDAO.closeStatements();
        ycItemDAO.closeStatements();
    }

    public static EuriborImportMgr getInstance() {
        if (_instance == null) {
            _instance = new EuriborImportMgr();
        }
        return _instance;
    }

    @Override
    public String getImportInfo() {
        return "EURIBOR";
    }
}
