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

public class Roll_conventionDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO roll_convention(id_roll_convention,code,description) VALUES(nextval(('roll_convention_s'::text)::regclass),?,?)";
    private final String updateExpr = "UPDATE roll_convention SET code=?,description=? WHERE id_roll_convention=?";
    private final String removeExpr = "DELETE FROM roll_convention WHERE id_roll_convention_id=?";
    private final String selectExpr = "SELECT id_roll_convention FROM roll_convention WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_roll_convention,code,description FROM roll_convention WHERE id_roll_convention=?";
    private final String selectByIdxExpr = "SELECT id_roll_convention,code,description FROM roll_convention WHERE code=?";

    public Roll_conventionDAO() {
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

    public boolean insert(Roll_convention record) {
        errorMsg = "";
        try {
            insertStmt.setString(1,record.getCode());
            insertStmt.setString(2,record.getDescription());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Roll_convention record) {
        errorMsg = "";
        try {
            updateStmt.setString(1,record.getCode());
            updateStmt.setString(2,record.getDescription());
            updateStmt.setInt(3,record.getId_roll_convention());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Roll_convention record) {
        errorMsg = "";
        try {
            selectStmt.setString(1,record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_roll_convention(rs.getInt("id_roll_convention"));
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

    public boolean remove(Roll_convention record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_roll_convention());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Roll_convention record) {
        errorMsg = "";
        try {
            boolean found=false;
            selectByPKeyStmt.setInt(1,record.getId_roll_convention());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found=true;
                    record.setId_roll_convention(rs.getInt("id_roll_convention"));
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

    public boolean loadByIdx(Roll_convention record) {
        errorMsg = "";
        try {
            boolean found=false;
            selectByIdxStmt.setString(1,record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found=true;
                    record.setId_roll_convention(rs.getInt("id_roll_convention"));
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

    public List<Roll_convention> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Roll_convention> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_roll_convention,code,description FROM roll_convention " + whereExpr;
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
                                case DATA_TYPE.TEXT -> selectByWhereStmt.setString(counter,param.getStringValue());
                                case DATA_TYPE.INTEGER -> selectByWhereStmt.setInt(counter,param.getIntegerValue());
                                case DATA_TYPE.LONG -> selectByWhereStmt.setLong(counter,param.getLongValue());
                                case DATA_TYPE.REAL -> selectByWhereStmt.setDouble(counter,param.getDoubleValue());
                                case DATA_TYPE.DATE -> selectByWhereStmt.setDate(counter,param.getDateValue());
                                default -> {
                                }
                            }
                        }
                        counter++;
                    }
                }
                try (ResultSet rs = selectByWhereStmt.executeQuery()) {
                    Roll_convention record = null;
                    while(rs.next()) {
                        record = new Roll_convention();
                        record.setId_roll_convention(rs.getInt("id_roll_convention"));
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
