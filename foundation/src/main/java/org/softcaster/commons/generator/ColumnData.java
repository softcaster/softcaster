/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

/**
 *
 * @author emy
 */
public class ColumnData {
    private String columnName = "";
    private  String datatype = "";
    private final int columnType = -1;
    private  String columnsize = "";
    private  String decimaldigits = "";
    private  boolean isNullable = false;
    private  boolean isAutoIncrment = false;    

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
     * @return the datatype
     */
    public String getDatatype() {
        return datatype;
    }

    /**
     * @param datatype the datatype to set
     */
    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    /**
     * @return the columnsize
     */
    public String getColumnsize() {
        return columnsize;
    }

    /**
     * @param columnsize the columnsize to set
     */
    public void setColumnsize(String columnsize) {
        this.columnsize = columnsize;
    }

    /**
     * @return the decimaldigits
     */
    public String getDecimaldigits() {
        return decimaldigits;
    }

    /**
     * @param decimaldigits the decimaldigits to set
     */
    public void setDecimaldigits(String decimaldigits) {
        this.decimaldigits = decimaldigits;
    }

    /**
     * @return the isNullable
     */
    public boolean getIsNullable() {
        return isNullable;
    }

    /**
     * @param isNullable the isNullable to set
     */
    public void setIsNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * @return the isAutoIncrment
     */
    public boolean isIsAutoIncrment() {
        return isAutoIncrment;
    }

    /**
     * @param isAutoIncrment the isAutoIncrment to set
     */
    public void setIsAutoIncrment(boolean isAutoIncrment) {
        this.isAutoIncrment = isAutoIncrment;
    }
    
    
}
