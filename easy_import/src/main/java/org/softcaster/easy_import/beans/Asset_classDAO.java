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

public class Asset_classDAO {

    protected String errorMsg = "";

    private PreparedStatement insertStmt = null;
    private PreparedStatement updateStmt = null;
    private PreparedStatement removeStmt = null;
    private PreparedStatement selectByPKeyStmt = null;
    private PreparedStatement selectByIdxStmt = null;
    private PreparedStatement selectStmt = null;

    private final String insertExpr = "INSERT INTO asset_class(id_asset_class,super_class,code,description) VALUES(nextval(('asset_class_s'::text)::regclass),?,?,?)";
    private final String updateExpr = "UPDATE asset_class SET super_class=?,code=?,description=? WHERE id_asset_class=?";
    private final String removeExpr = "DELETE FROM asset_class WHERE id_asset_class_id=?";
    private final String selectExpr = "SELECT id_asset_class FROM asset_class WHERE code=?";
    private final String selectByPKeyExpr = "SELECT id_asset_class,super_class,code,description FROM asset_class WHERE id_asset_class=?";
    private final String selectByIdxExpr = "SELECT id_asset_class,super_class,code,description FROM asset_class WHERE code=?";

    public Asset_classDAO() {
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

    public boolean insert(Asset_class record) {
        errorMsg = "";
        try {
            insertStmt.setInt(1, record.getSuper_class());
            insertStmt.setString(2, record.getCode());
            insertStmt.setString(3, record.getDescription());
            insertStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean update(Asset_class record) {
        errorMsg = "";
        try {
            updateStmt.setInt(1, record.getSuper_class());
            updateStmt.setString(2, record.getCode());
            updateStmt.setString(3, record.getDescription());
            updateStmt.setInt(4, record.getId_asset_class());
            updateStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean insertOrUpdate(Asset_class record) {
        errorMsg = "";
        try {
            selectStmt.setString(1, record.getCode());
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                record.setId_asset_class(rs.getInt("id_asset_class"));
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

    public boolean remove(Asset_class record) {
        errorMsg = "";
        try {
            removeStmt.setInt(1, record.getId_asset_class());
            removeStmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByPKey(Asset_class record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByPKeyStmt.setInt(1, record.getId_asset_class());
            try (ResultSet rs = selectByPKeyStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_asset_class(rs.getInt("id_asset_class"));
                    record.setSuper_class(rs.getInt("super_class"));
                    record.setCode(rs.getString("code"));
                    record.setDescription(rs.getString("description"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public boolean loadByIdx(Asset_class record) {
        errorMsg = "";
        try {
            boolean found = false;
            selectByIdxStmt.setString(1, record.getCode());
            try (ResultSet rs = selectByIdxStmt.executeQuery()) {
                if (rs.next()) {
                    found = true;
                    record.setId_asset_class(rs.getInt("id_asset_class"));
                    record.setSuper_class(rs.getInt("super_class"));
                    record.setCode(rs.getString("code"));
                    record.setDescription(rs.getString("description"));
                }
            }
            return found;
        } catch (SQLException ex) {
            errorMsg = ex.getLocalizedMessage();
            LoggerMgr.logError(errorMsg);
            return false;
        }
    }

    public List<Asset_class> loadRecordList(String whereExpr, List<JdbcParam> params) {
        errorMsg = "";
        try {
            List<Asset_class> records = new ArrayList<>();
            String selectByWhereExpr = "SELECT id_asset_class,super_class,code,description FROM asset_class " + whereExpr;
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
                    Asset_class record = null;
                    while (rs.next()) {
                        record = new Asset_class();
                        record.setId_asset_class(rs.getInt("id_asset_class"));
                        record.setSuper_class(rs.getInt("super_class"));
                        record.setCode(rs.getString("code"));
                        record.setDescription(rs.getString("description"));
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
