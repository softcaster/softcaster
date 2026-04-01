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
import org.softcaster.easy_pricer.Currency_pair;

public class Currency_pairDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO currency_pair(id_currency_pair,code,bcy,ccy,bid,ask) VALUES(nextval(('currency_pair_s'::text)::regclass),?,?,?,?,?)";
    private final String updateExpr = "UPDATE currency_pair SET code=?,bcy=?,ccy=?,bid=?,ask=? WHERE id_currency_pair=?";
    private final String removeExpr = "DELETE FROM currency_pair WHERE id_currency_pair_id=?";
    private final String selectExpr = "SELECT id_currency_pair FROM currency_pair WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_currency_pair,code,bcy,ccy,bid,ask FROM currency_pair WHERE id_currency_pair=?";
    private final String selectByIdxExpr = "SELECT id_currency_pair,code,bcy,ccy,bid,ask FROM currency_pair WHERE code=?";

    public Currency_pairDAO() {
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

    public boolean insert(Currency_pair record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getCode());
            insertStmt.setInt(2, record.getBcy());
            insertStmt.setInt(3, record.getCcy());
            insertStmt.setDouble(4, record.getBid());
            insertStmt.setDouble(5, record.getAsk());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Currency_pair record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getCode());
            updateStmt.setInt(2, record.getBcy());
            updateStmt.setInt(3, record.getCcy());
            updateStmt.setDouble(4, record.getBid());
            updateStmt.setDouble(5, record.getAsk());
            updateStmt.setInt(6, record.getId_currency_pair());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Currency_pair record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_currency_pair(rs.getInt("id_currency_pair"));
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

    public boolean remove(Currency_pair record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_currency_pair());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Currency_pair record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_currency_pair());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_currency_pair(rs.getInt("id_currency_pair"));
                    record.setCode(rs.getString("code"));
                    record.setBcy(rs.getInt("bcy"));
                    record.setCcy(rs.getInt("ccy"));
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

    public boolean loadByIdx(Currency_pair record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_currency_pair(rs.getInt("id_currency_pair"));
                    record.setCode(rs.getString("code"));
                    record.setBcy(rs.getInt("bcy"));
                    record.setCcy(rs.getInt("ccy"));
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

    public List<Currency_pair> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Currency_pair> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_currency_pair,code,bcy,ccy,bid,ask FROM currency_pair " + whereExpr;
            ConnectioManager cm = ConnectioManager.getInstance();
            try (PreparedStatement selectByWhereStmt = cm.createPreparedStatement(selectByWhereExpr)) {
                if (params != null && !params.isEmpty()) {
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
                    Currency_pair record = null;
                    while (rs.next()) {
                        record = new Currency_pair();
                        record.setId_currency_pair(rs.getInt("id_currency_pair"));
                        record.setCode(rs.getString("code"));
                        record.setBcy(rs.getInt("bcy"));
                        record.setCcy(rs.getInt("ccy"));
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
