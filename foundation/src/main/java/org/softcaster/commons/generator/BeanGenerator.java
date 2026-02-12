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
public class BeanGenerator extends FileUtils implements IFileGenerator {

    public BeanGenerator() {
        super();
    }

    public BeanGenerator(String tableName) {
        super(tableName);
    }

    @Override
    public List<String> getImportClasses() {
        List<String> importDecl = new ArrayList<>();

        importDecl.add("import java.io.Serializable;\n");
        importDecl.add("import javax.persistence.Column;\n");
        importDecl.add("import javax.persistence.Entity;\n");
        importDecl.add("import javax.persistence.GeneratedValue;\n");
        importDecl.add("import javax.persistence.GenerationType;\n");
        importDecl.add("import javax.persistence.Id;\n");
        importDecl.add("import javax.persistence.SequenceGenerator;\n");
        importDecl.add("import javax.persistence.Table;\n");
        importDecl.add("\n");

        return importDecl;
    }

    @Override
    public List<String> getAnnotations() {
        List<String> classAnnotation = new ArrayList<>();
        classAnnotation.add("@Entity\n");
        classAnnotation.add("@Table(name = " + "\"" + tableName + "\")\n");
        classAnnotation.add("@SuppressWarnings(\"PersistenceUnitPresent\")\n");
        classAnnotation.add("\n");

        return classAnnotation;
    }

    @Override
    public String getClassDef() {
        String classDef = "public class " + super.getClassName() + " implements Serializable {\n";
        return classDef;
    }

    @Override
    public String getClassName() {
        return super.getClassName();
    }

    @Override
    public String getFileName() {
        return super.getClassName() + ".java";
    }

    @Override
    public List<String> getFields() {
        List<String> columnDef = new ArrayList<>();

        if (primaryKey != null) {
            columnDef.add("\t@Id\n");
            columnDef.add("\t@SequenceGenerator(name = " + "\"" + tableName + "_seq" + "\"" + ", sequenceName = " + "\"" + tableName + "_s" + "\"" + ", allocationSize = 1)\n");
            columnDef.add("\t@GeneratedValue(strategy = GenerationType.AUTO, generator = " + "\"" + tableName + "_seq" + "\")\n");
            columnDef.add("\t@Column(name = " + "\"" + primaryKey.getColumnName() + "\"," + "columnDefinition = " + "\"" + getSqlColumnType(primaryKey.getColumnName()) + "\")\n");
            columnDef.add("\tprivate " + getJavaColumnType(primaryKey.getColumnName()) + " " + toCamelCase(primaryKey.getColumnName(), false) + ";\n");
            columnDef.add("\n");
        }

        if (columns != null) {
            Iterator<ColumnData> it = columns.iterator();
            ColumnData columnData;
            while (it.hasNext()) {
                columnData = it.next();
                if (columnData != null) {
                    if (primaryKey == null || columnData.getColumnName().equals(primaryKey.getColumnName())) {
                    } else {
                        columnDef.add("\t@Column(name =" + "\"" + columnData.getColumnName() + "\"" + ")\n");
                        columnDef.add("\tprivate " + getJavaColumnType(columnData.getColumnName()) + " " + toCamelCase(columnData.getColumnName(), false) + ";\n");
                        columnDef.add("\n");
                    }

                }
            }
        }
        return columnDef;
    }

    @Override
    public List<String> getMethods() {
        List<String> methods = new ArrayList<>();
        // Set e Get
        if (columns != null) {
            Iterator<ColumnData> it = columns.iterator();
            ColumnData columnData;
            while (it.hasNext()) {
                columnData = it.next();
                if (columnData != null) {
                    // get
                    methods.add("\tpublic " + getJavaColumnType(columnData.getColumnName()) + " get" + toCamelCase(columnData.getColumnName(), true) + "() {\n");
                    methods.add("\t\treturn " + toCamelCase(columnData.getColumnName(), false) + ";\n");
                    methods.add("\t}\n\n");

                    // set
                    methods.add("\tpublic void" + " set" + toCamelCase(columnData.getColumnName(), true) + "("
                            + getJavaColumnType(columnData.getColumnName()) + " " + toCamelCase(columnData.getColumnName(), false) + " " + ") {\n");
                    methods.add("\t\tthis." + toCamelCase(columnData.getColumnName(), false) + "=" + toCamelCase(columnData.getColumnName(), false) + ";\n");
                    methods.add("\t}\n\n");
                }
            }
        }

        if (primaryKey != null) {
            // Comparazione
            methods.add("\t@Override\n");
            methods.add("\tpublic boolean equals(Object obj) {\n");
            methods.add("\t\tif (this == obj) {\n");
            methods.add("\t\t\treturn true;\n");
            methods.add("\t\t}\n");
            methods.add("\t\tif (get" + toCamelCase(primaryKey.getColumnName(), true) + "() == null || obj == null || getClass() != obj.getClass()) {\n");
            methods.add("\t\t\treturn false;\n");
            methods.add("\t\t}\n");
            methods.add("\t\t" + getClassName() + " that = (" + getClassName() + ") obj;\n");
            methods.add("\t\treturn get" + toCamelCase(primaryKey.getColumnName(), true) + "().equals(that.get" + toCamelCase(primaryKey.getColumnName(), true) + "());\n");
            methods.add("\t}\n");

            methods.add("\n");

            methods.add("\t@Override\n");
            methods.add("\tpublic int hashCode() {\n");
            methods.add("\t\treturn get" + toCamelCase(primaryKey.getColumnName(), true) + "() == null ? 0 : " + toCamelCase(primaryKey.getColumnName(), false) + ".hashCode();\n");
            methods.add("\t}\n");
        }
        return methods;
    }

    @Override
    public String getLastLine() {
        return "}\n";
    }
}
