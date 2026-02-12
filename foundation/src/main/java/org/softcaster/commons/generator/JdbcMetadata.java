/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrea
 */
public class JdbcMetadata {

    // Torna lista colonne associate alla tabella in input
    public List<Column> getColumns(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        List<Column> columns;
        try (ResultSet rs = meta.getColumns(null, null, tableName, null)) {
            columns = new ArrayList<>();
            Column column;
            while (rs.next()) {
                column = new Column();
                column.setColumnName(rs.getString("COLUMN_NAME"));
                column.setColumnType(decodeType(rs.getString("TYPE_NAME")));
                //System.out.println(rs.getString("TYPE_NAME"));
                columns.add(column);
            }
        }
        return columns;
    }

    // Torna colonne chiave primaria della tabella
    public List<Column> getPrimaryKey(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        List<Column> columns;
        try (ResultSet rs = meta.getPrimaryKeys(null, null, tableName)) {
            columns = new ArrayList<>();
            Column column;
            while (rs.next()) {
                column = new Column();
                column.setColumnName(rs.getString("COLUMN_NAME"));
                column.setColumnType(-1);
                columns.add(column);
            }
        }
        return columns;
    }

    public Map<String, List<String>> getIndexes(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        IdxMetaData idxMetaData;
        try (ResultSet rs = meta.getIndexInfo(null, null, tableName, true, true)) {
            idxMetaData = new IdxMetaData();
            while (rs.next()) {
                idxMetaData.add(rs.getString("INDEX_NAME"), rs.getString("COLUMN_NAME"));
            }
        }
        return idxMetaData.getIdxMap();
    }

    // Elenco tabelle dello schema
    public List<String> getTables(Connection conn, String schema) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String[] types = {"TABLE"};
        List<String> tables;
        try (ResultSet rs = meta.getTables(null, schema, "%", types)) {
            tables = new ArrayList<>();
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    private Integer decodeType(String type) {
        if (type.equalsIgnoreCase("int2")) {
            return DATA_TYPE.INTEGER;
        } else if (type.equalsIgnoreCase("int4")) {
            return DATA_TYPE.INTEGER;
        } else if (type.equalsIgnoreCase("serial")) {
            return DATA_TYPE.LONG;
        } else if (type.equalsIgnoreCase("varchar")) {
            return DATA_TYPE.TEXT;
        } else if (type.equalsIgnoreCase("bpchar")) {
            return DATA_TYPE.TEXT;
        } else if (type.equalsIgnoreCase("text")) {
            return DATA_TYPE.TEXT;
        } else if (type.equalsIgnoreCase("numeric")) {
            return DATA_TYPE.REAL;
        } else if (type.equalsIgnoreCase("date")) {
            return DATA_TYPE.DATE;
        } else if (type.equalsIgnoreCase("time")) {
            return DATA_TYPE.DATE;
        } else if (type.equalsIgnoreCase("bool")) {
            return DATA_TYPE.BOOL;
        } else {
            return DATA_TYPE.INVALID;
        }
    }

    public static String getJavaType(int columnType) {
        switch (columnType) {
            case DATA_TYPE.DATE:
                return "org.softcaster.utils.Date";
            case DATA_TYPE.INTEGER:
                return "Integer";
            case DATA_TYPE.LONG:
                return "Long";
            case DATA_TYPE.REAL:
                return "Double";
            case DATA_TYPE.TEXT:
                return "String";
            case DATA_TYPE.BOOL:
                return "Boolean";
            case DATA_TYPE.INVALID:
            default:
                return "";
        }
    }

    public static String getSqlMethod(int columnType, boolean isSetMethod) {
        switch (columnType) {
            case DATA_TYPE.DATE:
                if (isSetMethod) {
                    return "setDate";
                } else {
                    return "getDate";
                }
            case DATA_TYPE.INTEGER:
                if (isSetMethod) {
                    return "setInt";
                } else {
                    return "getInt";
                }
            case DATA_TYPE.LONG:
                if (isSetMethod) {
                    return "setLong";
                } else {
                    return "getLong";
                }
            case DATA_TYPE.REAL:
                if (isSetMethod) {
                    return "setDouble";
                } else {
                    return "getDouble";
                }
            case DATA_TYPE.TEXT:
                if (isSetMethod) {
                    return "setString";
                } else {
                    return "getString";
                }
            case DATA_TYPE.BOOL:
                if (isSetMethod) {
                    return "setBoolean";
                } else {
                    return "getBoolean";
                }
            case DATA_TYPE.INVALID:
            default:
                return "";
        }
    }

    public static String getDefaultValue(int columnType) {
        switch (columnType) {
            case DATA_TYPE.DATE:
                return "null";
            case DATA_TYPE.INTEGER:
                return "0";
            case DATA_TYPE.LONG:
                return "0L";
            case DATA_TYPE.REAL:
                return "0.0";
            case DATA_TYPE.TEXT:
                return "\"\"";
            case DATA_TYPE.BOOL:
                return "false";
            case DATA_TYPE.INVALID:
            default:
                return "";
        }
    }

}
