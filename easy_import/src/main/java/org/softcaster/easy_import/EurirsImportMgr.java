/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_import.beans.Yield_curve;
import org.softcaster.easy_import.beans.Yield_curveDAO;
import org.softcaster.easy_import.beans.Yield_curve_item;
import org.softcaster.easy_import.beans.Yield_curve_itemDAO;
import org.softcaster.marketdataprovider.sole24h.Sole24hProvider;
import org.softcaster.marketdataprovider.YieldNode;

/**
 *
 * @author softc
 */
public class EurirsImportMgr implements IImportMgr {

    private static EurirsImportMgr _instance = null;
    private List<YieldNode> nodes = new ArrayList<>();
    private final Yield_curveDAO ycDAO = new Yield_curveDAO();
    private final Yield_curve_itemDAO ycItemDAO = new Yield_curve_itemDAO();

    private void saveNodes() {
        Yield_curve yc = new Yield_curve();
        yc.setCode("EURIRS");
        ycDAO.loadByIdx(yc);
        if (yc != null) {
            Yield_curve_item ycItem = null;
            for (YieldNode node : nodes) {
                ycItem = new Yield_curve_item();
                ycItem.setRic(node.getRic());
                ycItem.setAsk(node.getAsk() / 100.);
                ycItem.setBid(node.getBid() / 100.);
                ycItem.setOffset_type(node.getOffsetType().ordinal());
                ycItem.setOffset_value(node.getOffset());
                ycItem.setYield_curve(yc.getId_yield_curve());
                ycItemDAO.insertOrUpdate(ycItem);
            }
        }
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        try {
            Sole24hProvider.getInstance().refresh(new org.softcaster.commons.types.Date().sqlDate(), "");
            nodes = Sole24hProvider.getInstance().getIrsRates();
            saveNodes();
        } catch (Exception ex) {
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

    public static EurirsImportMgr getInstance() {
        if (_instance == null) {
            _instance = new EurirsImportMgr();
        }
        return _instance;
    }
}
