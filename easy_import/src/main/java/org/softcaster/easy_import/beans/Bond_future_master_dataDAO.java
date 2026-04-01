// File generato automaticamente. Non modificare!
package org.softcaster.easy_import.beans;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.softcaster.commons.generator.ConnectioManager;
import org.softcaster.commons.generator.DATA_TYPE;
import org.softcaster.commons.generator.JdbcParam;
import org.softcaster.commons.utils.LoggerMgr;

public class Bond_future_master_dataDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;

    private final String insertExpr = "INSERT INTO bond_future_master_data(id_master_data,contract_value,tick_size,initial_margin) VALUES(?,?,?,?)";
    private final String updateExpr = "UPDATE bond_future_master_data SET contract_value=?,tick_size=?,initial_margin=? WHERE id_master_data=?";
    private final String removeExpr = "DELETE FROM bond_future_master_data WHERE id_master_data_id=?";
    private final String selectByPKeyExpr = "SELECT id_master_data,contract_value,tick_size,initial_margin FROM bond_future_master_data WHERE id_master_data=?";

    public Bond_future_master_dataDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
        }
    }

    public boolean insert(Bond_future_master_data record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getId_master_data());
            insertStmt.setDouble(2, record.getContract_value());
            insertStmt.setDouble(3, record.getTick_size());
            insertStmt.setDouble(4, record.getInitial_margin());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Bond_future_master_data record) {
        errorMsg = "";
        try {
            updateStmt.setDouble(1, record.getContract_value());
            updateStmt.setDouble(2, record.getTick_size());
            updateStmt.setDouble(3, record.getInitial_margin());
            updateStmt.setInt(4, record.getId_master_data());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
    
    public boolean insertOrUpdate(Bond_future_master_data record) {
        errorMsg = "";
        try {
            selectByPKeyStmt.setInt(1, record.getId_master_data());
            ResultSet rs = selectByPKeyStmt.executeQuery();
            if (rs.next()) {
                record.setId_master_data(rs.getInt("id_master_data"));
                rs.close();
                return update(record);
            } else {
                rs.close();
                return insert(record);
            }
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean remove(Bond_future_master_data record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_master_data());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Bond_future_master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_master_data());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setContract_value(rs.getDouble("contract_value"));
                    record.setTick_size(rs.getDouble("tick_size"));
                    record.setInitial_margin(rs.getDouble("initial_margin"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Bond_future_master_data> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Bond_future_master_data> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_master_data,master_data,contract_value,tick_size,initial_margin FROM bond_future_master_data " + whereExpr;
            ConnectioManager cm = ConnectioManager.getInstance();
            try (PreparedStatement selectByWhereStmt = cm.createPreparedStatement(selectByWhereExpr)) {
                if (!params.isEmpty()) {
                    int counter = 1;
                    Iterator<JdbcParam> iter = params.iterator();
                    JdbcParam param = null;
                    while (iter.hasNext()) {
                        param = iter.next();
                        if (param != null) {
                            int paramType = param.getValueType();
                            switch (paramType) {
                                case DATA_TYPE.TEXT ->
                                    selectByWhereStmt.setString(counter, param.getStringValue());
                                case DATA_TYPE.INTEGER ->
                                    selectByWhereStmt.setInt(counter, param.getIntegerValue());
                                case DATA_TYPE.LONG ->
                                    selectByWhereStmt.setLong(counter, param.getLongValue());
                                case DATA_TYPE.REAL ->
                                    selectByWhereStmt.setDouble(counter, param.getDoubleValue());
                                case DATA_TYPE.DATE ->
                                    selectByWhereStmt.setDate(counter, param.getDateValue());
                                default -> {
                                }
                            }
                        }
                        counter++;
                    }
                }
                try (ResultSet rs = selectByWhereStmt.executeQuery()) {
                    Bond_future_master_data record = null;
                    while (rs.next()) {
                        record = new Bond_future_master_data();
                        record.setId_master_data(rs.getInt("id_master_data"));
                        record.setContract_value(rs.getDouble("contract_value"));
                        record.setTick_size(rs.getDouble("tick_size"));
                        record.setInitial_margin(rs.getDouble("initial_margin"));
                        records.add(record);
                    }
                }
            }
            return records;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return null;
        }
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean closeStatements() {
        try {
            insertStmt.close();
            updateStmt.close();
            removeStmt.close();
            selectByPKeyStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
