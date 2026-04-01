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

public class HolidayDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;

    private final String insertExpr = "INSERT INTO holiday(id_holiday,calendar,holiday_day,holiday_month) VALUES(nextval(('holiday_s'::text)::regclass),?,?,?)";
    private final String updateExpr = "UPDATE holiday SET calendar=?,holiday_day=?,holiday_month=? WHERE id_holiday=?";
    private final String removeExpr = "DELETE FROM holiday WHERE id_holiday_id=?";
    private final String selectByPKeyExpr = "SELECT id_holiday,calendar,holiday_day,holiday_month FROM holiday WHERE id_holiday=?";

    public HolidayDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
        }
    }

    public boolean insert(Holiday record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1,record.getCalendar());
            insertStmt.setInt(2,record.getHoliday_day());
            insertStmt.setInt(3,record.getHoliday_month());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Holiday record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1,record.getCalendar());
            updateStmt.setInt(2,record.getHoliday_day());
            updateStmt.setInt(3,record.getHoliday_month());
            updateStmt.setInt(4,record.getId_holiday());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean remove(Holiday record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_holiday());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Holiday record) {
        errorMsg = "";
        try {
            boolean found=false;
            selectByPKeyStmt.setInt(1,record.getId_holiday());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found=true;
                    record.setId_holiday(rs.getInt("id_holiday"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setHoliday_day(rs.getInt("holiday_day"));
                    record.setHoliday_month(rs.getInt("holiday_month"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Holiday> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Holiday> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_holiday,calendar,holiday_day,holiday_month FROM holiday " + whereExpr;
            ConnectioManager cm = ConnectioManager.getInstance();
            try (PreparedStatement selectByWhereStmt = cm.createPreparedStatement(selectByWhereExpr)) {
                if(!params.isEmpty()) {
                    int counter = 1;
                    Iterator<JdbcParam> iter = params.iterator();
                    JdbcParam param = null;
                    while(iter.hasNext()) {
                        param = iter.next();
                        if(param != null) {
                            int paramType = param.getValueType();
                            switch(paramType) {
                                case DATA_TYPE.TEXT:
                                    selectByWhereStmt.setString(counter,param.getStringValue());
                                    break;
                                case DATA_TYPE.INTEGER:
                                    selectByWhereStmt.setInt(counter,param.getIntegerValue());
                                    break;
                                case DATA_TYPE.LONG:
                                    selectByWhereStmt.setLong(counter,param.getLongValue());
                                    break;
                                case DATA_TYPE.REAL:
                                    selectByWhereStmt.setDouble(counter,param.getDoubleValue());
                                    break;
                                case DATA_TYPE.DATE:
                                    selectByWhereStmt.setDate(counter,param.getDateValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        counter++;
                    }
                }
                try (ResultSet rs = selectByWhereStmt.executeQuery()) {
                    Holiday record = null;
                    while(rs.next()) {
                        record = new Holiday();
                        record.setId_holiday(rs.getInt("id_holiday"));
                        record.setCalendar(rs.getInt("calendar"));
                        record.setHoliday_day(rs.getInt("holiday_day"));
                        record.setHoliday_month(rs.getInt("holiday_month"));
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
