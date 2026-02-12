/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author wbmaster
 */
public class ConnectioManager {

    private final JdbcMetadata metadata = new JdbcMetadata();
    private Connection conn = null;
    // Instanza singleton
    private static ConnectioManager instance = null;

    // Non istanziabile
    protected ConnectioManager() {
    }

    protected boolean connect() {

        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        if (paramsMgr != null) {
            try {
                // Legge impostazioni connessione
                String driver = paramsMgr.getParamValue("DB_DRIVER");
                String url = paramsMgr.getParamValue("DB_CONNECTION");
                String user = paramsMgr.getParamValue("DB_USER");
                String password = paramsMgr.getParamValue("DB_PASSWORD");
                Class.forName(driver);
                conn = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException | SQLException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return false;
            }
        }
        return true;
    }

    // Accesso
    public static ConnectioManager getInstance() {
        if (instance == null) {
            instance = new ConnectioManager();
            if (!instance.connect()) {
                instance = null;
            }
        }
        return instance;
    }

    public static void freeInstance() {
        if (instance != null && instance.conn != null) {
            try {
                instance.conn.close();
            } catch (SQLException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
            }
            instance.conn = null;
            instance = null;
        }
    }

    public Statement createStatement() {
        if (instance != null && instance.conn != null) {
            try {
                return instance.conn.createStatement();
            } catch (SQLException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public PreparedStatement createPreparedStatement(String sqlExpr) {
        if (instance != null && instance.conn != null) {
            try {
                return instance.conn.prepareStatement(sqlExpr);
            } catch (SQLException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    public List<Column> getColumns(String tableName) throws SQLException {
        List<Column> columns;
        columns = metadata.getColumns(conn, tableName);
        return columns;
    }

    public List<Column> getPrimaryKey(String tableName) throws SQLException {
        List<Column> columns;
        columns = metadata.getPrimaryKey(conn, tableName);
        return columns;
    }

    public Map<String, List<String>> getIndexes(String tableName) throws SQLException {
        Map<String, List<String>> indexes;
        indexes = metadata.getIndexes(conn, tableName);
        return indexes;
    }

    public List<String> getTables(String schema) throws SQLException {
        return metadata.getTables(conn, schema);
    }
}
