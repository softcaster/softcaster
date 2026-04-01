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

public class CountryDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectByAlfa3CodeStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO country(id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,sovereign,subdivision_code_links,internet_cc_tld,currency,calendar) VALUES(nextval(('country_s'::text)::regclass),?,?,?,?,?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE country SET country_name=?,official_state_name=?,alfa_2_code=?,alfa_3_code=?,country_numeric_code=?,sovereign=?,subdivision_code_links=?,internet_cc_tld=?,currency=?,calendar=? WHERE id_country=?";
    private final String removeExpr = "DELETE FROM country WHERE id_country_id=?";
    private final String selectExpr = "SELECT id_country FROM country WHERE alfa_2_code=?";
    private final String selectByPKeyExpr = "SELECT id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,sovereign,subdivision_code_links,internet_cc_tld,currency,calendar FROM country WHERE id_country=?";
    private final String selectByIdxExpr = "SELECT id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,sovereign,subdivision_code_links,internet_cc_tld,currency,calendar FROM country WHERE alfa_2_code=?";
    private final String selectByAlfa3Code = "SELECT id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,sovereign,subdivision_code_links,internet_cc_tld,currency,calendar FROM country WHERE alfa_3_code=?";

    public CountryDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectByIdxStmt = cm.createPreparedStatement(selectByIdxExpr);
            selectByAlfa3CodeStmt = cm.createPreparedStatement(selectByAlfa3Code);
            selectStmt = cm.createPreparedStatement(selectExpr);
        }
    }

    public boolean insert(Country record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getCountry_name());
            insertStmt.setString(2, record.getOfficial_state_name());
            insertStmt.setString(3, record.getAlfa_2_code());
            insertStmt.setString(4, record.getAlfa_3_code());
            insertStmt.setInt(5, record.getCountry_numeric_code());
            insertStmt.setString(6, record.getSovereign());
            insertStmt.setString(7, record.getSubdivision_code_links());
            insertStmt.setString(8, record.getInternet_cc_tld());
            insertStmt.setInt(9, record.getCurrency());
            insertStmt.setInt(10, record.getCalendar());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError("Country: " + record.getCountry_name() + " " + errorMsg);
            return false;
        }
    }

    public boolean update(Country record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getCountry_name());
            updateStmt.setString(2, record.getOfficial_state_name());
            updateStmt.setString(3, record.getAlfa_2_code());
            updateStmt.setString(4, record.getAlfa_3_code());
            updateStmt.setInt(5, record.getCountry_numeric_code());
            updateStmt.setString(6, record.getSovereign());
            updateStmt.setString(7, record.getSubdivision_code_links());
            updateStmt.setString(8, record.getInternet_cc_tld());
            updateStmt.setInt(9, record.getCurrency());
            updateStmt.setInt(10, record.getCalendar());
            updateStmt.setInt(11, record.getId_country());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError("Country: " + record.getCountry_name() + " " + errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Country record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getAlfa_2_code());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_country(rs.getInt("id_country"));
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

    public boolean remove(Country record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_country());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Country record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_country());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_country(rs.getInt("id_country"));
                    record.setCountry_name(rs.getString("country_name"));
                    record.setOfficial_state_name(rs.getString("official_state_name"));
                    record.setAlfa_2_code(rs.getString("alfa_2_code"));
                    record.setAlfa_3_code(rs.getString("alfa_3_code"));
                    record.setCountry_numeric_code(rs.getInt("country_numeric_code"));
                    record.setSovereign(rs.getString("sovereign"));
                    record.setSubdivision_code_links(rs.getString("subdivision_code_links"));
                    record.setInternet_cc_tld(rs.getString("internet_cc_tld"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByAlfa3Code(Country record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByAlfa3CodeStmt.setString(1, record.getAlfa_3_code());
            try (ResultSet rs = selectByAlfa3CodeStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_country(rs.getInt("id_country"));
                    record.setCountry_name(rs.getString("country_name"));
                    record.setOfficial_state_name(rs.getString("official_state_name"));
                    record.setAlfa_2_code(rs.getString("alfa_2_code"));
                    record.setAlfa_3_code(rs.getString("alfa_3_code"));
                    record.setCountry_numeric_code(rs.getInt("country_numeric_code"));
                    record.setSovereign(rs.getString("sovereign"));
                    record.setSubdivision_code_links(rs.getString("subdivision_code_links"));
                    record.setInternet_cc_tld(rs.getString("internet_cc_tld"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Country record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getAlfa_2_code());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_country(rs.getInt("id_country"));
                    record.setCountry_name(rs.getString("country_name"));
                    record.setOfficial_state_name(rs.getString("official_state_name"));
                    record.setAlfa_2_code(rs.getString("alfa_2_code"));
                    record.setAlfa_3_code(rs.getString("alfa_3_code"));
                    record.setCountry_numeric_code(rs.getInt("country_numeric_code"));
                    record.setSovereign(rs.getString("sovereign"));
                    record.setSubdivision_code_links(rs.getString("subdivision_code_links"));
                    record.setInternet_cc_tld(rs.getString("internet_cc_tld"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Country> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Country> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_country,country_name,official_state_name,alfa_2_code,alfa_3_code,country_numeric_code,sovereign,subdivision_code_links,internet_cc_tld,currency,calendar FROM country " + whereExpr;
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
                                case DATA_TYPE.TEXT:
                                    selectByWhereStmt.setString(counter, param.getStringValue());
                                    break;
                                case DATA_TYPE.INTEGER:
                                    selectByWhereStmt.setInt(counter, param.getIntegerValue());
                                    break;
                                case DATA_TYPE.LONG:
                                    selectByWhereStmt.setLong(counter, param.getLongValue());
                                    break;
                                case DATA_TYPE.REAL:
                                    selectByWhereStmt.setDouble(counter, param.getDoubleValue());
                                    break;
                                case DATA_TYPE.DATE:
                                    selectByWhereStmt.setDate(counter, param.getDateValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        counter++;
                    }
                }
                try (ResultSet rs = selectByWhereStmt.executeQuery()) {
                    Country record = null;
                    while (rs.next()) {
                        record = new Country();
                        record.setId_country(rs.getInt("id_country"));
                        record.setCountry_name(rs.getString("country_name"));
                        record.setOfficial_state_name(rs.getString("official_state_name"));
                        record.setAlfa_2_code(rs.getString("alfa_2_code"));
                        record.setAlfa_3_code(rs.getString("alfa_3_code"));
                        record.setCountry_numeric_code(rs.getInt("country_numeric_code"));
                        record.setSovereign(rs.getString("sovereign"));
                        record.setSubdivision_code_links(rs.getString("subdivision_code_links"));
                        record.setInternet_cc_tld(rs.getString("internet_cc_tld"));
                        record.setCurrency(rs.getInt("currency"));
                        record.setCalendar(rs.getInt("calendar"));
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
            selectByAlfa3CodeStmt.close();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
