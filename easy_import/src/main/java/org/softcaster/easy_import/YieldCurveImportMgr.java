/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.Arrays;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("Yield Curves")
public class YieldCurveImportMgr implements IImportMgr {

    public YieldCurveImportMgr() {

    }

    @Override
    @Async("importTaskExecutor")
    public void start(IProgressInfo progressInfo) {
        try {
            // Lista dei manager da eseguire in ordine
            List<IImportMgr> managers = Arrays.asList(
                    EcbYcImportMgr.getInstance(),
                    ItaYcImportMgr.getInstance(),
                    UsaYcImportMgr.getInstance(),
                    EuriborImportMgr.getInstance(),
                    EurirsImportMgr.getInstance(),
                    SofrImportMgr.getInstance()
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

}
