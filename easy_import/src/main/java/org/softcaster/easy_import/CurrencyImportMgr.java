/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_pricer_core.data.Currency;
import org.softcaster.easy_pricer_core.data.CurrencyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component 
public class CurrencyImportMgr implements IImportMgr {

    @Autowired
    private CurrencyDAO dao; 
    
    public  CurrencyImportMgr() {
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/currencies.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8);

        Currency currency = null;
        try {
            csvImport.startImport(config);
            String isoCode="";
            for (String[] s : csvImport.getBuffer()) {
                isoCode = s[1].trim();
                currency = dao.findByIsoCode(isoCode);
                if(currency == null)
                    currency = new Currency();
                currency.setDescription(s[0].trim());
                currency.setIsoCode(s[1].trim());
                currency.setCurrencyNumericCode(Integer.valueOf(s[2].trim()).shortValue());
                currency.setMinorUnit(Integer.valueOf(s[3].trim()).shortValue());
                currency.setPhysicalCurr(Short.valueOf("1"));
                currency.setSystemCurr(Short.valueOf("0"));

                dao.saveOrUpdate(currency);
            }

        } catch (Exception ex) {
            String error = "Error importing Currency: " + currency.getIsoCode() + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        }
    }

    @Override
    public void terminate() {
        LoggerMgr.logInfo("Import terminated");
    }
}
