/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.softcaster.commons.xml.ParamsMgr;
import org.springframework.stereotype.Service;

@Service("EEX Contract Details")
public class EEXContractDetailsImportMgr implements IImportMgr {

    @Override
    public void start(IProgressInfo progressInfo) {
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String fileName = paramsMgr.getParamValue("EEX_CONTR_DETAILS");
        Path file = Paths.get(IMPORT_PATH + "/" + fileName);

        try (InputStream is = Files.newInputStream(file); Workbook workbook = WorkbookFactory.create(is)) {

            for (Sheet sheet : workbook) {

                System.out.println("SHEET: " + sheet.getSheetName());

                Iterator<Row> rows = sheet.iterator();

                if (!rows.hasNext()) {
                    continue;
                }

                // Header
                Row headerRow = rows.next();
            }
        } catch (IOException ex) {
            Logger.getLogger(EEXContractDetailsImportMgr.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void terminate() {
    }

}
