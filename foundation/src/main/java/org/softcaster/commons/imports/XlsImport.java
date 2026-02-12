/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.imports;

import java.io.File;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 *
 * @author ep
 */
public class XlsImport {

    private int startData = 0;

    private Workbook workbook = null;
    private final Map<Integer, List<String>> buffer = new HashMap<>();

    public void startImport(ImportConfig config) throws Exception {
        startData = config.getStartData();
        FileInputStream file = new FileInputStream(new File(config.getFilePath().toString()));
        workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);
        int i = 0;
        for (Row row : sheet) {
            buffer.put(i, new ArrayList<>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        buffer.get(i).add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = cell.getDateCellValue();
                            String cellValue = df.format(date);
                            buffer.get(i).add(cellValue);
                        } else {
                            buffer.get(i).add(Double.toString(cell.getNumericCellValue()));
                        }
                        break;
                    case BOOLEAN:
                        buffer.get(i).add(Boolean.toString(cell.getBooleanCellValue()));
                        break;
                    case FORMULA:
                    default:
                        buffer.get(i).add(" ");
                }
            }
            i++;
        }
    }

    public Object[] getLine(int pos) {
        if (buffer != null) {
            if (pos < startData) {
                pos = startData;
            }
            return buffer.get(pos).toArray();
        } else {
            return null;
        }
    }

}
