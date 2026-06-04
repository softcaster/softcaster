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

public class Security_master_dataDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO security_master_data(id_master_data,isin,cfi_code,fisn,lei,issuer,nominal_value,first_coupon_rate,first_coupon_payment_date) VALUES(?,?,?,?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE security_master_data SET isin=?,cfi_code=?,fisn=?,lei=?,issuer=?,nominal_value=?,first_coupon_rate=?,first_coupon_payment_date=? WHERE id_master_data=?";
    private final String selectExpr = "SELECT id_master_data FROM security_master_data WHERE isin=?";
    private final String removeExpr = "DELETE FROM security_master_data WHERE id_master_data_id=?";
    private final String selectByPKeyExpr = "SELECT id_master_data,isin,cfi_code,fisn,lei,issuer,nominal_value,first_coupon_rate,first_coupon_payment_date FROM security_master_data WHERE id_master_data=?";
    private final String selectByIdxExpr = "SELECT id_master_data,isin,cfi_code,fisn,lei,issuer,nominal_value,first_coupon_rate,first_coupon_payment_date FROM security_master_data WHERE isin=?";

    public Security_master_dataDAO() {
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

    public boolean insertOrUpdate(Security_master_data record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getIsin());
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
    
    public boolean insert(Security_master_data record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getId_master_data());
            insertStmt.setString(2, record.getIsin());
            insertStmt.setString(3, record.getCfi_code());
            insertStmt.setString(4, record.getFisn());
            insertStmt.setString(5, record.getLei());
            insertStmt.setInt(6, record.getIssuer());
            insertStmt.setDouble(7, record.getNominal_value());
            insertStmt.setDouble(8, record.getFirst_coupon_rate());
            insertStmt.setDate(9, record.getFirst_coupon_payment_date());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Security_master_data record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getIsin());
            updateStmt.setString(2, record.getCfi_code());
            updateStmt.setString(3, record.getFisn());
            updateStmt.setString(4, record.getLei());
            updateStmt.setInt(5, record.getIssuer());
            updateStmt.setDouble(6, record.getNominal_value());
            updateStmt.setDouble(7, record.getFirst_coupon_rate());
            updateStmt.setDate(8, record.getFirst_coupon_payment_date());
            updateStmt.setInt(9, record.getId_master_data());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean remove(Security_master_data record) {
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

    public boolean loadByPKey(Security_master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_master_data());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setIsin(rs.getString("isin"));
                    record.setCfi_code(rs.getString("cfi_code"));
                    record.setFisn(rs.getString("fisn"));
                    record.setLei(rs.getString("lei"));
                    record.setIssuer(rs.getInt("issuer"));
                    record.setNominal_value(rs.getDouble("nominal_value"));
                    record.setFirst_coupon_rate(rs.getDouble("first_coupon_rate"));
                    record.setFirst_coupon_payment_date(rs.getDate("first_coupon_payment_date"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
    
    public boolean loadByIdx(Security_master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getIsin());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setIsin(rs.getString("isin"));
                    record.setCfi_code(rs.getString("cfi_code"));
                    record.setFisn(rs.getString("fisn"));
                    record.setLei(rs.getString("lei"));
                    record.setIssuer(rs.getInt("issuer"));
                    record.setNominal_value(rs.getDouble("nominal_value"));
                    record.setFirst_coupon_rate(rs.getDouble("first_coupon_rate"));
                    record.setFirst_coupon_payment_date(rs.getDate("first_coupon_payment_date"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Security_master_data> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Security_master_data> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_master_data,isin,cfi_code,fisn,lei,issuer,nominal_value,first_coupon_rate,first_coupon_payment_date FROM security_master_data " + whereExpr;
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
                    Security_master_data record = null;
                    while (rs.next()) {
                        record = new Security_master_data();
                        record.setId_master_data(rs.getInt("id_master_data"));
                        record.setIsin(rs.getString("isin"));
                        record.setCfi_code(rs.getString("cfi_code"));
                        record.setFisn(rs.getString("fisn"));
                        record.setLei(rs.getString("lei"));
                        record.setIssuer(rs.getInt("issuer"));
                        record.setNominal_value(rs.getDouble("nominal_value"));
                        record.setFirst_coupon_rate(rs.getDouble("first_coupon_rate"));
                        record.setFirst_coupon_payment_date(rs.getDate("first_coupon_payment_date"));
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
            selectStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
