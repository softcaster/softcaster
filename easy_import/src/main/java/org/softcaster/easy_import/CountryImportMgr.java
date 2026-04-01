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
import org.softcaster.easy_import.beans.Calendar;
import org.softcaster.easy_import.beans.CalendarDAO;
import org.softcaster.easy_import.beans.Country;
import org.softcaster.easy_import.beans.CountryDAO;
import org.softcaster.easy_import.beans.Currency;
import org.softcaster.easy_import.beans.CurrencyDAO;

/**
 *
 * @author ep
 */
public class CountryImportMgr implements IImportMgr {

    private static CountryImportMgr _instance = null;

    private CountryImportMgr() {
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/countries.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8

        Currency currency = new Currency();
        currency.setCurrency_numeric_code(978);
        CurrencyDAO currencyDAO = new CurrencyDAO();
        currencyDAO.loadByIdx(currency);
        currencyDAO.closeStatements();

        Calendar calendar = new Calendar();
        calendar.setCode("EUR");
        CalendarDAO calendarDAO = new CalendarDAO();
        calendarDAO.loadByIdx(calendar);
        calendarDAO.closeStatements();

        Country country = new Country();
        CountryDAO countryDAO = new CountryDAO();
        try {
            csvImport.startImport(config);
            for (String[] s : csvImport.getBuffer()) {
                if (s[0].isEmpty()) {
                    System.out.println("Error: " + s[0].trim());
                    continue;
                }
                country.setCountry_numeric_code(Integer.valueOf(s[0].trim()));
                country.setCountry_name(s[1].trim());
                country.setOfficial_state_name(s[2].trim());
                country.setAlfa_2_code(s[3].trim());
                country.setAlfa_3_code(s[4].trim());
                country.setSubdivision_code_links("");
                country.setInternet_cc_tld("");
                country.setCurrency(currency.getId_currency());
                country.setCalendar(calendar.getId_calendar());
                countryDAO.insertOrUpdate(country);
            }

        } catch (Exception ex) {
            String error = "Error importing Country: " + country.getAlfa_3_code() + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } finally {
            countryDAO.closeStatements();
        }
    }

    @Override
    public void terminate() {
        LoggerMgr.logInfo("Import terminated");
    }

    public static CountryImportMgr getInstance() {
        if (_instance == null) {
            _instance = new CountryImportMgr();
        }
        return _instance;
    }
}
