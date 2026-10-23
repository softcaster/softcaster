// Generato automaticamente in origine; adattato a mano alla tabella intermedia
// cash_flow_master_data: non rigenerare senza riportare la separazione delle colonne.
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

/**
 * Accesso JDBC all'anagrafica di uno strumento con flussi.
 *
 * Il bean Master_data contiene tutti i campi; sul database sono divisi tra
 * master_data (dati comuni) e cash_flow_master_data (convenzioni dei flussi,
 * prezzi di emissione e rimborso). Insert e update scrivono entrambe le
 * tabelle: usare solo per strumenti con flussi (titoli).
 */
public class Master_dataDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement upsertCashFlowStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;

    private static final String SELECT_COLUMNS = "SELECT md.id_master_data,md.code,md.currency,md.issue_date,md.maturity_date,"
            + "cf.type_of_interest,cf.form,cf.daycount,cf.frequency,cf.roll_convention,cf.accrual_schedule_type,"
            + "cf.interest_rate,cf.issue_price,cf.redempion_price,md.business_days,md.asset_class,cf.amortization_schedule,"
            + "md.description,cf.accrual_daycount,md.multiplier "
            + "FROM master_data md LEFT JOIN cash_flow_master_data cf ON cf.id_master_data = md.id_master_data ";

    private final String insertExpr = "INSERT INTO master_data(id_master_data,code,currency,issue_date,maturity_date,business_days,asset_class,description,multiplier) "
            + "VALUES(nextval(('master_data_s'::text)::regclass),?,?,?,?,?,?,?,?) RETURNING id_master_data";
    private final String updateExpr = "UPDATE master_data SET code=?,currency=?,issue_date=?,maturity_date=?,business_days=?,asset_class=?,description=?,multiplier=? "
            + "WHERE id_master_data=?";
    // compounding non e' nel bean: alla creazione resta il default della colonna
    private final String upsertCashFlowExpr = "INSERT INTO cash_flow_master_data(id_master_data,type_of_interest,form,daycount,accrual_daycount,frequency,"
            + "roll_convention,accrual_schedule_type,interest_rate,issue_price,redempion_price,amortization_schedule) "
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?) "
            + "ON CONFLICT (id_master_data) DO UPDATE SET type_of_interest=EXCLUDED.type_of_interest,form=EXCLUDED.form,"
            + "daycount=EXCLUDED.daycount,accrual_daycount=EXCLUDED.accrual_daycount,frequency=EXCLUDED.frequency,"
            + "roll_convention=EXCLUDED.roll_convention,accrual_schedule_type=EXCLUDED.accrual_schedule_type,"
            + "interest_rate=EXCLUDED.interest_rate,issue_price=EXCLUDED.issue_price,redempion_price=EXCLUDED.redempion_price,"
            + "amortization_schedule=EXCLUDED.amortization_schedule";
    private final String removeExpr = "DELETE FROM master_data WHERE id_master_data=?";
    private final String selectByPKeyExpr = SELECT_COLUMNS + "WHERE md.id_master_data=?";
    private final String selectByIdxExpr = SELECT_COLUMNS + "WHERE md.code=?";

    public Master_dataDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            upsertCashFlowStmt = cm.createPreparedStatement(upsertCashFlowExpr);
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
            insertStmt.setInt(5, record.getBusiness_days());
            insertStmt.setInt(6, record.getAsset_class());
            insertStmt.setString(7, record.getDescription());
            insertStmt.setDouble(8, record.getMultiplier());
            try (ResultSet rs = insertStmt.executeQuery()) {
                rs.next();
                record.setId_master_data(rs.getInt(1));
            }
            return upsertCashFlow(record);
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Master_data record) {
        errorMsg = "";
        try {
            updateStmt.setString(1, record.getCode());
            updateStmt.setInt(2, record.getCurrency());
            updateStmt.setDate(3, record.getIssue_date());
            updateStmt.setDate(4, record.getMaturity_date());
            updateStmt.setInt(5, record.getBusiness_days());
            updateStmt.setInt(6, record.getAsset_class());
            updateStmt.setString(7, record.getDescription());
            updateStmt.setDouble(8, record.getMultiplier());
            updateStmt.setInt(9, record.getId_master_data());
            updateStmt.executeUpdate();
            return upsertCashFlow(record);
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    /**
     * Crea o aggiorna la riga di cash_flow_master_data dello strumento.
     */
    private boolean upsertCashFlow(Master_data record) throws SQLException {
        upsertCashFlowStmt.setInt(1, record.getId_master_data());
        upsertCashFlowStmt.setInt(2, record.getType_of_interest());
        upsertCashFlowStmt.setInt(3, record.getForm());
        upsertCashFlowStmt.setInt(4, record.getDaycount());
        upsertCashFlowStmt.setInt(5, record.getAccrual_daycount());
        upsertCashFlowStmt.setInt(6, record.getFrequency());
        upsertCashFlowStmt.setInt(7, record.getRoll_convention());
        upsertCashFlowStmt.setInt(8, record.getAccrual_schedule_type());
        upsertCashFlowStmt.setDouble(9, record.getInterest_rate());
        upsertCashFlowStmt.setDouble(10, record.getIssue_price());
        upsertCashFlowStmt.setDouble(11, record.getRedempion_price());
        upsertCashFlowStmt.setInt(12, record.getAmortization_schedule());
        upsertCashFlowStmt.executeUpdate();
        return true;
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
            selectByPKeyStmt.setInt(1, record.getId_master_data());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    fill(record, rs);
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Master_data record) {
        errorMsg = "";
        try {
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    fill(record, rs);
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    /**
     * @param whereExpr condizione sulle colonne, con alias md (master_data) e
     * cf (cash_flow_master_data); le colonne senza alias e non ambigue vanno
     * bene (es. "WHERE code = ?")
     */
    public List<Master_data> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Master_data> records = new ArrayList<>();
            String selectByWhereExpr = SELECT_COLUMNS + whereExpr;
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
                    while (rs.next()) {
                        Master_data record = new Master_data();
                        fill(record, rs);
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

    private static void fill(Master_data record, ResultSet rs) throws SQLException {
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public boolean closeStatements() {
        try {
            insertStmt.close();
            updateStmt.close();
            upsertCashFlowStmt.close();
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
