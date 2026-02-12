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
public class RepositoryGenerator extends FileUtils implements IFileGenerator {

    public RepositoryGenerator() {
        super();
    }

    public RepositoryGenerator(String tableName) {
        super(tableName);
    }

    @Override
    public List<String> getImportClasses() {
        List<String> importDecl = new ArrayList<>();
        importDecl.add("import org.springframework.data.jpa.repository.JpaRepository;\n\n");
        return importDecl;
    }

    @Override
    public List<String> getAnnotations() {
        return null;
    }

    @Override
    public String getClassDef() {
        String classParameters = super.getClassName();
        if (primaryKey != null) {
            classParameters += "," + getJavaColumnType(primaryKey.getColumnName());
        }
        String classDef = "public interface " + getClassName() + " extends JpaRepository<" + classParameters + ">{\n";
        return classDef;
    }

    @Override
    public String getClassName() {
        return super.getClassName() + "Repository";
    }

    @Override
    public String getFileName() {
        return getClassName() + ".java";
    }

    @Override
    public List<String> getFields() {
        return null;
    }

    @Override
    public List<String> getMethods() {
        List<String> methods = new ArrayList<>();
        if (primaryKey != null) {
            String methodParameters = getJavaColumnType(primaryKey.getColumnName()) + " " + toCamelCase(primaryKey.getColumnName(), false);
            methods.add("\tpublic " + super.getClassName() + " findBy" + toCamelCase(primaryKey.getColumnName(), true) + "(" + methodParameters + ");\n");
        }

        return methods;
    }

    @Override
    public String getLastLine() {
        return "}\n";
    }
}
