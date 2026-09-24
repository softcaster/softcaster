/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;
import org.softcaster.easy_import.beans.EexColumnsMapping;
import org.softcaster.easy_import.beans.EexContractRow;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("EEX Contract Details")
public class EEXContractDetailsImportMgr implements IImportMgr {

    private EexColumnsMapping ecm = null;

    @Override
    @Async("importTaskExecutor")
    public void start(IProgressInfo progressInfo) {
        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        String fileName = paramsMgr.getParamValue("EEX_CONTR_DETAILS");
        Path file = Paths.get(IMPORT_PATH + "/" + fileName);

        String tokens = paramsMgr.getParamValue("EEX_TOKENS");
        List<String> tokenList = getTokens(tokens);

        String token = "DEBY";
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
                int step = total / 100;
                int cnt = 0;

                LocalDate today = LocalDate.now();
                for (int r = headerIndex + 1;
                        r <= sheet.getLastRowNum();
                        r++) {

                    Row row = sheet.getRow(r);

                    if (row == null) {
                        continue;
                    }

                    // parse
                    if (hasToken(tokenList, row, ecm)) {
                        EexContractRow last = parseRow(today, row, ecm, eexContractRowList);
                        if (last == null) {
                            System.out.println("Error reading line: " + r);
                            break;
                        }
                    }
                    if (progressInfo != null) {
                        if (cnt == step) {
                            progressInfo.updateProgress("Scanning: " + r + " records", cnt);
                            cnt = 0;
                        } else {
                            cnt++;
                        }
                    }
                }
                progressInfo.updateProgress("Imported: " + eexContractRowList.size() + " records", cnt);
            }
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        } finally {
            progressInfo.updateProgress("Import terminated successfully", 100);
            terminate();
        }
    }

    @Override
    public void terminate() {
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

    private EexContractRow parseRow(LocalDate officialDate, Row row, EexColumnsMapping ecm, List<EexContractRow> eexContractRowList) {

        Cell cell;
        EexContractRow eexContractRow = new EexContractRow();

        int index = ecm.getColumnIndex("PRODUCT_ID");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        eexContractRow.setProductId(cell.getStringCellValue());

        index = ecm.getColumnIndex("EXPIRY_YEAR");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        eexContractRow.setExpiryYear((int) cell.getNumericCellValue());

        index = ecm.getColumnIndex("EXPIRY_MONTH");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        eexContractRow.setExpiryMonth((int) cell.getNumericCellValue());

        index = ecm.getColumnIndex("FIRST_TRADING_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        java.util.Date rawDate = cell.getDateCellValue();
        java.time.LocalDate dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setFirstTradingDate(dt);

        index = ecm.getColumnIndex("LAST_TRADING_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        rawDate = cell.getDateCellValue();
        dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setLastTradingDate(dt);

        index = ecm.getColumnIndex("EXPIRY_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        rawDate = cell.getDateCellValue();
        dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setExpiryDate(dt);

        index = ecm.getColumnIndex("FIRST_DELIVERY_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        rawDate = cell.getDateCellValue();
        dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setFirstDeliveryDate(dt);

        index = ecm.getColumnIndex("LAST_DELIVERY_DATE");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        rawDate = cell.getDateCellValue();
        dt = LocalDate.ofInstant(rawDate.toInstant(), ZoneId.systemDefault());
        eexContractRow.setLastDeliveryDate(dt);

        index = ecm.getColumnIndex("CONTRACT_SIZE");
        cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        eexContractRow.setContractSize(BigDecimal.valueOf(cell.getNumericCellValue()));

        if (!eexContractRow.getLastDeliveryDate().isBefore(officialDate)) {
            eexContractRowList.add(eexContractRow);
        }
        return eexContractRow;
    }

    private boolean hasToken(List<String> tokens, Row row, EexColumnsMapping ecm) {
        int index = ecm.getColumnIndex("PRODUCT_ID");
        Cell cell = row.getCell(index);
        if (cell == null) {
            return false;
        }
        for (String token : tokens) {
            if (cell.getStringCellValue().equals(token)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getTokens(String tokens) {
        // Eliminare spazi bianchi extra 
        return Arrays.asList(tokens.split("\\s*,\\s*"));
    }
}
