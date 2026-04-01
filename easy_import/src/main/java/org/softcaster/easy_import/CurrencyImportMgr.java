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
import org.softcaster.easy_import.beans.Currency;
import org.softcaster.easy_import.beans.CurrencyDAO;

/**
 *
 * @author ep
 */
public class CurrencyImportMgr implements IImportMgr {

    private static CurrencyImportMgr _instance = null;

    private CurrencyImportMgr() {
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

        Currency currency = new Currency();
        CurrencyDAO currencyDAO = new CurrencyDAO();
        try {
            csvImport.startImport(config);
            for (String[] s : csvImport.getBuffer()) {
                currency.setDescription(s[0].trim());
                currency.setIso_code(s[1].trim());
                currency.setCurrency_numeric_code(Integer.valueOf(s[2].trim()));
                currency.setMinor_unit(Integer.valueOf(s[3].trim()));
                currency.setPhysical_curr(1);
                currency.setSystem_curr(0);

                currencyDAO.insertOrUpdate(currency);
            }

        } catch (Exception ex) {
            String error = "Error importing Currency: " + currency.getIso_code() + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } finally {
            currencyDAO.closeStatements();
        }
    }

    @Override
    public void terminate() {
        LoggerMgr.logInfo("Import terminated");
    }

    public static CurrencyImportMgr getInstance() {
        if (_instance == null) {
            _instance = new CurrencyImportMgr();
        }
        return _instance;
    }
}
