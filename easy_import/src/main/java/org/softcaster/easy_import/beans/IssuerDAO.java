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

public class IssuerDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByCodeStmt = null;

    private final String insertExpr = "INSERT INTO issuer(id_issuer,long_issuer_name,country,short_issuer_name) VALUES(nextval(('issuer_s'::text)::regclass),?,?,?)";
    private final String updateExpr = "UPDATE issuer SET long_issuer_name=?,country=?,short_issuer_name=? WHERE id_issuer=?";
    private final String removeExpr = "DELETE FROM issuer WHERE id_issuer_id=?";
    private final String selectByPKeyExpr = "SELECT id_issuer,long_issuer_name,country,short_issuer_name FROM issuer WHERE id_issuer=?";
    private final String selectByCode = "SELECT id_issuer,long_issuer_name,country,short_issuer_name FROM issuer WHERE short_issuer_name=?";

    public IssuerDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectByCodeStmt = cm.createPreparedStatement(selectByCode);
        }
    }

    public boolean insert(Issuer record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getLong_issuer_name());
            insertStmt.setInt(2, record.getCountry());
            insertStmt.setString(3, record.getShort_issuer_name());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Issuer record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getLong_issuer_name());
            updateStmt.setInt(2, record.getCountry());
            updateStmt.setString(3, record.getShort_issuer_name());
            updateStmt.setInt(4, record.getId_issuer());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean remove(Issuer record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_issuer());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Issuer record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_issuer());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_issuer(rs.getInt("id_issuer"));
                    record.setLong_issuer_name(rs.getString("long_issuer_name"));
                    record.setCountry(rs.getInt("country"));
                    record.setShort_issuer_name(rs.getString("short_issuer_name"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByCode(Issuer record) {
        errorMsg = "";
        try {
            boolean found=false;
            selectByCodeStmt.setString(1,record.getShort_issuer_name());
            try (ResultSet rs = selectByCodeStmt.executeQuery()) {
                if (rs.next()) {
                    found=true;
                    record.setId_issuer(rs.getInt("id_issuer"));
                    record.setLong_issuer_name(rs.getString("long_issuer_name"));
                    record.setCountry(rs.getInt("country"));
                    record.setShort_issuer_name(rs.getString("short_issuer_name"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
    
    public List<Issuer> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Issuer> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_issuer,long_issuer_name,country,short_issuer_name FROM issuer " + whereExpr;
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
                    Issuer record = null;
                    while (rs.next()) {
                        record = new Issuer();
                        record.setId_issuer(rs.getInt("id_issuer"));
                        record.setLong_issuer_name(rs.getString("long_issuer_name"));
                        record.setCountry(rs.getInt("country"));
                        record.setShort_issuer_name(rs.getString("short_issuer_name"));
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
            selectByCodeStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
