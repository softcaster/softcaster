/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import org.softcaster.commons.xml.ParamsMgr;

/**
 *
 * @author emy
 */
public class Generator {

    private BeanGenerator beanGenerator = null;
    private RepositoryGenerator repositoryGenerator = null;
    private DaoGenerator daoGenerator = null;
    private ControllerGenerator controllerGenerator = null;

    private void createFilesUtils(String tableName) {
        beanGenerator = new BeanGenerator(tableName);
        repositoryGenerator = new RepositoryGenerator(tableName);
        daoGenerator = new DaoGenerator(tableName);
        controllerGenerator = new ControllerGenerator(tableName);
    }

    public void setTableName(String tableName) {
        beanGenerator = null;
        repositoryGenerator = null;
        daoGenerator = null;
        controllerGenerator = null;

        beanGenerator = new BeanGenerator(tableName);
        repositoryGenerator = new RepositoryGenerator(tableName);
        daoGenerator = new DaoGenerator(tableName);
        controllerGenerator = new ControllerGenerator(tableName);
    }

    public Vector<String> getTableList() throws SQLException {
        Vector<String> tableList = new Vector<>();
        MetaDataMgr metaDataMgr = new MetaDataMgr();
        Iterator<String> it = metaDataMgr.getTables().iterator();
        while (it.hasNext()) {
            tableList.add(it.next());
        }
        return tableList;
    }

    public void writeFiles(String tableName) throws IOException, SQLException {
        String dataPackageString = "";
        String controllerPackageString = "";

        ParamsMgr paramsMgr = ParamsMgr.getInstance();
        if (paramsMgr != null) {
            dataPackageString = paramsMgr.getParamValue("DATA_PACKAGE");
            controllerPackageString = paramsMgr.getParamValue("CONTROLLER_PACKAGE");
        }

        createFilesUtils(tableName);

        writeFileData(dataPackageString);
        writeFileRepository(dataPackageString);
        writeFileDao(dataPackageString);
        writeFileController(controllerPackageString);

        controllerGenerator.disconnect();
    }

    public void writeFile(String packageString, IFileGenerator generator) throws IOException {
        String fileName = generator.getFileName();
        FileWriter fw = new FileWriter(fileName);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(packageString);
            bw.write("\n");
            bw.write("\n");

            Iterator<String> it;
            if (generator.getImportClasses() != null) {
                it = generator.getImportClasses().iterator();
                while (it.hasNext()) {
                    String ouput = it.next();
                    bw.write(ouput);
                }
            }

            if (generator.getAnnotations() != null) {
                it = generator.getAnnotations().iterator();
                while (it.hasNext()) {
                    String ouput = it.next();
                    bw.write(ouput);
                }
            }

            bw.write(generator.getClassDef());

            if (generator.getFields() != null) {
                it = generator.getFields().iterator();
                while (it.hasNext()) {
                    String ouput = it.next();
                    bw.write(ouput);
                }
            }

            if (generator.getMethods() != null) {
                it = generator.getMethods().iterator();
                while (it.hasNext()) {
                    String ouput = it.next();
                    bw.write(ouput);
                }
            }

            bw.write(generator.getLastLine());
        }
    }

    public void disconnect() throws SQLException {
        beanGenerator.disconnect();
    }

    public void writeFileData(String packageString) throws IOException {
        writeFile(packageString, beanGenerator);
    }

    public void writeFileRepository(String packageString) throws IOException {
        writeFile(packageString, repositoryGenerator);
    }

    public void writeFileDao(String packageString) throws IOException {
        writeFile(packageString, daoGenerator);
    }

    public void writeFileController(String packageString) throws IOException {
        writeFile(packageString, controllerGenerator);
    }

    public void writeFileJavaScript() throws IOException, SQLException {
        JavascriptGenerator generator = new JavascriptGenerator();
        String fileName = generator.getFileName();
        FileWriter fw = new FileWriter(fileName);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            MetaDataMgr metaDataMgr = new MetaDataMgr();
            Iterator<String> itt = metaDataMgr.getTables().iterator();
            while (itt.hasNext()) {
                generator.setTableName(itt.next());
                Iterator<String> it;
                if (generator.getImportClasses() != null) {
                    it = generator.getImportClasses().iterator();
                    while (it.hasNext()) {
                        String ouput = it.next();
                        bw.write(ouput);
                    }
                }

                if (generator.getAnnotations() != null) {
                    it = generator.getAnnotations().iterator();
                    while (it.hasNext()) {
                        String ouput = it.next();
                        bw.write(ouput);
                    }
                }

                bw.write(generator.getClassDef());

                if (generator.getFields() != null) {
                    it = generator.getFields().iterator();
                    while (it.hasNext()) {
                        String ouput = it.next();
                        bw.write(ouput);
                    }
                }

                if (generator.getMethods() != null) {
                    it = generator.getMethods().iterator();
                    while (it.hasNext()) {
                        String ouput = it.next();
                        bw.write(ouput);
                    }
                }
                bw.write(generator.getLastLine());
                bw.write("\n");
            }
        }
        generator.disconnect();
    }
}
