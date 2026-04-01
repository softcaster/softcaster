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

public class Deliverable_bondsDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO deliverable_bonds(id_deliverable_bonds,master_data,expiration_date,isin,coupon_rate,bond_maturity,bond_cf) VALUES(nextval(('deliverable_bonds_s'::text)::regclass),?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE deliverable_bonds SET master_data=?,expiration_date=?,isin=?,coupon_rate=?,bond_maturity=?,bond_cf=? WHERE id_deliverable_bonds=?";
    private final String removeExpr = "DELETE FROM deliverable_bonds WHERE id_deliverable_bonds_id=?";
    private final String selectByPKeyExpr = "SELECT id_deliverable_bonds,master_data,expiration_date,isin,coupon_rate,bond_maturity,bond_cf FROM deliverable_bonds WHERE id_deliverable_bonds=?";
    private final String selectExpr = "SELECT id_deliverable_bonds FROM deliverable_bonds WHERE master_data=? AND isin=?";

    public Deliverable_bondsDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectStmt = cm.createPreparedStatement(selectExpr);
        }
    }

    public boolean insert(Deliverable_bonds record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getMaster_data());
            insertStmt.setDate(2, record.getExpiration_date());
            insertStmt.setString(3, record.getIsin());
            insertStmt.setDouble(4, record.getCoupon_rate());
            insertStmt.setDate(5, record.getBond_maturity());
            insertStmt.setDouble(6, record.getBond_cf());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Deliverable_bonds record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1, record.getMaster_data());
            updateStmt.setDate(2, record.getExpiration_date());
            updateStmt.setString(3, record.getIsin());
            updateStmt.setDouble(4, record.getCoupon_rate());
            updateStmt.setDate(5, record.getBond_maturity());
            updateStmt.setDouble(6, record.getBond_cf());
            updateStmt.setInt(7, record.getId_deliverable_bonds());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Deliverable_bonds record) {
        errorMsg = "";
        try {
            selectStmt.setInt(1,record.getMaster_data());
            selectStmt.setString(2,record.getIsin());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_deliverable_bonds(rs.getInt("id_deliverable_bonds"));
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
    
    public boolean remove(Deliverable_bonds record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_deliverable_bonds());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Deliverable_bonds record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_deliverable_bonds());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_deliverable_bonds(rs.getInt("id_deliverable_bonds"));
                    record.setMaster_data(rs.getInt("master_data"));
                    record.setExpiration_date(rs.getDate("expiration_date"));
                    record.setIsin(rs.getString("isin"));
                    record.setCoupon_rate(rs.getDouble("coupon_rate"));
                    record.setBond_maturity(rs.getDate("bond_maturity"));
                    record.setBond_cf(rs.getDouble("bond_cf"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Deliverable_bonds> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Deliverable_bonds> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_deliverable_bonds,master_data,expiration_date,isin,coupon_rate,bond_maturity,bond_cf FROM deliverable_bonds " + whereExpr;
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
                    Deliverable_bonds record = null;
                    while (rs.next()) {
                        record = new Deliverable_bonds();
                        record.setId_deliverable_bonds(rs.getInt("id_deliverable_bonds"));
                        record.setMaster_data(rs.getInt("master_data"));
                        record.setExpiration_date(rs.getDate("expiration_date"));
                        record.setIsin(rs.getString("isin"));
                        record.setCoupon_rate(rs.getDouble("coupon_rate"));
                        record.setBond_maturity(rs.getDate("bond_maturity"));
                        record.setBond_cf(rs.getDouble("bond_cf"));
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
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
