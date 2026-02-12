/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author andrea
 */
public class JdbcDaoBuilder {

    private final String path = "src";
    private TableStructure tableStructure = null;
    private String pKey = "";
    private int pKeyType = DATA_TYPE.INVALID;
    String tableName = "";
    String className = "";
    String classDaoName = "";

    private int getType(String name) {
        Iterator<Column> iter = tableStructure.getColumns().iterator();
        Column column = null;
        while (iter.hasNext()) {
            column = iter.next();
            if (column != null) {
                if (column.getColumnName().equalsIgnoreCase(name)) {
                    return column.getColumnType();
                }
            }
        }
        return DATA_TYPE.INVALID;
    }

    private boolean initData() {
        if (tableStructure == null) {
            return false;
        }

        // Nome tabella
        tableName = tableStructure.getTableName();

        // Classe record database
        className = Character.toUpperCase(tableName.charAt(0)) + tableName.substring(1);

        // Classe DAO
        classDaoName = Character.toUpperCase(tableName.charAt(0)) + tableName.substring(1) + "DAO";

        // Chiave primaria
        pKey = tableStructure.getpKeys().get(0).getColumnName();

        // Tipo primaria
        pKeyType = getType(pKey);

        return true;
    }

    public boolean build(TableStructure tableStructure) {
        this.tableStructure = tableStructure;
        if (initData() == false) {
            return false;
        }
        FileWriter outFile = null;
        try {
            outFile = new FileWriter(path + System.getProperty("file.separator") + classDaoName + ".java");
            PrintWriter out = new PrintWriter(outFile);

            // Intestazione
            if (writeHeader(out) == false) {
                outFile.close();
                return false;
            }

            // Variabili
            if (writeVariables(out) == false) {
                outFile.close();
                return false;
            }

            // Costruttore
            if (writeConstructor(out) == false) {
                outFile.close();
                return false;
            }

            // Metodo Insert
            if (writeInsertMethod(out) == false) {
                outFile.close();
                return false;
            }

            // Metodo Insert
            if (writeUpdateMethod(out) == false) {
                outFile.close();
                return false;
            }

            // Metodo Insert/Update
            if (writeInsertOrUpdateMethod(out) == false) {
                outFile.close();
                return false;
            }

            // Metodo Remove
            if (writeRemoveMethod(out) == false) {
                outFile.close();
                return false;
            }

            // load ds chiave primaria
            if (writeLoadByPKeyMethod(out) == false) {
                outFile.close();
                return false;
            }

            // load ds chiave logica
            if (writeLoadByIdxMethod(out) == false) {
                outFile.close();
                return false;
            }

            // load lista con whereCond
            if (writeLoadRecordListMethod(out) == false) {
                outFile.close();
                return false;
            }

            if (writeFooter(out) == false) {
                outFile.close();
                return false;
            }

            outFile.close();
            return true;
        } catch (IOException ex) {
            try {
                LoggerMgr.logError(ex.getLocalizedMessage());
                if (outFile != null) {
                    outFile.close();
                }
                return false;
            } catch (IOException ex1) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return false;
            }
        }
    }

    private boolean writeHeader(PrintWriter out) {
        String lineBuffer = "";
        lineBuffer = "// File generato automaticamente. Non modificare!";
        out.println(lineBuffer);
        out.println("");

        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        if (paramsMgr != null) {
            lineBuffer = paramsMgr.getParamValue("PACKAGE");
            if (lineBuffer != null && !lineBuffer.isEmpty()) {
                out.println(lineBuffer);
                out.println("");
            }
        }

        // import
        lineBuffer = "import java.util.List;";
        out.println(lineBuffer);

        lineBuffer = "import java.util.ArrayList;";
        out.println(lineBuffer);

        lineBuffer = "import java.util.Iterator;";
        out.println(lineBuffer);

        lineBuffer = "import java.sql.PreparedStatement;";
        out.println(lineBuffer);

        lineBuffer = "import java.sql.ResultSet;";
        out.println(lineBuffer);

        lineBuffer = "import java.sql.SQLException;";
        out.println(lineBuffer);
        /*        
        lineBuffer = "import java.util.logging.Level;";
        out.println(lineBuffer);
         */
        lineBuffer = "import org.softcaster.foundation.generator.ConnectioManager;";
        out.println(lineBuffer);

        lineBuffer = "import org.softcaster.foundation.utils.LoggerMgr;";
        out.println(lineBuffer);

        lineBuffer = "import org.softcaster.foundation.generator.DATA_TYPE;";
        out.println(lineBuffer);

        lineBuffer = "import org.softcaster.foundation.generator.JdbcParam;";
        out.println(lineBuffer);

        out.println("");

        lineBuffer = "public class " + classDaoName + " {";
        out.println(lineBuffer);
        out.println("");
        return true;
    }

    private boolean writeVariables(PrintWriter out) {
        List<Column> columns = tableStructure.getColumns();
        List<String> indexes = getIndexes();
        if (columns == null) {
            return false;
        }

        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "protected String errorMsg = \"\";";
        out.println(lineBuffer);

        out.println("");

        lineBuffer = FileUtil.TAB + "private PreparedStatement insertStmt = null;";
        out.println(lineBuffer);
        lineBuffer = FileUtil.TAB + "private PreparedStatement updateStmt = null;";
        out.println(lineBuffer);
        lineBuffer = FileUtil.TAB + "private PreparedStatement removeStmt = null;";
        out.println(lineBuffer);
        lineBuffer = FileUtil.TAB + "private PreparedStatement selectByPKeyStmt = null;";
        out.println(lineBuffer);
        if (getIndexes() != null) {
            lineBuffer = FileUtil.TAB + "private PreparedStatement selectByIdxStmt = null;";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + "private PreparedStatement selectStmt = null;";
            out.println(lineBuffer);
        }
        out.println("");

        // Stringa insert
        lineBuffer = "INSERT INTO " + tableName + "(";
        Iterator<Column> iter = columns.iterator();
        Column column = null;
        String autoIncrement = "nextval(('" + tableName + "_s" + "'::text)::regclass),";
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            lineBuffer = lineBuffer + column.getColumnName() + ",";
        }
        // Sostituisco ultima virgola 
        lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 1) + ") VALUES(";
        for (int i = 0; i < columns.size() - 1; i++) {
            if (i == 0) {
                lineBuffer = lineBuffer + autoIncrement + "?,";
            } else {
                lineBuffer = lineBuffer + "?,";
            }
        }
        // Sostituisco ultima virgola
        lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 1) + ")";

        String insertString = FileUtil.TAB + "private final String insertExpr = \"" + lineBuffer + "\";";
        out.println(insertString);

        // Stringa update
        lineBuffer = "UPDATE " + tableName + " SET ";
        iter = columns.iterator();
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            if (column.getColumnName().equalsIgnoreCase(pKey)) {
                continue;
            }
            lineBuffer = lineBuffer + column.getColumnName() + "=?,";
        }
        // Sostituisco ultima virgola
        lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 1) + " WHERE " + pKey + "=?";
        insertString = FileUtil.TAB + "private final String updateExpr = \"" + lineBuffer + "\";";
        out.println(insertString);

        // Stringa delete
        lineBuffer = "DELETE FROM " + tableName + " WHERE " + pKey + "_id=?";
        insertString = FileUtil.TAB + "private final String removeExpr = \"" + lineBuffer + "\";";
        out.println(insertString);

        // Stringa select
        Iterator<String> idxIter = null;
        if (indexes != null) {
            lineBuffer = "SELECT " + pKey + " FROM " + tableName + " WHERE ";
            idxIter = indexes.iterator();
            while (idxIter.hasNext()) {
                lineBuffer = lineBuffer + idxIter.next() + "=? AND ";
            }
            lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 5);
            insertString = FileUtil.TAB + "private final String selectExpr = \"" + lineBuffer + "\";";
            out.println(insertString);
        }
        // Stringa selectByPKey
        lineBuffer = "SELECT ";
        iter = columns.iterator();
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            lineBuffer = lineBuffer + column.getColumnName() + ",";
        }
        // Sostituisco ultima virgola
        lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 1) + " FROM " + tableName + " WHERE " + pKey + "=?";
        insertString = FileUtil.TAB + "private final String selectByPKeyExpr = \"" + lineBuffer + "\";";
        out.println(insertString);

        // Stringa selectByIdx
        if (indexes != null && idxIter != null) {
            // Stringa selectByIdx
            lineBuffer = "SELECT ";
            iter = columns.iterator();
            while (iter.hasNext()) {
                column = iter.next();
                if (column == null) {
                    return false;
                }
                lineBuffer = lineBuffer + column.getColumnName() + ",";
            }
            // Condizione where
            lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 1) + " FROM " + tableName + " WHERE ";
            idxIter = indexes.iterator();
            while (idxIter.hasNext()) {
                lineBuffer = lineBuffer + idxIter.next() + "=? AND ";
            }
            lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 5);
            insertString = FileUtil.TAB + "private final String selectByIdxExpr = \"" + lineBuffer + "\";";
            out.println(insertString);
        }
        out.println("");
        return true;
    }

    private boolean writeConstructor(PrintWriter out) {

        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public " + classDaoName + "() {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "ConnectioManager cm = ConnectioManager.getInstance();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "if (cm != null) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "insertStmt = cm.createPreparedStatement(insertExpr);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "updateStmt = cm.createPreparedStatement(updateExpr);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "removeStmt = cm.createPreparedStatement(removeExpr);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectByPKeyStmt = cm.createPreparedStatement(selectByPKeyExpr);";
        out.println(lineBuffer);

        if (getIndexes() != null) {
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectByIdxStmt = cm.createPreparedStatement(selectByIdxExpr);";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectStmt = cm.createPreparedStatement(selectExpr);";
            out.println(lineBuffer);
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeInsertMethod(PrintWriter out) {
        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public boolean insert(" + className + " record) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        Iterator<Column> iter = tableStructure.getColumns().iterator();
        Column column = null;
        String variableName = null;
        String methodName = null;
        int counter = 1;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            variableName = column.getColumnName();
            if (variableName.equalsIgnoreCase(pKey)) {
                continue;
            }
            methodName = "get" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
            if (column.getColumnType() == DATA_TYPE.DATE) {
                methodName = methodName + "().sqlDate()";
            } else {
                methodName = methodName + "()";
            }
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "insertStmt." + JdbcMetadata.getSqlMethod(column.getColumnType(), true) + "(" + counter + "," + "record." + methodName + ");";
            out.println(lineBuffer);
            counter++;
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "insertStmt.executeUpdate();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return true;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeUpdateMethod(PrintWriter out) {
        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public boolean update(" + className + " record) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        Iterator<Column> iter = tableStructure.getColumns().iterator();
        Column column = null;
        String variableName = null;
        String methodName = null;
        int counter = 1;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            variableName = column.getColumnName();
            if (variableName.equalsIgnoreCase(pKey)) {
                continue;
            }
            methodName = "get" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
            if (column.getColumnType() == DATA_TYPE.DATE) {
                methodName = methodName + "().sqlDate()";
            } else {
                methodName = methodName + "()";
            }
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "updateStmt." + JdbcMetadata.getSqlMethod(column.getColumnType(), true) + "(" + counter + "," + "record." + methodName + ");";
            out.println(lineBuffer);
            counter++;
        }

        methodName = "get" + Character.toUpperCase(pKey.charAt(0)) + pKey.substring(1) + "()";
        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "updateStmt." + JdbcMetadata.getSqlMethod(pKeyType, true) + "(" + counter + ",record." + methodName + ");";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "updateStmt.executeUpdate();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return true;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeInsertOrUpdateMethod(PrintWriter out) {
        if (getIndexes() == null) {
            return true;
        }

        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public boolean insertOrUpdate(" + className + " record) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        Iterator<Column> iter = tableStructure.getColumns().iterator();
        Column column = null;
        String variableName = null;
        String methodName = null;
        int counter = 1;
        List<String> indexes = getIndexes();
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            variableName = column.getColumnName();
            if (variableName.equalsIgnoreCase(pKey)) {
                continue;
            }
            if (!isIndexField(variableName, indexes)) {
                continue;
            }
            methodName = "get" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
            if (column.getColumnType() == DATA_TYPE.DATE) {
                methodName = methodName + "().sqlDate()";
            } else {
                methodName = methodName + "()";
            }
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectStmt." + JdbcMetadata.getSqlMethod(column.getColumnType(), true) + "(" + counter + "," + "record." + methodName + ");";
            out.println(lineBuffer);
            counter++;
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "ResultSet rs = selectStmt.executeQuery();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "if (rs.next()) {";
        out.println(lineBuffer);

        methodName = "set" + Character.toUpperCase(pKey.charAt(0)) + pKey.substring(1);
        String sqlMethodName = JdbcMetadata.getSqlMethod(pKeyType, false);
        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName + "(rs." + sqlMethodName + "(\"" + pKey + "\"));";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "rs.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return update(record);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "} else {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "rs.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return insert(record);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeRemoveMethod(PrintWriter out) {
        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public boolean remove(" + className + " record) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        String methodName = "get" + Character.toUpperCase(pKey.charAt(0)) + pKey.substring(1);
        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "removeStmt." + JdbcMetadata.getSqlMethod(pKeyType, true) + "(1, record." + methodName + "());";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "removeStmt.executeUpdate();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return true;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeLoadByPKeyMethod(PrintWriter out) {
        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public boolean loadByPKey(" + className + " record) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "boolean found=false;";
        out.println(lineBuffer);

        Iterator<Column> iter = tableStructure.getColumns().iterator();
        Column column = null;
        String variableName = null;
        String methodName = null;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            if (column.getColumnName().equalsIgnoreCase(pKey)) {
                variableName = column.getColumnName();
                methodName = "get" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1) + "()";
                lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                        + "selectByPKeyStmt." + JdbcMetadata.getSqlMethod(column.getColumnType(), true) + "(" + 1 + "," + "record." + methodName + ");";
                out.println(lineBuffer);
                break;
            }
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "ResultSet rs = selectByPKeyStmt.executeQuery();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "if (rs.next()) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "found=true;";
        out.println(lineBuffer);

        iter = tableStructure.getColumns().iterator();
        String sqlMethodName = null;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            methodName = "set" + Character.toUpperCase(column.getColumnName().charAt(0)) + column.getColumnName().substring(1);
            sqlMethodName = JdbcMetadata.getSqlMethod(column.getColumnType(), false);
            if (column.getColumnType() == DATA_TYPE.DATE) {
                lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName
                        + "(new org.softcaster.utils.Date(rs." + sqlMethodName + "(\"" + column.getColumnName() + "\")));";
            } else {
                lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName + "(rs." + sqlMethodName + "(\"" + column.getColumnName() + "\"));";
            }
            out.println(lineBuffer);
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "rs.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return found;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeLoadByIdxMethod(PrintWriter out) {
        if (getIndexes() == null) {
            return true;
        }

        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public boolean loadByIdx(" + className + " record) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "boolean found=false;";
        out.println(lineBuffer);

        Iterator<Column> iter = tableStructure.getColumns().iterator();
        Column column = null;
        String variableName = null;
        String methodName = null;
        int counter = 1;
        List<String> indexes = getIndexes();
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            variableName = column.getColumnName();
            if (variableName.equalsIgnoreCase(pKey)) {
                continue;
            }
            if (!isIndexField(variableName, indexes)) {
                continue;
            }
            methodName = "get" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
            if (column.getColumnType() == DATA_TYPE.DATE) {
                methodName = methodName + "().sqlDate()";
            } else {
                methodName = methodName + "()";
            }
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectByIdxStmt." + JdbcMetadata.getSqlMethod(column.getColumnType(), true) + "(" + counter + "," + "record." + methodName + ");";
            out.println(lineBuffer);
            counter++;
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "ResultSet rs = selectByIdxStmt.executeQuery();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "if (rs.next()) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "found=true;";
        out.println(lineBuffer);

        iter = tableStructure.getColumns().iterator();
        String sqlMethodName = null;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            methodName = "set" + Character.toUpperCase(column.getColumnName().charAt(0)) + column.getColumnName().substring(1);
            sqlMethodName = JdbcMetadata.getSqlMethod(column.getColumnType(), false);
            if (column.getColumnType() == DATA_TYPE.DATE) {
                lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName
                        + "(new org.softcaster.utils.Date(rs." + sqlMethodName + "(\"" + column.getColumnName() + "\")));";
            } else {
                lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName + "(rs." + sqlMethodName + "(\"" + column.getColumnName() + "\"));";
            }
            out.println(lineBuffer);
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "rs.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return found;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeLoadRecordListMethod(PrintWriter out) {
        String lineBuffer = "";
        lineBuffer = FileUtil.TAB + "public " + "List<" + className + "> loadRecordList(String whereExpr, List<JdbcParam> params) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "errorMsg = \"\";";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                + "List<" + className + "> records = new ArrayList<>();";
        out.println(lineBuffer);

        List<Column> columns = tableStructure.getColumns();
        Column column = null;
        // Stringa selectByWhereExpr
        if (columns != null) {
            // Stringa selectByIdx
            lineBuffer = "SELECT ";
            Iterator<Column> iter = columns.iterator();
            while (iter.hasNext()) {
                column = iter.next();
                if (column == null) {
                    return false;
                }
                lineBuffer = lineBuffer + column.getColumnName() + ",";
            }
            // fine select
            lineBuffer = lineBuffer.substring(0, lineBuffer.length() - 1) + " FROM " + tableName + " \" + whereExpr;";
            String insertString = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "String selectByWhereExpr = \"" + lineBuffer;
            out.println(insertString);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "ConnectioManager cm = ConnectioManager.getInstance();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "PreparedStatement selectByWhereStmt = cm.createPreparedStatement(selectByWhereExpr);";
            out.println(lineBuffer);

            // Bind parametri se esistono
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "if(params.size() > 0) {";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "int counter = 1;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "Iterator<JdbcParam> iter = params.iterator();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "JdbcParam param = null;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "while(iter.hasNext()) {";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "param = iter.next();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "if(param != null) {";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "int paramType = param.getValueType();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "switch(paramType) {";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "case DATA_TYPE.TEXT:";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectByWhereStmt.setString(counter,param.getStringValue());";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "break;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "case DATA_TYPE.INTEGER:";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectByWhereStmt.setInt(counter,param.getIntegerValue());";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "break;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "case DATA_TYPE.LONG:";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectByWhereStmt.setLong(counter,param.getLongValue());";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "break;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "case DATA_TYPE.REAL:";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectByWhereStmt.setDouble(counter,param.getDoubleValue());";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "break;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "case DATA_TYPE.DATE:";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "selectByWhereStmt.setDate(counter,param.getDateValue());";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "break;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "default:";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "break;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "}";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB
                    + "}";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "counter++;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "}";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "}";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "ResultSet rs = selectByWhereStmt.executeQuery();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + className + " record = null;";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "while(rs.next()) {";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record = new " + className + "();";
            out.println(lineBuffer);

            iter = tableStructure.getColumns().iterator();
            String sqlMethodName = null;
            String methodName = null;
            while (iter.hasNext()) {
                column = iter.next();
                if (column == null) {
                    return false;
                }
                methodName = "set" + Character.toUpperCase(column.getColumnName().charAt(0)) + column.getColumnName().substring(1);
                sqlMethodName = JdbcMetadata.getSqlMethod(column.getColumnType(), false);
                if (column.getColumnType() == DATA_TYPE.DATE) {
                    lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName
                            + "(new org.softcaster.utils.Date(rs." + sqlMethodName + "(\"" + column.getColumnName() + "\")));";
                } else {
                    lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "record." + methodName + "(rs." + sqlMethodName + "(\"" + column.getColumnName() + "\"));";
                }
                out.println(lineBuffer);
            }

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "records.add(record);";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "}";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "rs.close();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectByWhereStmt.close();";
            out.println(lineBuffer);
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return records;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return null;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        out.println("");
        return true;
    }

    private boolean writeFooter(PrintWriter out) {
        String lineBuffer = "";

        // Metodo getErrorMsg
        lineBuffer = FileUtil.TAB + "public String getErrorMsg() {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "return errorMsg;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = "";
        out.println(lineBuffer);

        // Metodo closeStatements
        lineBuffer = FileUtil.TAB + "public boolean closeStatements() {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "try {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "insertStmt.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "updateStmt.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "removeStmt.close();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectByPKeyStmt.close();";
        out.println(lineBuffer);

        if (getIndexes() != null) {
            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectStmt.close();";
            out.println(lineBuffer);

            lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "selectByIdxStmt.close();";
            out.println(lineBuffer);
        }

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return true;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "} catch (SQLException ex) {";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "errorMsg = ex.getLocalizedMessage();";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "LoggerMgr.logError(errorMsg);";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + FileUtil.TAB + "return false;";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + FileUtil.TAB + "}";
        out.println(lineBuffer);

        lineBuffer = FileUtil.TAB + "}";
        out.println(lineBuffer);

        // Chiusura classe
        lineBuffer = "}";
        out.println(lineBuffer);
        return true;
    }

    private List<String> getIndexes() {
        List<Column> list = tableStructure.getpKeys();
        Column key = list.get(0);
        Map<String, List<String>> indexed = tableStructure.getIndexes();
        if (indexed != null) {
            Iterator iter = indexed.entrySet().iterator();
            Map.Entry pairs = null;
            while (iter.hasNext()) {
                pairs = (Map.Entry) iter.next();
                if (pairs.getKey() instanceof String) {
                    List<String> idxColumn = (List<String>) pairs.getValue();
                    Iterator<String> idxIter = idxColumn.iterator();
                    boolean containsPkey = false;
                    while (idxIter.hasNext()) {
                        if (idxIter.next().equalsIgnoreCase(key.getColumnName())) {
                            containsPkey = true;
                            break;
                        }
                    }
                    if (containsPkey) {
                        continue;
                    }
                    return (List<String>) pairs.getValue();
                }
            }
        }
        return null;
    }

    private boolean isIndexField(String variableName, List<String> indexes) {
        if (indexes == null) {
            return false;
        }

        Iterator<String> iter = indexes.iterator();
        while (iter.hasNext()) {
            if (iter.next().equalsIgnoreCase(variableName)) {
                return true;
            }
        }
        return false;
    }
}
