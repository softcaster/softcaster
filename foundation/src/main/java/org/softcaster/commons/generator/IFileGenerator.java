/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.commons.generator;

import java.util.List;

/**
 *
 * @author emy
 */
public interface IFileGenerator {

    public List<String> getImportClasses();

    public List<String> getAnnotations();

    public String getClassDef();

    public String getClassName();

    public String getFileName();

    public List<String> getFields();

    public List<String> getMethods();

    public String getLastLine();
}
