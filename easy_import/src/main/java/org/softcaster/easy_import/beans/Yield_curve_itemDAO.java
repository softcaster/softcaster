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

public class Yield_curve_itemDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;

    private final String insertExpr = "INSERT INTO yield_curve_item(id_yield_curve_item,yield_curve,ric,offset_type,offset_value,bid,ask) VALUES(nextval(('yield_curve_item_s'::text)::regclass),?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE yield_curve_item SET yield_curve=?,ric=?,offset_type=?,offset_value=?,bid=?,ask=? WHERE id_yield_curve_item=?";
    private final String removeExpr = "DELETE FROM yield_curve_item WHERE id_yield_curve_item_id=?";
    private final String selectByPKeyExpr = "SELECT id_yield_curve_item,yield_curve,ric,offset_type,offset_value,bid,ask FROM yield_curve_item WHERE id_yield_curve_item=?";
    private final String selectByIdxExpr = "SELECT id_yield_curve_item,yield_curve,ric,offset_type,offset_value,bid,ask FROM yield_curve_item WHERE ric=?";

    public Yield_curve_itemDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectByIdxStmt = cm.createPreparedStatement(selectByIdxExpr);
        }
    }

    public boolean insert(Yield_curve_item record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getYield_curve());
            insertStmt.setString(2, record.getRic());
            insertStmt.setInt(3, record.getOffset_type());
            insertStmt.setInt(4, record.getOffset_value());
            insertStmt.setDouble(5, record.getBid());
            insertStmt.setDouble(6, record.getAsk());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Yield_curve_item record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1, record.getYield_curve());
            updateStmt.setString(2, record.getRic());
            updateStmt.setInt(3, record.getOffset_type());
            updateStmt.setInt(4, record.getOffset_value());
            updateStmt.setDouble(5, record.getBid());
            updateStmt.setDouble(6, record.getAsk());
            updateStmt.setInt(7, record.getId_yield_curve_item());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Yield_curve_item record) {
        errorMsg = "";
        try {
            selectByIdxStmt.setString(1, record.getRic());
            ResultSet rs = selectByIdxStmt.executeQuery();
            if (rs.next()) {
                record.setId_yield_curve_item(rs.getInt("id_yield_curve_item"));
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
    
    public boolean remove(Yield_curve_item record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_yield_curve_item());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Yield_curve_item record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_yield_curve_item());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_yield_curve_item(rs.getInt("id_yield_curve_item"));
                    record.setYield_curve(rs.getInt("yield_curve"));
                    record.setRic(rs.getString("ric"));
                    record.setOffset_type(rs.getInt("offset_type"));
                    record.setOffset_value(rs.getInt("offset_value"));
                    record.setBid(rs.getDouble("bid"));
                    record.setAsk(rs.getDouble("ask"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
    public boolean loadByIdx(Yield_curve_item record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getRic());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_yield_curve_item(rs.getInt("id_yield_curve_item"));
                    record.setYield_curve(rs.getInt("yield_curve"));
                    record.setRic(rs.getString("ric"));
                    record.setOffset_type(rs.getInt("offset_type"));
                    record.setOffset_value(rs.getInt("offset_value"));
                    record.setBid(rs.getDouble("bid"));
                    record.setAsk(rs.getDouble("ask"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Yield_curve_item> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Yield_curve_item> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_yield_curve_item,yield_curve,ric,offset_type,offset_value,bid,ask FROM yield_curve_item " + whereExpr;
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
                    Yield_curve_item record = null;
                    while (rs.next()) {
                        record = new Yield_curve_item();
                        record.setId_yield_curve_item(rs.getInt("id_yield_curve_item"));
                        record.setYield_curve(rs.getInt("yield_curve"));
                        record.setRic(rs.getString("ric"));
                        record.setOffset_type(rs.getInt("offset_type"));
                        record.setOffset_value(rs.getInt("offset_value"));
                        record.setBid(rs.getDouble("bid"));
                        record.setAsk(rs.getDouble("ask"));
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
            selectByIdxStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
