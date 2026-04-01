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

public class Forex_master_dataDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO forex_master_data(id_master_data,bcy,ccy,bcy_irc,ccy_irc) VALUES(?,?,?,?,?)";
    private final String updateExpr = "UPDATE forex_master_data SET bcy=?,ccy=?,bcy_irc=?,ccy_irc=? WHERE id_master_data=?";
    private final String removeExpr = "DELETE FROM forex_master_data WHERE id_master_data_id=?";
    private final String selectExpr = "SELECT id_master_data FROM forex_master_data WHERE bcy=? AND ccy=?";
    private final String selectByPKeyExpr = "SELECT id_master_data,bcy,ccy,bcy_irc,ccy_irc FROM forex_master_data WHERE id_master_data=?";
    private final String selectByIdxExpr = "SELECT id_master_data,bcy,ccy,bcy_irc,ccy_irc FROM forex_master_data WHERE bcy=? AND ccy=?";

    public Forex_master_dataDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectByIdxStmt = cm.createPreparedStatement(selectByIdxExpr);
            selectStmt = cm.createPreparedStatement(selectExpr);
        }
    }

    public boolean insert(Forex_master_data record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getId_master_data());
            insertStmt.setInt(2, record.getBcy());
            insertStmt.setInt(3, record.getCcy());
            insertStmt.setString(4, record.getBcy_irc());
            insertStmt.setString(5, record.getCcy_irc());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Forex_master_data record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1, record.getBcy());
            updateStmt.setInt(2, record.getCcy());
            updateStmt.setString(3, record.getBcy_irc());
            updateStmt.setString(4, record.getCcy_irc());
            updateStmt.setInt(5, record.getId_master_data());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Forex_master_data record) {
        errorMsg = "";
        try {
            selectStmt.setInt(1, record.getBcy());
            selectStmt.setInt(2, record.getCcy());
            ResultSet rs = selectStmt.executeQuery();
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

    public boolean remove(Forex_master_data record) {
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

    public boolean loadByPKey(Forex_master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_master_data());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setBcy(rs.getInt("bcy"));
                    record.setCcy(rs.getInt("ccy"));
                    record.setBcy_irc(rs.getString("bcy_irc"));
                    record.setCcy_irc(rs.getString("ccy_irc"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Forex_master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setInt(1, record.getBcy());
            selectByIdxStmt.setInt(2, record.getCcy());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setBcy(rs.getInt("bcy"));
                    record.setCcy(rs.getInt("ccy"));
                    record.setBcy_irc(rs.getString("bcy_irc"));
                    record.setCcy_irc(rs.getString("ccy_irc"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Forex_master_data> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Forex_master_data> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_master_data,bcy,ccy,bcy_irc,ccy_irc FROM currpair_master_data " + whereExpr;
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
                    Forex_master_data record = null;
                    while (rs.next()) {
                        record = new Forex_master_data();
                        record.setId_master_data(rs.getInt("id_master_data"));
                        record.setBcy(rs.getInt("bcy"));
                        record.setCcy(rs.getInt("ccy"));
                        record.setBcy_irc(rs.getString("bcy_irc"));
                        record.setCcy_irc(rs.getString("ccy_irc"));
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
            selectStmt.close();
            selectByIdxStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
