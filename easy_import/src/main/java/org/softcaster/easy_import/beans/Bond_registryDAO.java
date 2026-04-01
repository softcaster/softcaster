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

public class Bond_registryDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO bond_registry(id_bond_registry,isin,cfi_code,fisn,lei,issuer,currency,calendar,issue_date,maturity_date,type_of_interest,form,daycount,coupon_frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,nominal_value,first_coupon_rate,first_coupon_payment_date,business_days,issue_description) VALUES(nextval(('bond_registry_s'::text)::regclass),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE bond_registry SET isin=?,cfi_code=?,fisn=?,lei=?,issuer=?,currency=?,calendar=?,issue_date=?,maturity_date=?,type_of_interest=?,form=?,daycount=?,coupon_frequency=?,roll_convention=?,accrual_schedule_type=?,interest_rate=?,issue_price=?,redempion_price=?,nominal_value=?,first_coupon_rate=?,first_coupon_payment_date=?,business_days=?,issue_description=? WHERE id_bond_registry=?";
    private final String removeExpr = "DELETE FROM bond_registry WHERE id_bond_registry_id=?";
    private final String selectExpr = "SELECT id_bond_registry FROM bond_registry WHERE isin=?";
    private final String selectByPKeyExpr = "SELECT id_bond_registry,isin,cfi_code,fisn,lei,issuer,currency,calendar,issue_date,maturity_date,type_of_interest,form,daycount,coupon_frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,nominal_value,first_coupon_rate,first_coupon_payment_date,business_days,issue_description FROM bond_registry WHERE id_bond_registry=?";
    private final String selectByIdxExpr = "SELECT id_bond_registry,isin,cfi_code,fisn,lei,issuer,currency,calendar,issue_date,maturity_date,type_of_interest,form,daycount,coupon_frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,nominal_value,first_coupon_rate,first_coupon_payment_date,business_days,issue_description FROM bond_registry WHERE isin=?";

    public Bond_registryDAO() {
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

    public boolean insert(Bond_registry record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getIsin());
            insertStmt.setString(2, record.getCfi_code());
            insertStmt.setString(3, record.getFisn());
            insertStmt.setString(4, record.getLei());
            insertStmt.setInt(5, record.getIssuer());
            insertStmt.setInt(6, record.getCurrency());
            insertStmt.setInt(7, record.getCalendar());
            insertStmt.setDate(8, record.getIssue_date());
            insertStmt.setDate(9, record.getMaturity_date());
            insertStmt.setInt(10, record.getType_of_interest());
            insertStmt.setInt(11, record.getForm());
            insertStmt.setInt(12, record.getDaycount());
            insertStmt.setInt(13, record.getCoupon_frequency());
            insertStmt.setInt(14, record.getRoll_convention());
            insertStmt.setInt(15, record.getAccrual_schedule_type());
            insertStmt.setDouble(16, record.getInterest_rate());
            insertStmt.setDouble(17, record.getIssue_price());
            insertStmt.setDouble(18, record.getRedempion_price());
            insertStmt.setDouble(19, record.getNominal_value());
            insertStmt.setDouble(20, record.getFirst_coupon_rate());
            insertStmt.setDate(21, record.getFirst_coupon_payment_date());
            insertStmt.setInt(22, record.getBusiness_days());
            insertStmt.setString(23, record.getIssue_description());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Bond_registry record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getIsin());
            updateStmt.setString(2, record.getCfi_code());
            updateStmt.setString(3, record.getFisn());
            updateStmt.setString(4, record.getLei());
            updateStmt.setInt(5, record.getIssuer());
            updateStmt.setInt(6, record.getCurrency());
            updateStmt.setInt(7, record.getCalendar());
            updateStmt.setDate(8, record.getIssue_date());
            updateStmt.setDate(9, record.getMaturity_date());
            updateStmt.setInt(10, record.getType_of_interest());
            updateStmt.setInt(11, record.getForm());
            updateStmt.setInt(12, record.getDaycount());
            updateStmt.setInt(13, record.getCoupon_frequency());
            updateStmt.setInt(14, record.getRoll_convention());
            updateStmt.setInt(15, record.getAccrual_schedule_type());
            updateStmt.setDouble(16, record.getInterest_rate());
            updateStmt.setDouble(17, record.getIssue_price());
            updateStmt.setDouble(18, record.getRedempion_price());
            updateStmt.setDouble(19, record.getNominal_value());
            updateStmt.setDouble(20, record.getFirst_coupon_rate());
            updateStmt.setDate(21, record.getFirst_coupon_payment_date());
            updateStmt.setInt(22, record.getBusiness_days());
            updateStmt.setString(23, record.getIssue_description());
            updateStmt.setInt(24, record.getId_bond_registry());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Bond_registry record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getIsin());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_bond_registry(rs.getInt("id_bond_registry"));
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

    public boolean remove(Bond_registry record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_bond_registry());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Bond_registry record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_bond_registry());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_bond_registry(rs.getInt("id_bond_registry"));
                    record.setIsin(rs.getString("isin"));
                    record.setCfi_code(rs.getString("cfi_code"));
                    record.setFisn(rs.getString("fisn"));
                    record.setLei(rs.getString("lei"));
                    record.setIssuer(rs.getInt("issuer"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setIssue_date(rs.getDate("issue_date"));
                    record.setMaturity_date(rs.getDate("maturity_date"));
                    record.setType_of_interest(rs.getInt("type_of_interest"));
                    record.setForm(rs.getInt("form"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setCoupon_frequency(rs.getInt("coupon_frequency"));
                    record.setRoll_convention(rs.getInt("roll_convention"));
                    record.setAccrual_schedule_type(rs.getInt("accrual_schedule_type"));
                    record.setInterest_rate(rs.getDouble("interest_rate"));
                    record.setIssue_price(rs.getDouble("issue_price"));
                    record.setRedempion_price(rs.getDouble("redempion_price"));
                    record.setNominal_value(rs.getDouble("nominal_value"));
                    record.setFirst_coupon_rate(rs.getDouble("first_coupon_rate"));
                    record.setFirst_coupon_payment_date(rs.getDate("first_coupon_payment_date"));
                    record.setBusiness_days(rs.getInt("business_days"));
                    record.setIssue_description(rs.getString("issue_description"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Bond_registry record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getIsin());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_bond_registry(rs.getInt("id_bond_registry"));
                    record.setIsin(rs.getString("isin"));
                    record.setCfi_code(rs.getString("cfi_code"));
                    record.setFisn(rs.getString("fisn"));
                    record.setLei(rs.getString("lei"));
                    record.setIssuer(rs.getInt("issuer"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setCalendar(rs.getInt("calendar"));
                    record.setIssue_date(rs.getDate("issue_date"));
                    record.setMaturity_date(rs.getDate("maturity_date"));
                    record.setType_of_interest(rs.getInt("type_of_interest"));
                    record.setForm(rs.getInt("form"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setCoupon_frequency(rs.getInt("coupon_frequency"));
                    record.setRoll_convention(rs.getInt("roll_convention"));
                    record.setAccrual_schedule_type(rs.getInt("accrual_schedule_type"));
                    record.setInterest_rate(rs.getDouble("interest_rate"));
                    record.setIssue_price(rs.getDouble("issue_price"));
                    record.setRedempion_price(rs.getDouble("redempion_price"));
                    record.setNominal_value(rs.getDouble("nominal_value"));
                    record.setFirst_coupon_rate(rs.getDouble("first_coupon_rate"));
                    record.setFirst_coupon_payment_date(rs.getDate("first_coupon_payment_date"));
                    record.setBusiness_days(rs.getInt("business_days"));
                    record.setIssue_description(rs.getString("issue_description"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Bond_registry> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Bond_registry> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_bond_registry,isin,cfi_code,fisn,lei,issuer,currency,calendar,issue_date,maturity_date,type_of_interest,form,daycount,coupon_frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,nominal_value,first_coupon_rate,first_coupon_payment_date,business_days,issue_description FROM bond_registry " + whereExpr;
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
                    Bond_registry record = null;
                    while (rs.next()) {
                        record = new Bond_registry();
                        record.setId_bond_registry(rs.getInt("id_bond_registry"));
                        record.setIsin(rs.getString("isin"));
                        record.setCfi_code(rs.getString("cfi_code"));
                        record.setFisn(rs.getString("fisn"));
                        record.setLei(rs.getString("lei"));
                        record.setIssuer(rs.getInt("issuer"));
                        record.setCurrency(rs.getInt("currency"));
                        record.setCalendar(rs.getInt("calendar"));
                        record.setIssue_date(rs.getDate("issue_date"));
                        record.setMaturity_date(rs.getDate("maturity_date"));
                        record.setType_of_interest(rs.getInt("type_of_interest"));
                        record.setForm(rs.getInt("form"));
                        record.setDaycount(rs.getInt("daycount"));
                        record.setCoupon_frequency(rs.getInt("coupon_frequency"));
                        record.setRoll_convention(rs.getInt("roll_convention"));
                        record.setAccrual_schedule_type(rs.getInt("accrual_schedule_type"));
                        record.setInterest_rate(rs.getDouble("interest_rate"));
                        record.setIssue_price(rs.getDouble("issue_price"));
                        record.setRedempion_price(rs.getDouble("redempion_price"));
                        record.setNominal_value(rs.getDouble("nominal_value"));
                        record.setFirst_coupon_rate(rs.getDouble("first_coupon_rate"));
                        record.setFirst_coupon_payment_date(rs.getDate("first_coupon_payment_date"));
                        record.setBusiness_days(rs.getInt("business_days"));
                        record.setIssue_description(rs.getString("issue_description"));
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
