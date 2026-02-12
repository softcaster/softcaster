/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;

/**
 *
 * @author andrea
 */
public class TablesManager {

    private Map<String, TableStructure> tables = null;
    private static TablesManager instance = null;

    private TablesManager() {
    }

    private boolean buildStructure() throws SQLException {
        ConnectioManager cm = ConnectioManager.getInstance();
        List<String> t = cm.getTables("public");
        if (t != null && !t.isEmpty()) {
            tables = new HashMap<>();
            Iterator<String> iter = t.iterator();
            TableStructure ts = null;
            while (iter.hasNext()) {
                ts = new TableStructure();
                ts.setTableName(iter.next());
                if (ts.buildStructure()) {
                    tables.put(ts.getTableName(), ts);
                } else {
                    LoggerMgr.logError("Impossibile costruire struttuta per: " + ts.getTableName());
                }
            }
        }
        return true;
    }

    public Map<String, TableStructure> getTables() {
        return tables;
    }

    public static TablesManager getInstance() throws SQLException {
        if (instance == null) {
            instance = new TablesManager();
            if (instance.buildStructure() == false) {
                return null;
            }
        }

        return instance;
    }

    public static void freeInstance() {
        if (instance != null) {
            ConnectioManager.freeInstance();
            instance = null;
        }
    }
}
