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

public class Instrument_quoteDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO instrument_quote(id_instrument_quote,master_data,code,bid,ask) VALUES(nextval(('instrument_quote_s'::text)::regclass),?,?,?,?)";
    private final String updateExpr = "UPDATE instrument_quote SET master_data=?,code=?,bid=?,ask=? WHERE id_instrument_quote=?";
    private final String removeExpr = "DELETE FROM instrument_quote WHERE id_instrument_quote_id=?";
    private final String selectExpr = "SELECT id_instrument_quote FROM instrument_quote WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_instrument_quote,master_data,code,bid,ask FROM instrument_quote WHERE id_instrument_quote=?";
    private final String selectByIdxExpr = "SELECT id_instrument_quote,master_data,code,bid,ask FROM instrument_quote WHERE code=?";

    public Instrument_quoteDAO() {
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

    public boolean insert(Instrument_quote record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getMaster_data());
            insertStmt.setString(2, record.getCode());
            insertStmt.setDouble(3, record.getBid());
            insertStmt.setDouble(4, record.getAsk());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Instrument_quote record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1, record.getMaster_data());
            updateStmt.setString(2, record.getCode());
            updateStmt.setDouble(3, record.getBid());
            updateStmt.setDouble(4, record.getAsk());
            updateStmt.setInt(5, record.getId_instrument_quote());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Instrument_quote record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_instrument_quote(rs.getInt("id_instrument_quote"));
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

    public boolean remove(Instrument_quote record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_instrument_quote());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Instrument_quote record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_instrument_quote());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_instrument_quote(rs.getInt("id_instrument_quote"));
                    record.setMaster_data(rs.getInt("master_data"));
                    record.setCode(rs.getString("code"));
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

    public boolean loadByIdx(Instrument_quote record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_instrument_quote(rs.getInt("id_instrument_quote"));
                    record.setMaster_data(rs.getInt("master_data"));
                    record.setCode(rs.getString("code"));
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

    public List<Instrument_quote> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Instrument_quote> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_instrument_quote,master_data,code,bid,ask FROM instrument_quote " + whereExpr;
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
                    Instrument_quote record = null;
                    while (rs.next()) {
                        record = new Instrument_quote();
                        record.setId_instrument_quote(rs.getInt("id_instrument_quote"));
                        record.setMaster_data(rs.getInt("master_data"));
                        record.setCode(rs.getString("code"));
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
