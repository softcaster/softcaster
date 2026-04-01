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

public class CurrencyDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectByCodeStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO currency(id_currency,iso_code,currency_numeric_code,description,minor_unit,system_curr,physical_curr,calendar,daycount,business_days) VALUES(nextval(('currency_s'::text)::regclass),?,?,?,?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE currency SET iso_code=?,currency_numeric_code=?,description=?,minor_unit=?,system_curr=?,physical_curr=?,calendar=?,daycount=?,business_days=? WHERE id_currency=?";
    private final String removeExpr = "DELETE FROM currency WHERE id_currency_id=?";
    private final String selectExpr = "SELECT id_currency FROM currency WHERE currency_numeric_code=?";
    private final String selectByPKeyExpr = "SELECT id_currency,iso_code,currency_numeric_code,description,minor_unit,system_curr,physical_curr,calendar,daycount,business_days FROM currency WHERE id_currency=?";
    private final String selectByIdxExpr = "SELECT id_currency,iso_code,currency_numeric_code,description,minor_unit,system_curr,physical_curr,calendar,daycount,business_days FROM currency WHERE currency_numeric_code=?";

    private final String selectByCode = "SELECT id_currency,iso_code,currency_numeric_code,description,minor_unit,system_curr,physical_curr,calendar,daycount,business_days FROM currency WHERE iso_code=?";

    public CurrencyDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectByIdxStmt = cm.createPreparedStatement(selectByIdxExpr);
            selectByCodeStmt = cm.createPreparedStatement(selectByCode);
            selectStmt = cm.createPreparedStatement(selectExpr);
        }
    }

    public boolean insert(Currency record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getIso_code());
            insertStmt.setInt(2, record.getCurrency_numeric_code());
            insertStmt.setString(3, record.getDescription());
            insertStmt.setInt(4, record.getMinor_unit());
            insertStmt.setInt(5, record.getSystem_curr());
            insertStmt.setInt(6, record.getPhysical_curr());
            insertStmt.setInt(7, record.getCalendar());
            insertStmt.setInt(8, record.getDaycount());
            insertStmt.setInt(9, record.getBusiness_days());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Currency record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getIso_code());
            updateStmt.setInt(2, record.getCurrency_numeric_code());
            updateStmt.setString(3, record.getDescription());
            updateStmt.setInt(4, record.getMinor_unit());
            updateStmt.setInt(5, record.getSystem_curr());
            updateStmt.setInt(6, record.getPhysical_curr());
            updateStmt.setInt(7, record.getCalendar());
            updateStmt.setInt(8, record.getDaycount());
            updateStmt.setInt(9, record.getBusiness_days());
            updateStmt.setInt(10, record.getId_currency());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Currency record) {
        errorMsg = "";
        try {
            selectStmt.setInt(1, record.getCurrency_numeric_code());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_currency(rs.getInt("id_currency"));
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

    public boolean remove(Currency record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_currency());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Currency record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_currency());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_currency(rs.getInt("id_currency"));
                    record.setIso_code(rs.getString("iso_code"));
                    record.setCurrency_numeric_code(rs.getInt("currency_numeric_code"));
                    record.setDescription(rs.getString("description"));
                    record.setMinor_unit(rs.getInt("minor_unit"));
                    record.setSystem_curr(rs.getInt("system_curr"));
                    record.setPhysical_curr(rs.getInt("physical_curr"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setBusiness_days(rs.getInt("business_days"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Currency record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setInt(1, record.getCurrency_numeric_code());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_currency(rs.getInt("id_currency"));
                    record.setIso_code(rs.getString("iso_code"));
                    record.setCurrency_numeric_code(rs.getInt("currency_numeric_code"));
                    record.setDescription(rs.getString("description"));
                    record.setMinor_unit(rs.getInt("minor_unit"));
                    record.setSystem_curr(rs.getInt("system_curr"));
                    record.setPhysical_curr(rs.getInt("physical_curr"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setBusiness_days(rs.getInt("business_days"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIsoCode(Currency record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByCodeStmt.setString(1, record.getIso_code());
            try (ResultSet rs = selectByCodeStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_currency(rs.getInt("id_currency"));
                    record.setIso_code(rs.getString("iso_code"));
                    record.setCurrency_numeric_code(rs.getInt("currency_numeric_code"));
                    record.setDescription(rs.getString("description"));
                    record.setMinor_unit(rs.getInt("minor_unit"));
                    record.setSystem_curr(rs.getInt("system_curr"));
                    record.setPhysical_curr(rs.getInt("physical_curr"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setBusiness_days(rs.getInt("business_days"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Currency> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Currency> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_currency,iso_code,currency_numeric_code,description,minor_unit,system_curr,physical_curr FROM currency " + whereExpr;
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
                                case DATA_TYPE.TEXT -> selectByWhereStmt.setString(counter, param.getStringValue());
                                case DATA_TYPE.INTEGER -> selectByWhereStmt.setInt(counter, param.getIntegerValue());
                                case DATA_TYPE.LONG -> selectByWhereStmt.setLong(counter, param.getLongValue());
                                case DATA_TYPE.REAL -> selectByWhereStmt.setDouble(counter, param.getDoubleValue());
                                case DATA_TYPE.DATE -> selectByWhereStmt.setDate(counter, param.getDateValue());
                                default -> {
                                }
                            }
                        }
                        counter++;
                    }
                }
                try (ResultSet rs = selectByWhereStmt.executeQuery()) {
                    Currency record = null;
                    while (rs.next()) {
                        record = new Currency();
                        record.setId_currency(rs.getInt("id_currency"));
                        record.setIso_code(rs.getString("iso_code"));
                        record.setCurrency_numeric_code(rs.getInt("currency_numeric_code"));
                        record.setDescription(rs.getString("description"));
                        record.setMinor_unit(rs.getInt("minor_unit"));
                        record.setSystem_curr(rs.getInt("system_curr"));
                        record.setPhysical_curr(rs.getInt("physical_curr"));
                        record.setCalendar(rs.getInt("calendar"));
                        record.setDaycount(rs.getInt("daycount"));
                        record.setBusiness_days(rs.getInt("business_days"));
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
            selectByCodeStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
