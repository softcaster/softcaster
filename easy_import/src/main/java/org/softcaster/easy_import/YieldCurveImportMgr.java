/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.Arrays;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_core.data.YieldCurve;
import org.softcaster.easy_pricer_core.data.YieldCurveDAO;
import org.softcaster.easy_pricer_core.data.YieldCurveItem;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.YieldNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("Yield Curves")
public class YieldCurveImportMgr implements IImportMgr {

    @Autowired
    private YieldCurveDAO dao;

    public YieldCurveImportMgr() {
    }

    @Override
    @Async("importTaskExecutor")
    public void start(IProgressInfo progressInfo) {
        try {
            // Lista dei manager da eseguire in ordine
            List<IImportMgr> managers = Arrays.asList(
                    EcbYcImportMgr.getInstance(dao),
                    ItaYcImportMgr.getInstance(dao),
                    UsaYcImportMgr.getInstance(dao),
                    EuriborImportMgr.getInstance(dao),
                    EurirsImportMgr.getInstance(dao),
                    SofrImportMgr.getInstance(dao)
            );

            int total = managers.size();
            int current = 1;

            for (int i = 0; i < managers.size(); i++) {
                try {
                    managers.get(i).start(progressInfo);
                    if (progressInfo != null) {
                        int percent = (int) ((current / (double) total) * 100);
                        progressInfo.updateProgress("Importing Yield Curve: " + managers.get(i).getImportInfo() + " (" + current + "/" + total + ")", percent);
                        current++;
                    }
                } catch (Exception ex) {
                    String error = "Errore durante l'import " + managers.get(i).getClass().getSimpleName() + ": " + ex.getMessage();
                    LoggerMgr.logError(error);
                    progressInfo.showError(error);
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            terminate();
        } finally {
            progressInfo.updateProgress("Import terminated successfully", 100);
            terminate();
        }
    }

    @Override
    public void terminate() {
    }

    // Accetta una lista di DataNode o di qualsiasi sua sottoclasse
    public static void saveNodes(List<? extends DataNode> rates, String curveId, YieldCurveDAO yieldCurveDAO) {
        if (yieldCurveDAO != null) {
            YieldCurve yieldCurve = yieldCurveDAO.findByCode(curveId);
            if (yieldCurve != null) {
                YieldCurveItem yieldCurveItem = null;
                for (DataNode node : rates) {
                    if (node instanceof YieldNode yieldNode) {
                        yieldCurveItem = yieldCurve.getItems().stream()
                                .filter(item -> item.getRic() != null && item.getRic().equals(yieldNode.getRic()))
                                .findFirst()
                                .orElse(null);

                        if (yieldCurveItem != null) {
                            // AGGIORNA i dati di mercato
                            yieldCurveItem.setBid(yieldNode.getBid() / 100.);
                            yieldCurveItem.setAsk(yieldNode.getAsk() / 100.);
                        } else {
                            yieldCurveItem = new YieldCurveItem();
                            yieldCurveItem.setRic(yieldNode.getRic());
                            yieldCurveItem.setBid(yieldNode.getBid() / 100.);
                            yieldCurveItem.setAsk(yieldNode.getAsk() / 100.);
                            yieldCurveItem.setOffsetType((short) yieldNode.getOffsetType().ordinal());
                            yieldCurveItem.setOffsetValue((short) yieldNode.getOffset());
                            yieldCurveItem.setYieldCurve(yieldCurve.getIdYieldCurve());
                            yieldCurve.getItems().add(yieldCurveItem);
                        }
                    }
                }
                yieldCurveDAO.saveOrUpdate(yieldCurve);
            }
        }
    }
}
