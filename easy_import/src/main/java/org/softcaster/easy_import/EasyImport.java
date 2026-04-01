/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.python.util.PythonInterpreter;
import org.softcaster.easy_import.beans.Master_data;
import org.softcaster.easy_import.beans.Master_dataDAO;

/**
 *
 * @author ep
 */
public class EasyImport {
    
    private static IImportMgr getImportMgr(String importMgrName) {
        IImportMgr importMgr = null;
        switch (importMgrName) {
            case "currencies" -> importMgr = CurrencyImportMgr.getInstance();
            case "countries" -> importMgr = CountryImportMgr.getInstance();
            case "bonds" -> importMgr = BondImportMgr.getInstance();
            default -> {
            }
        }
        
        return importMgr;
    }
    
    public static void main(String[] args) throws IOException {

        File conf = new File(System.getProperty("user.dir") + "//conf//log4j.conf");
        System.setProperty("log4j.configuration", "file:" + conf);
        
        // Inizializzazione PythonPath da farsi prima di ogni utilizzo dell'interprete
        String pythonPath = System.getProperty("user.dir") + "\\scripts";
        Properties props = new Properties();
        props.setProperty("python.path", pythonPath);
        PythonInterpreter.initialize(System.getProperties(), props, new String[]{""});

        //BotImportMgr bim = new BotImportMgr();
        //bim.dumpBotXml();
        //UsaYcImportMgr importMgr = UsaYcImportMgr.getInstance();
        //importMgr.start(null);
        
        Master_dataDAO dao = new Master_dataDAO();
        List<Master_data> records = dao.loadRecordList("",null);
        for(Master_data record: records) {
            System.out.println(record.getCode() + "\t" + record.getAsset_class());
        }
        
    }
}
