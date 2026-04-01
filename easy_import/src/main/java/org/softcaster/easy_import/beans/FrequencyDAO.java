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

public class FrequencyDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO frequency(id_frequency,code,description,year_fraction) VALUES(nextval(('frequency_s'::text)::regclass),?,?,?)";
    private final String updateExpr = "UPDATE frequency SET code=?,description=?,year_fraction=? WHERE id_frequency=?";
    private final String removeExpr = "DELETE FROM frequency WHERE id_frequency_id=?";
    private final String selectExpr = "SELECT id_frequency FROM frequency WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_frequency,code,description,year_fraction FROM frequency WHERE id_frequency=?";
    private final String selectByIdxExpr = "SELECT id_frequency,code,description,year_fraction FROM frequency WHERE code=?";

    public FrequencyDAO() {
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

    public boolean insert(Frequency record) {
        errorMsg = "";
        try {
            insertStmt.setString(1,record.getCode());
            insertStmt.setString(2,record.getDescription());
            insertStmt.setInt(3,record.getYear_fraction());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Frequency record) {
        errorMsg = "";
        try {
            updateStmt.setString(1,record.getCode());
            updateStmt.setString(2,record.getDescription());
            updateStmt.setInt(3,record.getYear_fraction());
            updateStmt.setInt(4,record.getId_frequency());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Frequency record) {
        errorMsg = "";
        try {
            selectStmt.setString(1,record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_frequency(rs.getInt("id_frequency"));
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

    public boolean remove(Frequency record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_frequency());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Frequency record) {
        errorMsg = "";
        try {
            boolean found=false;
            selectByPKeyStmt.setInt(1,record.getId_frequency());
            ResultSet rs = selectByPKeyStmt.executeQuery();
            if (rs.next()) {
                found=true;
                record.setId_frequency(rs.getInt("id_frequency"));
                record.setCode(rs.getString("code"));
                record.setDescription(rs.getString("description"));
                record.setYear_fraction(rs.getInt("year_fraction"));
            }
            rs.close();
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Frequency record) {
        errorMsg = "";
        try {
            boolean found=false;
            selectByIdxStmt.setString(1,record.getCode());
            ResultSet rs = selectByIdxStmt.executeQuery();
            if (rs.next()) {
                found=true;
                record.setId_frequency(rs.getInt("id_frequency"));
                record.setCode(rs.getString("code"));
                record.setDescription(rs.getString("description"));
                record.setYear_fraction(rs.getInt("year_fraction"));
            }
            rs.close();
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Frequency> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Frequency> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_frequency,code,description,year_fraction FROM frequency " + whereExpr;
            ConnectioManager cm = ConnectioManager.getInstance();
            PreparedStatement selectByWhereStmt = cm.createPreparedStatement(selectByWhereExpr);
            if(params.size() > 0) {
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
            ResultSet rs = selectByWhereStmt.executeQuery();
            Frequency record = null;
            while(rs.next()) {
                record = new Frequency();
                record.setId_frequency(rs.getInt("id_frequency"));
                record.setCode(rs.getString("code"));
                record.setDescription(rs.getString("description"));
                record.setYear_fraction(rs.getInt("year_fraction"));
                records.add(record);
            }
            rs.close();
            selectByWhereStmt.close();
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
