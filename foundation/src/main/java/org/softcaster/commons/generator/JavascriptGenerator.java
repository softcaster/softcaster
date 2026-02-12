    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author emy
 */
public class JavascriptGenerator extends FileUtils implements IFileGenerator {

    public JavascriptGenerator(String tableName) {
        super(tableName);
    }

    public JavascriptGenerator() {
        super();
    }

    @Override
    public List<String> getImportClasses() {
        return null;
    }

    @Override
    public List<String> getAnnotations() {
        return null;
    }

    @Override
    public String getClassDef() {
        String classDef = "export class " + super.getClassName() + "{\n";
        return classDef;
    }

    @Override
    public String getClassName() {
        return super.getClassName();
    }

    @Override
    public String getFileName() {
        return "ddl.ts";
    }

    @Override
    public List<String> getFields() {
        List<String> fields = new ArrayList<>();
        if (columns != null) {
            String lastColumName = "";
            Iterator<ColumnData> it = columns.iterator();
            while (it.hasNext()) {
                lastColumName = it.next().getColumnName();
            }
            ColumnData columnData = null;
            it = columns.iterator();
            while (it.hasNext()) {
                columnData = it.next();
                if (columnData != null) {
                    if (lastColumName.equals(columnData.getColumnName())) {
                        fields.add("    public " + toCamelCase(columnData.getColumnName(), false) + "!: " + metaDataMgr.decodeTypeToJavaScript(Integer.parseInt(columnData.getDatatype())) + ";\n");
                    } else {
                        fields.add("    public " + toCamelCase(columnData.getColumnName(), false) + "!: " + metaDataMgr.decodeTypeToJavaScript(Integer.parseInt(columnData.getDatatype())) + ";\n");
                    }
                }
            }
            fields.add("\n");
        }
        return fields;
    }

    @Override
    public List<String> getMethods() {
        List<String> methods = new ArrayList<>();
        methods.add("    constructor() {}\n");
        methods.add("\n");
        methods.add("    reset(): void {\n");
        ColumnData columnData = null;
        Iterator<ColumnData> it = columns.iterator();
        String value = "";
        while (it.hasNext()) {
            columnData = it.next();
            if (columnData != null) {
                value = switch (columnData.getDatatype()) {
                    case "2", "3", "4", "5", "6", "8" -> "0";
                    case "12" -> "''";
                    case "16" -> "true";
                    default -> "null";
                }; // Numeric
                // Decimal
                // Integer
                // Smallint
                // Float
                // Double
                // `Varchar
                // Boolean
                // metaDataMgr.decodeTypeToJavaScript(Integer.parseInt(columnData.getDatatype()
                methods.add("         this." + toCamelCase(columnData.getColumnName(), false) + " = " + value + ";\n");
            }
        }
        methods.add("    }\n"); // chiusura reset
        methods.add("}\n"); // chiusura classe
        return methods;
    }

    @Override
    public String getLastLine() {
        return "\n";
    }
}
