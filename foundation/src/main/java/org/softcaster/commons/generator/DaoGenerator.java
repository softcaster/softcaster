/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author emy
 */
public class DaoGenerator extends FileUtils implements IFileGenerator {

    public DaoGenerator() {
        super();
    }

    public DaoGenerator(String tableName) {
        super(tableName);
    }

    @Override
    public List<String> getImportClasses() {
        List<String> importDecl = new ArrayList<>();
        importDecl.add("import java.util.List;\n");
        importDecl.add("import javax.annotation.Resource;\n");
        importDecl.add("import org.springframework.data.domain.Sort;\n");
        importDecl.add("import org.springframework.stereotype.Service;\n");
        importDecl.add("import org.springframework.transaction.annotation.Transactional;\n\n");

        return importDecl;
    }

    @Override
    public List<String> getAnnotations() {
        List<String> classAnnotation = new ArrayList<>();
        String classNameDAO = super.getClassName();
        classNameDAO = classNameDAO.substring(0, 1).toLowerCase() + classNameDAO.substring(1) + "DAO";
        classAnnotation.add("@Service(" + "\"" + classNameDAO + "\")\n");
        return classAnnotation;
    }

    @Override
    public String getClassDef() {
        String classDef = "public class " + super.getClassName() + "DAO {\n";
        return classDef;
    }

    @Override
    public String getClassName() {
        return super.getClassName() + "DAO";
    }

    @Override
    public String getFileName() {
        return getClassName() + ".java";
    }

    @Override
    public List<String> getFields() {
        List<String> fields = new ArrayList<>();
        fields.add("\t@Resource\n");
        fields.add("\tprivate " + super.getClassName() + "Repository repository;\n\n");

        ColumnData codOrd = getColumnData("cod_ord");
        if (codOrd != null) {
            fields.add("""
                       \tprivate final Sort sortByCodOrdAsc = new Sort(Sort.Direction.ASC,"codOrd");
                       
                       """);
        }

        return fields;
    }

    @Override
    public List<String> getMethods() {
        List<String> methods = new ArrayList<>();
        if (primaryKey != null) {
            methods.add("\t@Transactional(readOnly = true)\n");
            String methodParameters = getJavaColumnType(primaryKey.getColumnName()) + " " + toCamelCase(primaryKey.getColumnName(), false);
            methods.add("\tpublic " + super.getClassName() + " findBy" + toCamelCase(primaryKey.getColumnName(), true) + "(" + methodParameters + "){\n");
            methods.add("\t\treturn repository.findBy" + toCamelCase(primaryKey.getColumnName(), true) + "(" + toCamelCase(primaryKey.getColumnName(), false) + ");\n");
            methods.add("\t}\n\n");

        }

        ColumnData codOrd = getColumnData("cod_ord");
        if (codOrd != null) {
            String className = super.getClassName();
            String varName = toCamelCase(className, false);
            methods.add("\t@Transactional(readOnly = true)\n");
            methods.add("\tpublic List<" + className + "> findAll() {\n");
            methods.add("\t\tList<" + className + "> " + varName + "= repository.findAll(sortByCodOrdAsc);\n");
            methods.add("\t\treturn " + varName + ";\n");
            methods.add("\t}\n\n");
        }

        methods.add("\t@Transactional\n");
        String className = super.getClassName();
        String methodParameters = className + " " + className.substring(0, 1).toLowerCase() + className.substring(1);
        methods.add("\tpublic " + super.getClassName() + " saveOrUpdate(" + methodParameters + "){\n");
        methods.add("\t\treturn repository.save(" + className.substring(0, 1).toLowerCase() + className.substring(1) + ");\n");
        methods.add("\t}\n\n");

        methods.add("\t@Transactional\n");
        methods.add("\tpublic void delete(" + methodParameters + "){\n");
        methods.add("\t\trepository.delete(" + className.substring(0, 1).toLowerCase() + className.substring(1) + ");\n");
        methods.add("\t}\n\n");

        return methods;
    }

    @Override
    public String getLastLine() {
        return "}\n";
    }
}
