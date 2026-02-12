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
import org.softcaster.commons.utils.FileUtil;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author andrea
 */
public class JdbcClassBuilder {

    private final String path = "src";

    public boolean build(TableStructure tableStructure) {
        FileWriter outFile = null;
        try {
            String tableName = tableStructure.getTableName();
            String fileName = Character.toUpperCase(tableName.charAt(0)) + tableName.substring(1);
            outFile = new FileWriter(path + System.getProperty("file.separator") + fileName + ".java");
            PrintWriter out = new PrintWriter(outFile);

            // Intestazione
            if (writeHeader(out, fileName) == false) {
                outFile.close();
                return false;
            }

            // Variabili
            if (writeVariables(out, tableStructure.getColumns()) == false) {
                outFile.close();
                return false;
            }

            // Metodi d get/set
            if (writeMethods(out, tableStructure.getColumns()) == false) {
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

    private boolean writeHeader(PrintWriter out, String fileName) {
        String lineBuffer;
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

        lineBuffer = "import org.softcaster.foundation.generator.IRecord;";
        out.println(lineBuffer);
        out.println("");

        lineBuffer = "public class " + fileName + " implements IRecord " + " {";
        out.println(lineBuffer);
        out.println("");
        return true;
    }

    private boolean writeFooter(PrintWriter out) {
        String lineBuffer;
        lineBuffer = "}";
        out.println(lineBuffer);
        return true;
    }

    private boolean writeVariables(PrintWriter out, List<Column> columns) {
        if (columns == null) {
            return false;
        }

        String lineBuffer;

        Iterator<Column> iter = columns.iterator();
        Column column;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            lineBuffer = FileUtil.TAB + "private " + JdbcMetadata.getJavaType(column.getColumnType()) + " "
                    + column.getColumnName() + " = " + JdbcMetadata.getDefaultValue(column.getColumnType()) + ";";
            out.println(lineBuffer);
        }
        return true;
    }

    private boolean writeMethods(PrintWriter out, List<Column> columns) {
        if (columns == null) {
            return false;
        }

        out.println("");
        String lineBuffer;

        Iterator<Column> iter = columns.iterator();
        Column column;
        while (iter.hasNext()) {
            column = iter.next();
            if (column == null) {
                return false;
            }
            // Metodo di get
            String variableName = column.getColumnName();
            String methodName = "get" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
            lineBuffer = FileUtil.TAB + "public " + JdbcMetadata.getJavaType(column.getColumnType()) + " "
                    + methodName + "() {";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + "return " + variableName + ";";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + "}";
            out.println(lineBuffer);

            out.println("");

            // Metodo di set
            methodName = "set" + Character.toUpperCase(variableName.charAt(0)) + variableName.substring(1);
            lineBuffer = FileUtil.TAB + "public void " + methodName + "(" + JdbcMetadata.getJavaType(column.getColumnType()) + " "
                    + variableName + ") {";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + FileUtil.TAB + "this." + variableName + " = " + variableName + ";";
            out.println(lineBuffer);
            lineBuffer = FileUtil.TAB + "}";
            out.println(lineBuffer);

            out.println("");
        }
        return true;
    }

}
