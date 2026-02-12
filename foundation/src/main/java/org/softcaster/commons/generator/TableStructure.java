/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 *
 * @author andrea
 */
public class TableStructure {

    private String tableName = null;
    private List<Column> columns = null;
    private List<Column> pKeys = null;
    private Map<String, List<String>> indexes = null;

    public boolean buildStructure() throws SQLException {
        if (tableName == null || tableName.length() <= 0) {
            return false;
        }

        // leggo metadata tabella
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm == null) {
            return false;
        }

        // Colonne
        columns = cm.getColumns(tableName);

        // Indici
        indexes = cm.getIndexes(tableName);

        // PKey
        pKeys = cm.getPrimaryKey(tableName);

        return true;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the columns
     */
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * @return the pKeys
     */
    public List<Column> getpKeys() {
        return pKeys;
    }

    /**
     * @return the indexes
     */
    public Map<String, List<String>> getIndexes() {
        return indexes;
    }
}
