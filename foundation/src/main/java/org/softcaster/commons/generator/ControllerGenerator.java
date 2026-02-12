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
public class ControllerGenerator extends FileUtils implements IFileGenerator {

    public ControllerGenerator(String tableName) {
        super(tableName);
    }

    public ControllerGenerator() {
        super();
    }

    @Override
    public List<String> getImportClasses() {
        List<String> importDecl = new ArrayList<>();
        importDecl.add("import java.util.List;\n");
        importDecl.add("import org.softcaster.checkin_bo.data." + super.getClassName() + ";\n");
        importDecl.add("import org.softcaster.checkin_bo.data." + super.getClassName() + "DAO;\n");
        importDecl.add("import org.springframework.beans.factory.annotation.Autowired;\n");
        importDecl.add("import org.springframework.context.ApplicationContext;\n");
        importDecl.add("import org.springframework.http.HttpStatus;\n");
        importDecl.add("import org.springframework.http.ResponseEntity;\n");
        importDecl.add("import org.springframework.web.bind.annotation.GetMapping;\n");
        importDecl.add("import org.springframework.web.bind.annotation.PathVariable;\n");
        importDecl.add("import org.springframework.web.bind.annotation.PostMapping;\n");
        importDecl.add("import org.springframework.web.bind.annotation.RequestBody;\n");
        importDecl.add("import org.springframework.web.bind.annotation.RestController;\n");

        return importDecl;
    }

    @Override
    public List<String> getAnnotations() {
        List<String> classAnnotation = new ArrayList<>();
        classAnnotation.add("@RestController\n");
        return classAnnotation;
    }

    @Override
    public String getClassDef() {
        String classDef = "public class " + super.getClassName() + "RestController {\n";
        return classDef;
    }

    @Override
    public String getClassName() {
        return super.getClassName() + "RestController";
    }

    @Override
    public String getFileName() {
        return getClassName() + ".java";
    }

    @Override
    public List<String> getFields() {
        List<String> fieldsDef = new ArrayList<>();
        fieldsDef.add("\t@Autowired\n");
        fieldsDef.add("\tprivate " + super.getClassName() + "DAO dao;\n\n");
        fieldsDef.add("\t@Autowired\n");
        fieldsDef.add("\tprivate ApplicationContext appContext;\n\n");

        return fieldsDef;
    }

    @Override
    public List<String> getMethods() {
        List<String> methodsDef = new ArrayList<>();
        String className = super.getClassName();
        String varName = className.substring(0, 1).toLowerCase() + className.substring(1);

        methodsDef.add("\t@GetMapping(" + "\"" + "/" + tableName + "/r0" + "\"" + ")\n");
        methodsDef.add("\tpublic ResponseEntity findAll() {\n");
        methodsDef.add("\t\tList<" + className + "> lista" + className + "=dao.findAll();\n");
        methodsDef.add("\t\tif(lista" + className + "== null) {\n");
        methodsDef.add("\t\t\treturn new ResponseEntity(null, HttpStatus.NOT_FOUND);\n");
        methodsDef.add("\t\t}\n");
        methodsDef.add("\t\treturn new ResponseEntity(lista" + className + ", HttpStatus.OK);\n");
        methodsDef.add("\t}\n\n");

        methodsDef.add("\t@GetMapping(" + "\"" + "/" + tableName + "/r1/{id}" + "\"" + ")\n");
        if (primaryKey != null) {
            methodsDef.add("\tpublic ResponseEntity findBy" + toCamelCase(primaryKey.getColumnName(), true) + "(@PathVariable(" + "\"" + "id" + "\"" + ") Integer " + toCamelCase(primaryKey.getColumnName(), false) + ") {\n");
            methodsDef.add("\t\t" + className + " " + varName + " = dao.findBy" + toCamelCase(primaryKey.getColumnName(), true) + "(" + toCamelCase(primaryKey.getColumnName(), false) + ");\n");
        }
        methodsDef.add("\t\tif(" + varName + "== null) {\n");
        methodsDef.add("\t\t\treturn new ResponseEntity(null, HttpStatus.NOT_FOUND);\n");
        methodsDef.add("\t\t}\n");
        methodsDef.add("\t\treturn new ResponseEntity(" + varName + ", HttpStatus.OK);\n");
        methodsDef.add("\t}\n\n");

        return methodsDef;
    }

    @Override
    public String getLastLine() {
        return "}\n";
    }
}
