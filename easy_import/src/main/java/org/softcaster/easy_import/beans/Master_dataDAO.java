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

public class Master_dataDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;

    private final String insertExpr = "INSERT INTO master_data(id_master_data,code,currency,issue_date,maturity_date,type_of_interest,form,daycount,frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,business_days,asset_class,amortization_schedule,description,accrual_daycount,multiplier) VALUES(nextval(('master_data_s'::text)::regclass),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final String updateExpr = "UPDATE master_data SET code=?,currency=?,issue_date=?,maturity_date=?,type_of_interest=?,form=?,daycount=?,frequency=?,roll_convention=?,accrual_schedule_type=?,interest_rate=?,issue_price=?,redempion_price=?,business_days=?,asset_class=?,amortization_schedule=?,description=?,accrual_daycount=?,multiplier=? WHERE id_master_data=?";
    private final String removeExpr = "DELETE FROM master_data WHERE id_master_data_id=?";
    private final String selectByPKeyExpr = "SELECT id_master_data,code,currency,issue_date,maturity_date,type_of_interest,form,daycount,frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,business_days,asset_class,amortization_schedule,description,accrual_daycount,multiplier FROM master_data WHERE id_master_data=?";
    private final String selectByIdxExpr = "SELECT id_master_data,code,currency,issue_date,maturity_date,type_of_interest,form,daycount,frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,business_days,asset_class,amortization_schedule,description,accrual_daycount,multiplier FROM master_data WHERE code=?";

    public Master_dataDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectByIdxStmt = cm.createPreparedStatement(selectByIdxExpr);
        }
    }

    public boolean insertOrUpdate(Master_data record) {
        errorMsg = "";
        try {
            selectByIdxStmt.setString(1, record.getCode());
            ResultSet rs = selectByIdxStmt.executeQuery();
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

    public boolean insert(Master_data record) {
        errorMsg = "";
        try {
            insertStmt.setString(1, record.getCode());
            insertStmt.setInt(2, record.getCurrency());
            insertStmt.setDate(3, record.getIssue_date());
            insertStmt.setDate(4, record.getMaturity_date());
            insertStmt.setInt(5, record.getType_of_interest());
            insertStmt.setInt(6, record.getForm());
            insertStmt.setInt(7, record.getDaycount());
            insertStmt.setInt(8, record.getFrequency());
            insertStmt.setInt(9, record.getRoll_convention());
            insertStmt.setInt(10, record.getAccrual_schedule_type());
            insertStmt.setDouble(11, record.getInterest_rate());
            insertStmt.setDouble(12, record.getIssue_price());
            insertStmt.setDouble(13, record.getRedempion_price());
            insertStmt.setInt(14, record.getBusiness_days());
            insertStmt.setInt(15, record.getAsset_class());
            insertStmt.setInt(16, record.getAmortization_schedule());
            insertStmt.setString(17, record.getDescription());
            insertStmt.setInt(18, record.getAccrual_daycount());
            insertStmt.setDouble(19, record.getMultiplier());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Master_data record) {
        errorMsg = "";
        try {
            updateStmt.setString(1,record.getCode());
            updateStmt.setInt(2,record.getCurrency());
            updateStmt.setDate(3,record.getIssue_date());
            updateStmt.setDate(4,record.getMaturity_date());
             updateStmt.setInt(5,record.getType_of_interest());
            updateStmt.setInt(6,record.getForm());
            updateStmt.setInt(7,record.getDaycount());
            updateStmt.setInt(8,record.getFrequency());
            updateStmt.setInt(9,record.getRoll_convention());
            updateStmt.setInt(10,record.getAccrual_schedule_type());
            updateStmt.setDouble(11,record.getInterest_rate());
            updateStmt.setDouble(12,record.getIssue_price());
            updateStmt.setDouble(13,record.getRedempion_price());
            updateStmt.setInt(14,record.getBusiness_days());
            updateStmt.setInt(15,record.getAsset_class());
            updateStmt.setInt(16,record.getAmortization_schedule());
            updateStmt.setInt(17,record.getId_master_data());
            insertStmt.setString(18, record.getDescription());
            insertStmt.setInt(10, record.getAccrual_daycount());
            insertStmt.setDouble(20, record.getMultiplier());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean remove(Master_data record) {
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

    public boolean loadByPKey(Master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_master_data());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setCode(rs.getString("code"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setIssue_date(rs.getDate("issue_date"));
                    record.setMaturity_date(rs.getDate("maturity_date"));
                    record.setType_of_interest(rs.getInt("type_of_interest"));
                    record.setForm(rs.getInt("form"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setFrequency(rs.getInt("frequency"));
                    record.setRoll_convention(rs.getInt("roll_convention"));
                    record.setAccrual_schedule_type(rs.getInt("accrual_schedule_type"));
                    record.setInterest_rate(rs.getDouble("interest_rate"));
                    record.setIssue_price(rs.getDouble("issue_price"));
                    record.setRedempion_price(rs.getDouble("redempion_price"));
                    record.setBusiness_days(rs.getInt("business_days"));
                    record.setAsset_class(rs.getInt("asset_class"));
                    record.setAmortization_schedule(rs.getInt("amortization_schedule"));
                    record.setDescription(rs.getString("description"));
                    record.setAccrual_daycount(rs.getInt("accrual_daycount"));
                    record.setMultiplier(rs.getDouble("multiplier"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Master_data record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_master_data(rs.getInt("id_master_data"));
                    record.setCode(rs.getString("code"));
                    record.setCurrency(rs.getInt("currency"));
                    record.setIssue_date(rs.getDate("issue_date"));
                    record.setMaturity_date(rs.getDate("maturity_date"));
                    record.setType_of_interest(rs.getInt("type_of_interest"));
                    record.setForm(rs.getInt("form"));
                    record.setDaycount(rs.getInt("daycount"));
                    record.setFrequency(rs.getInt("frequency"));
                    record.setRoll_convention(rs.getInt("roll_convention"));
                    record.setAccrual_schedule_type(rs.getInt("accrual_schedule_type"));
                    record.setInterest_rate(rs.getDouble("interest_rate"));
                    record.setIssue_price(rs.getDouble("issue_price"));
                    record.setRedempion_price(rs.getDouble("redempion_price"));
                    record.setBusiness_days(rs.getInt("business_days"));
                    record.setAsset_class(rs.getInt("asset_class"));
                    record.setAmortization_schedule(rs.getInt("amortization_schedule"));
                    record.setDescription(rs.getString("description"));
                    record.setAccrual_daycount(rs.getInt("accrual_daycount"));
                    record.setMultiplier(rs.getDouble("multiplier"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

        public List<Master_data> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Master_data> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_master_data,code,currency,calendar,issue_date,maturity_date,type_of_interest,form,daycount,frequency,roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,business_days,asset_class,amortization_schedule FROM master_data " + whereExpr;
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
                    Master_data record = null;
                    while (rs.next()) {
                        record = new Master_data();
                        record.setId_master_data(rs.getInt("id_master_data"));
                        record.setCode(rs.getString("code"));
                        record.setCurrency(rs.getInt("currency"));
                        record.setIssue_date(rs.getDate("issue_date"));
                        record.setMaturity_date(rs.getDate("maturity_date"));
                        record.setType_of_interest(rs.getInt("type_of_interest"));
                        record.setForm(rs.getInt("form"));
                        record.setDaycount(rs.getInt("daycount"));
                        record.setFrequency(rs.getInt("frequency"));
                        record.setRoll_convention(rs.getInt("roll_convention"));
                        record.setAccrual_schedule_type(rs.getInt("accrual_schedule_type"));
                        record.setInterest_rate(rs.getDouble("interest_rate"));
                        record.setIssue_price(rs.getDouble("issue_price"));
                        record.setRedempion_price(rs.getDouble("redempion_price"));
                        record.setBusiness_days(rs.getInt("business_days"));
                        record.setAsset_class(rs.getInt("asset_class"));
                        record.setAmortization_schedule(rs.getInt("amortization_schedule"));
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
