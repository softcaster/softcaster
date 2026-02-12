/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.commons.xls;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author ep
 */
public class XlsUtils {

    private String sheetName = "";
    private InputData inputData = null;

    public static void applyNumericFormat(Workbook workbook, Row row, Cell cell, Double value, String styleFormat) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat(styleFormat));
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static void applyDateFormat(Workbook workbook, Row row, Cell cell, java.util.Date value, String styleFormat) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat(styleFormat));
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static void applyStringFormat(Workbook workbook, Row row, Cell cell, String value, String styleFormat) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    public static void applyBooleanFormat(Workbook workbook, Row row, Cell cell, boolean value, String styleFormat) {
        CellStyle style = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        style.setDataFormat(createHelper.createDataFormat().getFormat("BOOLEAN"));
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /**
     * @return the sheetName
     */
    public String getSheetName() {
        return sheetName;
    }

    /**
     * @param sheetName the sheetName to set
     */
    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void write() throws IOException {
        if (inputData.getHeader().getHeaders().isEmpty()) {
            return;
        }
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(getSheetName());

            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(font);

            int cols = 0;
            int rows = 0;
            Row header = sheet.createRow(rows);
            for (org.softcaster.commons.xls.Header rowHeader : inputData.getHeader().getHeaders()) {
                sheet.setColumnWidth(cols, 4000);
                Cell headerCell = header.createCell(cols);
                headerCell.setCellValue(rowHeader.getHeaderID());
                headerCell.setCellStyle(headerStyle);
                cols++;
            }
            rows++;

            for (Body body : inputData.getBody()) {
                Row row = sheet.createRow(rows);
                cols = 0;
                for (Object o : body.getRawValues()) {
                    Cell cell = row.createCell(cols);
                    Type headerType = inputData.getHeader().getHeaders().get(cols).getHeaderType();
                    switch (headerType) {
                        case STRING -> applyStringFormat(workbook, row, cell, (String) o, "0");
                        case SHORT -> {
                            Short shortValue = (Short) (o);
                            applyNumericFormat(workbook, row, cell, shortValue.doubleValue(), "0");
                        }
                        case INTEGER -> {
                            Integer intValue = (Integer) (o);
                            applyNumericFormat(workbook, row, cell, intValue.doubleValue(), "0");
                        }
                        case DOUBLE -> applyNumericFormat(workbook, row, cell, (Double) o, "0.00");
                        case DATE -> applyDateFormat(workbook, row, cell, (java.util.Date) o, "0");
                        case BOOLEAN -> applyBooleanFormat(workbook, row, cell, (Boolean) o, "0");
                        default -> {
                        }
                    }
                    cols++;
                }
                rows++;
            }

            File currDir = new File(".");
            String path = currDir.getAbsolutePath();
            String fileLocation = path.substring(0, path.length() - 1) + getSheetName();

            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
        }
    }

    public ByteArrayOutputStream download() throws IOException {
        if (inputData.getHeader().getHeaders().isEmpty()) {
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(getSheetName());

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(font);

        int cols = 0;
        int rows = 0;
        Row header = sheet.createRow(rows);
        for (org.softcaster.commons.xls.Header rowHeader : inputData.getHeader().getHeaders()) {
            sheet.setColumnWidth(cols, 4000);
            Cell headerCell = header.createCell(cols);
            headerCell.setCellValue(rowHeader.getHeaderID());
            headerCell.setCellStyle(headerStyle);
            cols++;
        }
        rows++;

        for (Body body : inputData.getBody()) {
            Row row = sheet.createRow(rows);
            cols = 0;
            for (Object o : body.getRawValues()) {
                Cell cell = row.createCell(cols);
                Type headerType = inputData.getHeader().getHeaders().get(cols).getHeaderType();
                switch (headerType) {
                    case STRING -> applyStringFormat(workbook, row, cell, (String) o, "0");
                    case SHORT -> {
                        Short shortValue = (Short) (o);
                        applyNumericFormat(workbook, row, cell, shortValue.doubleValue(), "0");
                    }
                    case INTEGER -> {
                        Integer intValue = (Integer) (o);
                        applyNumericFormat(workbook, row, cell, intValue.doubleValue(), "0");
                    }
                    case DOUBLE -> applyNumericFormat(workbook, row, cell, (Double) o, "0.00");
                    case DATE -> applyDateFormat(workbook, row, cell, (java.util.Date) o, "0");
                    case BOOLEAN -> applyBooleanFormat(workbook, row, cell, (Boolean) o, "0");
                    default -> {
                    }
                }
                cols++;
            }
            rows++;
        }

        workbook.write(outputStream);
        outputStream.close();
        return outputStream;
    }

    /**
     * @return the inputData
     */
    public InputData getInputData() {
        return inputData;
    }

    /**
     * @param inputData the inputData to set
     */
    public void setInputData(InputData inputData) {
        this.inputData = inputData;
    }

}
