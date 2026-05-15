/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.softcaster.commons.imports.CsvImport;
import org.softcaster.commons.imports.ImportConfig;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service("Countries")
public class CountryImportMgr implements IImportMgr {

    @Autowired
    private org.softcaster.core.data.CountryDAO dao;
    @Autowired
    private org.softcaster.core.data.CurrencyDAO currencyDAO;
    @Autowired
    private org.softcaster.core.data.CalendarDAO calendarDAO;

    public CountryImportMgr() {
    }

    @Override
    @Async("importTaskExecutor")
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/countries.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8

        org.softcaster.core.data.Currency currency = currencyDAO.findByIsoCode("EUR");
        org.softcaster.core.data.Calendar calendar = calendarDAO.findByCode("EUR");

        Country country = null;
        try {
            csvImport.startImport(config);
            List<String[]> rows = csvImport.getBuffer();
            int total = rows.size();
            int current = 0;
            for (String[] s : rows) {
                if (s[0].isEmpty()) {
                    System.out.println("Error: " + s[0].trim());
                    continue;
                }
                String alfa3Code = s[4].trim();
                country = dao.findByAlfa3Code(alfa3Code);
                if (country == null) {
                    country = new org.softcaster.core.data.Country();
                }

                country.setCountryNumericCode(Integer.valueOf(s[0].trim()).shortValue());
                country.setCountryName(s[1].trim());
                country.setOfficialStateName(s[2].trim());
                country.setAlfa2Code(s[3].trim());
                country.setAlfa3Code(alfa3Code);
                country.setSubdivisionCodeLinks("");
                country.setInternetCcTld("");
                country.setCurrency(currency);
                country.setCalendar(calendar);
                dao.saveOrUpdate(country);

                // 2. Aggiorna il progresso ogni X righe o calcola la percentuale
                int percent = (int) ((current / (double) total) * 100);
                progressInfo.updateProgress("Importing " + alfa3Code + " (" + current + "/" + total + ")", percent);
                current++;
            }

        } catch (Exception ex) {
            String error = "Error during import: " + ex.getLocalizedMessage();;
            LoggerMgr.logError(error);
            progressInfo.showError(error);
        } finally {
            progressInfo.updateProgress("Import terminated successfully", 100);
            terminate();
        }
    }

    @Override
    public void terminate() {
        LoggerMgr.logInfo("Import terminated");
    }

}
