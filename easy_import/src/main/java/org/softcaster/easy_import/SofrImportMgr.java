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
import org.softcaster.marketdataprovider.CmeGroup.CmeGroupProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import org.softcaster.marketdataprovider.YieldNode;

/**
 *
 * @author softc
 */
public class SofrImportMgr implements IImportMgr {

    private static SofrImportMgr _instance = null;
    private final Yield_curveDAO ycDAO = new Yield_curveDAO();
    private final Yield_curve_itemDAO ycItemDAO = new Yield_curve_itemDAO();

    private void saveNodes(List<DataNode> rates) {
        Yield_curve yc = new Yield_curve();
        yc.setCode("SOFR");
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
        try {
            CmeGroupProvider provider = CmeGroupProvider.getInstance();
            ConnectionParam param = new ConnectionParam();
            param.baseUrl = "https://www.cmegroup.com";
            param.url = "https://www.cmegroup.com/services/sofr-strip-rates/";
            param.extraParams.add("SOFR");
            param.market = MARKETS.YIELDS;
            provider.refresh(param);
            saveNodes(provider.quotes(MARKETS.YIELDS));
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

    public static SofrImportMgr getInstance() {
        if (_instance == null) {
            _instance = new SofrImportMgr();
        }
        return _instance;
    }

}
