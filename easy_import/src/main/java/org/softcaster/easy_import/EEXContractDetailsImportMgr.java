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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;
import org.softcaster.easy_import.beans.EexColumnsMapping;
import org.softcaster.easy_import.beans.EexContractRow;
import org.springframework.stereotype.Service;

@Service("EEX Contract Details")
public class EEXContractDetailsImportMgr implements IImportMgr {

    private EexColumnsMapping ecm = null;
    private IProgressInfo progressInfo = null;

    @Override
    public void start(IProgressInfo progressInfo) {
        this.progressInfo = progressInfo;
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String fileName = paramsMgr.getParamValue("EEX_CONTR_DETAILS");
        Path file = Paths.get(IMPORT_PATH + "/" + fileName);

        try (InputStream is = Files.newInputStream(file); Workbook workbook = WorkbookFactory.create(is)) {

            for (Sheet sheet : workbook) {

                int headerIndex = findHeaderRow(sheet);
                Row headerRow = sheet.getRow(headerIndex);
                if (headerRow == null) {
                    throw new IllegalStateException("No header found");
                }

                ecm = new EexColumnsMapping(headerRow);
                List<EexContractRow> eexContractRowList = new ArrayList<>();

                int total = sheet.getLastRowNum();
                int current = 0;

                for (int r = headerIndex + 1;
                        r <= sheet.getLastRowNum();
                        r++) {

                    Row row = sheet.getRow(r);

                    if (row == null) {
                        continue;
                    }

                    // parse
                    if (!parseRow(row, ecm, eexContractRowList)) {
                        System.out.println("Error reading line: " + r);
                    }

                    if (this.progressInfo != null) {
                        int percent = (int) ((current / (double) total) * 100);
                        this.progressInfo.updateProgress("Importing " + r + " (" + current + "/" + total + ")", percent);
                    }

                }
                int size = eexContractRowList.size();
                System.out.println(size);
            }
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    @Override
    public void terminate() {
        if (progressInfo != null) {
             progressInfo.updateProgress("End Importing ", 100);
        }
    }

    private int findHeaderRow(Sheet sheet) {

        for (Row row : sheet) {
            for (Cell cell : row) {
                String value = cell.toString().trim();
                if ("PRODUCT_ID".equalsIgnoreCase(value)) {
                    return row.getRowNum();
                }
            }
        }

        throw new IllegalStateException(
                "Header PRODUCT_ID not found"
        );
    }

    private boolean parseRow(Row row, EexColumnsMapping ecm, List<EexContractRow> eexContractRowList) {

        Cell cell;
        EexContractRow eexContractRow = new EexContractRow();

        int index = ecm.getColumnIndex("PRODUCT_ID");
        cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        eexContractRow.setProductId(cell.getStringCellValue());

        index = ecm.getColumnIndex("EXPIRY_YEAR");
        cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        eexContractRow.setExpiryYear((int) cell.getNumericCellValue());

        index = ecm.getColumnIndex("EXPIRY_MONTH");
        cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        eexContractRow.setExpiryMonth((int) cell.getNumericCellValue());

        index = ecm.getColumnIndex("FIRST_TRADING_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        java.util.Date rawDate = cell.getDateCellValue();
        java.time.LocalDate dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setFirstTradingDate(dt);

        index = ecm.getColumnIndex("LAST_TRADING_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        rawDate = cell.getDateCellValue();
        dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setFirstTradingDate(dt);

        index = ecm.getColumnIndex("EXPIRY_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        rawDate = cell.getDateCellValue();
        dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setExpiryDate(dt);

        eexContractRowList.add(eexContractRow);
        return true;
    }
}
