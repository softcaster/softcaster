/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_import;

/**
 *
 * @author ep
 */
public interface IImportMgr {
    public static final String IMPORT_PATH = System.getProperty("user.dir") + "/import";

    public void start(IProgressInfo progressInfo);

    public void terminate();

    public static IImportMgr getInstance(){return null;};
    
    default String getImportInfo() {
        return "";
    } 
}
