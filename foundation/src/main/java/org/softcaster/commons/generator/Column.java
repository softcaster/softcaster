/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

/**
 *
 * @author wbmaster
 */
public class Column {

    private String columnName = "";
    private int columnType = -1;

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the columnType
     */
    public int getColumnType() {
        return columnType;
    }

    /**
     * @param columnType the columnType to set
     */
    public void setColumnType(int columnType) {
        this.columnType = columnType;
    }
}
