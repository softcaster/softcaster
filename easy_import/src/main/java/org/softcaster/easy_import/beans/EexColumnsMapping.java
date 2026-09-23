/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.beans;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 *
 * @author ep
 */
public class EexColumnsMapping {

    private final String[] expectedColumns
            = {"MARKET",
                "PRODUCT_ID",
                "EXPIRY_YEAR",
                "EXPIRY_MONTH",
                "FIRST_TRADING_DATE",
                "LAST_TRADING_DATE",
                "EXPIRY_DATE",
                "FIRST_DELIVERY_DATE",
                "LAST_DELIVERY_DATE",
                "BEGIN_BOM_SETTLEMENT",
                "DELIVERY_DAYS",
                "CONTRACT_SIZE",
                "BEGIN_EMF",
                "DELIVERY_WEEK",
                "UL_PRODUCT_ID",
                "UL_EXPIRY_YEAR",
                "UL_EXPIRY_MONTH"};

    private final Map<String, Integer> columns = new HashMap<>();

    public EexColumnsMapping(Row headerRow) {
        for (Cell cell : headerRow) {

            String name = cell.getStringCellValue()
                    .trim()
                    .toUpperCase();

            if (validateColumn(name)) {
                columns.put(name, cell.getColumnIndex());
            }
        }
    }

    private boolean validateColumn(String name) {
        for (String expectedColumn : expectedColumns) {
            if (expectedColumn.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public Integer getColumnIndex(String columnName) {
        return columns.get(columnName);
    }
}
