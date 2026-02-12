/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author emy
 */
public class MetaDataMgr {

    private Connection conn = null;

    public Connection getConnection() throws SQLException {
        if (conn == null) {
            ParamsMgr paramsMgr = ParamsMgr.getInstance();
            if (paramsMgr != null) {
                String url = paramsMgr.getParamValue("DB_CONNECTION");
                Properties props = new Properties();
                props.setProperty("user", paramsMgr.getParamValue("DB_USER"));
                props.setProperty("password", paramsMgr.getParamValue("DB_PASSWORD"));
                // props.setProperty("ssl","true");
                conn = DriverManager.getConnection(url, props);
            }
        }

        return conn;
    }

    public void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    public List<String> getTables() throws SQLException {
        List<String> tables = new ArrayList<>();
        if (getConnection() != null) {
            DatabaseMetaData databaseMetaData = getConnection().getMetaData();
            String catalog = null;
            String schemaPattern = "public";
            String tableNamePattern = null;
            String[] types = new String[]{"TABLE"}; // solo tabelle

            ResultSet result = databaseMetaData.getTables(
                    catalog, schemaPattern, tableNamePattern, types);
            while (result.next()) {
                tables.add(result.getString(3));
            }
        }
        return tables;
    }

    public List<ColumnData> getColumns(String tableName) throws SQLException {
        List<ColumnData> columns = new ArrayList<>();
        if (getConnection() != null) {
            DatabaseMetaData databaseMetaData = getConnection().getMetaData();

            ResultSet result = databaseMetaData.getColumns(null, "public", tableName, null);
            while (result.next()) {
                ColumnData columnData = new ColumnData();
                columnData.setColumnName(result.getString("COLUMN_NAME"));
                columnData.setColumnsize(result.getString("COLUMN_SIZE"));
                columnData.setDatatype(result.getString("DATA_TYPE"));
                columnData.setDecimaldigits(result.getString("DECIMAL_DIGITS"));
                columnData.setIsNullable(result.getString("IS_NULLABLE").equalsIgnoreCase("TRUE"));
                columnData.setIsAutoIncrment(result.getString("IS_AUTOINCREMENT").equalsIgnoreCase("TRUE"));
                columns.add(columnData);
            }
        }
        return columns;
    }

    public ColumnData getPrimaryKey(String tableName) throws SQLException {
        ColumnData columnData = null;
        if (getConnection() != null) {
            DatabaseMetaData databaseMetaData = getConnection().getMetaData();
            ResultSet result = databaseMetaData.getPrimaryKeys(null, "public", tableName);
            while (result.next()) {
                columnData = new ColumnData();
                columnData.setColumnName(result.getString("COLUMN_NAME"));
            }
        }

        return columnData;
    }

    public Map<String, List<String>> getIndexes(String tableName) throws SQLException {
        DatabaseMetaData meta = getConnection().getMetaData();
        ResultSet rs = meta.getIndexInfo(null, null, tableName, true, true);
        IdxMetaData idxMetaData = new IdxMetaData();
        while (rs.next()) {
            idxMetaData.add(rs.getString("INDEX_NAME"), rs.getString("COLUMN_NAME"));
        }

        if (rs != null) {
            rs.close();
        }
        return idxMetaData.getIdxMap();
    }

    public Statement createStatement() throws SQLException {
        if (getConnection() != null) {
            try {
                return getConnection().createStatement();
            } catch (SQLException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public PreparedStatement createPreparedStatement(String sqlExpr) throws SQLException {
        if (getConnection() != null) {
            try {
                return getConnection().prepareStatement(sqlExpr);
            } catch (SQLException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public String decodeType(String type, boolean toJava) {
        int intType = Integer.parseInt(type);
        if (toJava) {
            return decodeTypeToJava(intType);
        } else {
            return decodeTypeToSql(intType);
        }
    }

    public String decodeTypeToJava(int type) {
        String typeName = "";
        switch (type) {
            case java.sql.Types.SMALLINT ->
                typeName = "Short";
            case java.sql.Types.INTEGER ->
                typeName = "Integer";
            case java.sql.Types.FLOAT, java.sql.Types.REAL, java.sql.Types.DOUBLE, java.sql.Types.DECIMAL, java.sql.Types.NUMERIC ->
                typeName = "Double";
            case java.sql.Types.DATE, java.sql.Types.TIMESTAMP, java.sql.Types.TIME ->
                typeName = "java.sql.Date";
            case java.sql.Types.VARCHAR ->
                typeName = "String";
        }
        return typeName;
    }

    public String decodeTypeToJavaScript(int type) {
        String typeName = "";
        switch (type) {
            case java.sql.Types.SMALLINT, java.sql.Types.INTEGER, java.sql.Types.FLOAT, java.sql.Types.REAL, java.sql.Types.DOUBLE, java.sql.Types.DECIMAL, java.sql.Types.NUMERIC ->
                typeName = "number";
            case java.sql.Types.VARCHAR ->
                typeName = "string";
            case java.sql.Types.DATE, java.sql.Types.TIMESTAMP, java.sql.Types.TIME ->
                typeName = "Date";
        }
        return typeName;
    }

    public String decodeTypeToSql(int type) {
        String typeName = "";
        switch (type) {
            case java.sql.Types.SMALLINT ->
                typeName = "SMALLINT";
            case java.sql.Types.INTEGER ->
                typeName = "INTEGER";
            case java.sql.Types.FLOAT ->
                typeName = "FLOAT";
            case java.sql.Types.REAL ->
                typeName = "REAL";
            case java.sql.Types.DOUBLE ->
                typeName = "DOUBLE";
            case java.sql.Types.DECIMAL ->
                typeName = "DECIMAL";
            case java.sql.Types.NUMERIC ->
                typeName = "NUMERIC";
            case java.sql.Types.VARCHAR ->
                typeName = "VARCHAR";
            case java.sql.Types.DATE ->
                typeName = "DATE";
            case java.sql.Types.TIMESTAMP ->
                typeName = "TIMESTAMP";
            case java.sql.Types.TIME ->
                typeName = "TIME";
        }
        return typeName;
    }

}
