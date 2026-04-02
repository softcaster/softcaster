/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import org.softcaster.commons.utils.LoggerMgr;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class YieldCurveImportMgr implements IImportMgr {

    public YieldCurveImportMgr() {
        
    }
    
    @Override
    public void start(IProgressInfo progressInfo) {
        try {
            IImportMgr importMgr = EcbYcImportMgr.getInstance();
            importMgr.start(progressInfo);
            if (progressInfo != null) {
                progressInfo.setProgress(10);
            }
            
            importMgr = ItaYcImportMgr.getInstance();
            importMgr.start(progressInfo);
            if (progressInfo != null) {
                progressInfo.setProgress(30);
            }

            importMgr = UsaYcImportMgr.getInstance();
            importMgr.start(progressInfo);
            if (progressInfo != null) {
                progressInfo.setProgress(50);
            }
            
            importMgr = EuriborImportMgr.getInstance();
            importMgr.start(progressInfo);
            if (progressInfo != null) {
                progressInfo.setProgress(70);
            }
            
            importMgr = EurirsImportMgr.getInstance();
            importMgr.start(progressInfo);
            if (progressInfo != null) {
                progressInfo.setProgress(90);
            }
            
            importMgr = SofrImportMgr.getInstance();
            importMgr.start(progressInfo);
            if (progressInfo != null) {
                progressInfo.setProgress(100);
            }
            
        } catch(Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            terminate();
        }
    }

    @Override
    public void terminate() {
    }
    
}
