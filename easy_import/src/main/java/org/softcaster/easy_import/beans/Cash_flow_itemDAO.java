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

public class Cash_flow_itemDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO cash_flow_item(id_cash_flow_item,master_data,start_date,end_date,interest,amount) VALUES(nextval(('cash_flow_item_s'::text)::regclass),?,?,?,?,?)";
    private final String updateExpr = "UPDATE cash_flow_item SET master_data=?,start_date=?,end_date=?,interest=?,amount=? WHERE id_cash_flow_item=?";
    private final String removeExpr = "DELETE FROM cash_flow_item WHERE id_cash_flow_item_id=?";
    private final String selectByPKeyExpr = "SELECT id_cash_flow_item,master_data,start_date,end_date,interest,amount FROM cash_flow_item WHERE id_cash_flow_item=?";
    private final String selectByIdxExpr = "SELECT id_cash_flow_item,master_data,start_date,end_date,interest,amount FROM cash_flow_item WHERE master_data=? AND end_date=?";

    public Cash_flow_itemDAO() {
        ConnectioManager cm = ConnectioManager.getInstance();
        if (cm != null) {
            insertStmt = cm.createPreparedStatement(insertExpr);
            updateStmt = cm.createPreparedStatement(updateExpr);
            removeStmt = cm.createPreparedStatement(removeExpr);
            selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);
            selectStmt = cm.createPreparedStatement(selectByIdxExpr);
        }
    }

    public boolean insert(Cash_flow_item record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getMaster_data());
            insertStmt.setDate(2, record.getStart_date());
            insertStmt.setDate(3, record.getEnd_date());
            insertStmt.setDouble(4, record.getInterest());
            insertStmt.setDouble(5, record.getAmount());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Cash_flow_item record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1, record.getMaster_data());
            updateStmt.setDate(2, record.getStart_date());
            updateStmt.setDate(3, record.getEnd_date());
            updateStmt.setDouble(4, record.getInterest());
            updateStmt.setDouble(5, record.getAmount());
            updateStmt.setInt(6, record.getId_cash_flow_item());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Cash_flow_item record) {
        errorMsg = "";
        try {
            selectStmt.setInt(1, record.getMaster_data());
            selectStmt.setDate(2, record.getEnd_date());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_cash_flow_item(rs.getInt("id_cash_flow_item"));
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
    
    public boolean remove(Cash_flow_item record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_cash_flow_item());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Cash_flow_item record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_cash_flow_item());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_cash_flow_item(rs.getInt("id_cash_flow_item"));
                    record.setMaster_data(rs.getInt("master_data"));
                    record.setStart_date(rs.getDate("start_date"));
                    record.setEnd_date(rs.getDate("end_date"));
                    record.setInterest(rs.getDouble("interest"));
                    record.setAmount(rs.getDouble("amount"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Cash_flow_item> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Cash_flow_item> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_cash_flow_item,master_data,start_date,end_date,interest,amount FROM cash_flow_item " + whereExpr;
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
                    Cash_flow_item record = null;
                    while (rs.next()) {
                        record = new Cash_flow_item();
                        record.setId_cash_flow_item(rs.getInt("id_cash_flow_item"));
                        record.setMaster_data(rs.getInt("master_data"));
                        record.setStart_date(rs.getDate("start_date"));
                        record.setEnd_date(rs.getDate("end_date"));
                        record.setInterest(rs.getDouble("interest"));
                        record.setAmount(rs.getDouble("amount"));
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
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }
}
