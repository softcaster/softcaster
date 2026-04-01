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

public class CalendarDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO calendar(id_calendar,code,description) VALUES(nextval(('calendar_s'::text)::regclass),?,?)";
    private final String updateExpr = "UPDATE calendar SET code=?,description=? WHERE id_calendar=?";
    private final String removeExpr = "DELETE FROM calendar WHERE id_calendar_id=?";
    private final String selectExpr = "SELECT id_calendar FROM calendar WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_calendar,code,description FROM calendar WHERE id_calendar=?";
    private final String selectByIdxExpr = "SELECT id_calendar,code,description FROM calendar WHERE code=?";

    public CalendarDAO() {
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

    public boolean insert(Calendar record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getCode());
            insertStmt.setString(2, record.getDescription());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Calendar record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getCode());
            updateStmt.setString(2, record.getDescription());
            updateStmt.setInt(3, record.getId_calendar());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Calendar record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_calendar(rs.getInt("id_calendar"));
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

    public boolean remove(Calendar record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_calendar());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Calendar record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_calendar());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_calendar(rs.getInt("id_calendar"));
                    record.setCode(rs.getString("code"));
                    record.setDescription(rs.getString("description"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Calendar record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_calendar(rs.getInt("id_calendar"));
                    record.setCode(rs.getString("code"));
                    record.setDescription(rs.getString("description"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Calendar> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Calendar> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_calendar,code,description FROM calendar " + whereExpr;
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
                                case DATA_TYPE.TEXT:
                                    selectByWhereStmt.setString(counter, param.getStringValue());
                                    break;
                                case DATA_TYPE.INTEGER:
                                    selectByWhereStmt.setInt(counter, param.getIntegerValue());
                                    break;
                                case DATA_TYPE.LONG:
                                    selectByWhereStmt.setLong(counter, param.getLongValue());
                                    break;
                                case DATA_TYPE.REAL:
                                    selectByWhereStmt.setDouble(counter, param.getDoubleValue());
                                    break;
                                case DATA_TYPE.DATE:
                                    selectByWhereStmt.setDate(counter, param.getDateValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        counter++;
                    }
                }
                try (ResultSet rs = selectByWhereStmt.executeQuery()) {
                    Calendar record = null;
                    while (rs.next()) {
                        record = new Calendar();
                        record.setId_calendar(rs.getInt("id_calendar"));
                        record.setCode(rs.getString("code"));
                        record.setDescription(rs.getString("description"));
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
