/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.sql.SQLException;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;

/**
 *
 * @author emy
 */
public class FileUtils {

    protected String tableName = "";
    protected MetaDataMgr metaDataMgr = null;
    protected List<ColumnData> columns = null;
    protected ColumnData primaryKey = null;

    public FileUtils(String tableName) {
        this.tableName = tableName;
        metaDataMgr = new MetaDataMgr();
        try {
            primaryKey = metaDataMgr.getPrimaryKey(this.tableName);
            columns = metaDataMgr.getColumns(tableName);
        } catch (SQLException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            primaryKey = null;
            columns = null;
        }
    }

    public FileUtils() {
        this.tableName = "";
        metaDataMgr = new MetaDataMgr();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
        primaryKey = null;
        columns = null;
        try {
            primaryKey = metaDataMgr.getPrimaryKey(this.tableName);
            columns = metaDataMgr.getColumns(tableName);
        } catch (SQLException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            primaryKey = null;
            columns = null;
        }
    }

    public String getClassName() {
        return toCamelCase(tableName, true);
    }

    public List<String> getTables() throws SQLException {
        return metaDataMgr.getTables();
    }

    public ColumnData getColumnData(String columnName) {
        if (columns != null) {
            for (ColumnData columnData : columns) {
                if (columnData.getColumnName().equals(columnName)) {
                    return columnData;
                }
            }
        }
        return null;
    }

    protected String getSqlColumnType(String columnName) {
        String sqlColumnType = "";

        for (ColumnData columnData : columns) {
            if (columnData.getColumnName().equals(columnName)) {
                return metaDataMgr.decodeType(columnData.getDatatype(), false);
            }
        }

        return sqlColumnType;

    }

    protected String getJavaColumnType(String columnName) {
        String sqlColumnType = "";

        for (ColumnData columnData : columns) {
            if (columnData.getColumnName().equals(columnName)) {
                return metaDataMgr.decodeType(columnData.getDatatype(), true);
            }
        }

        return sqlColumnType;

    }

    protected static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase()
                + s.substring(1).toLowerCase();
    }

    public static String toCamelCase(String s, boolean useAsFunctionName) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        if (!useAsFunctionName) {
            camelCaseString = camelCaseString.substring(0, 1).toLowerCase() + camelCaseString.substring(1);
        }
        return camelCaseString;
    }

    public String getEndFile() {
        return "}\n";
    }

    public void disconnect() throws SQLException {
        if (metaDataMgr != null) {
            metaDataMgr.disconnect();
        }
    }
}
