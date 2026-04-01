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

public class Yield_curveDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO yield_curve(id_yield_curve,code,description,currency,calendar,compounding) VALUES(nextval(('yield_curve_s'::text)::regclass),?,?,?,?,?)";
    private final String updateExpr = "UPDATE yield_curve SET code=?,description=?,currency=?,calendar=? compounding=? WHERE id_yield_curve=?";
    private final String removeExpr = "DELETE FROM yield_curve WHERE id_yield_curve_id=?";
    private final String selectExpr = "SELECT id_yield_curve FROM yield_curve WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_yield_curve,code,description,currency,calendar,compounding FROM yield_curve WHERE id_yield_curve=?";
    private final String selectByIdxExpr = "SELECT id_yield_curve,code,description,currency,calendar,compounding FROM yield_curve WHERE code=?";

    public Yield_curveDAO() {
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

    public boolean insert(Yield_curve record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getCode());
            insertStmt.setString(2, record.getDescription());
            insertStmt.setInt(3, record.getCurrency());
            insertStmt.setInt(4, record.getCalendar());
            insertStmt.setInt(5, record.getCompounding());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Yield_curve record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getCode());
            updateStmt.setString(2, record.getDescription());
            updateStmt.setInt(3, record.getCurrency());
            updateStmt.setInt(4, record.getCalendar());
            updateStmt.setInt(5, record.getCompounding());
            updateStmt.setInt(6, record.getId_yield_curve());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Yield_curve record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_yield_curve(rs.getInt("id_yield_curve"));
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

    public boolean remove(Yield_curve record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_yield_curve());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Yield_curve record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_yield_curve());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_yield_curve(rs.getInt("id_yield_curve"));
                    record.setCode(rs.getString("code"));
                    record.setDescription(rs.getString("description"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setCompounding(rs.getInt("compounding"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Yield_curve record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_yield_curve(rs.getInt("id_yield_curve"));
                    record.setCode(rs.getString("code"));
                    record.setDescription(rs.getString("description"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setCompounding(rs.getInt("compounding"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Yield_curve> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Yield_curve> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_yield_curve,code,description,currency,calendar,compounding FROM yield_curve " + whereExpr;
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
                    Yield_curve record = null;
                    while (rs.next()) {
                        record = new Yield_curve();
                        record.setId_yield_curve(rs.getInt("id_yield_curve"));
                        record.setCode(rs.getString("code"));
                        record.setDescription(rs.getString("description"));
                        record.setCurrency(rs.getInt("currency"));
                        record.setCalendar(rs.getInt("calendar"));
                        record.setCompounding(rs.getInt("compounding"));
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
