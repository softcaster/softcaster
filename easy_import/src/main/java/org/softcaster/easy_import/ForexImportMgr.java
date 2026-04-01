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
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.LoggerMgr;
import static org.softcaster.easy_import.IImportMgr.IMPORT_PATH;
import org.softcaster.easy_import.beans.Accrual_schedule_type;
import org.softcaster.easy_import.beans.Accrual_schedule_typeDAO;
import org.softcaster.easy_import.beans.Amortization_schedule;
import org.softcaster.easy_import.beans.Amortization_scheduleDAO;
import org.softcaster.easy_import.beans.Asset_class;
import org.softcaster.easy_import.beans.Asset_classDAO;
import org.softcaster.easy_import.beans.CountryDAO;
import org.softcaster.easy_import.beans.Currency;
import org.softcaster.easy_import.beans.CurrencyDAO;
import org.softcaster.easy_import.beans.Forex_master_data;
import org.softcaster.easy_import.beans.Forex_master_dataDAO;
import org.softcaster.easy_import.beans.Daycount;
import org.softcaster.easy_import.beans.DaycountDAO;
import org.softcaster.easy_import.beans.Form;
import org.softcaster.easy_import.beans.FormDAO;
import org.softcaster.easy_import.beans.Frequency;
import org.softcaster.easy_import.beans.FrequencyDAO;
import org.softcaster.easy_import.beans.Issuer;
import org.softcaster.easy_import.beans.IssuerDAO;
import org.softcaster.easy_import.beans.ItemCurrencyPair;
import org.softcaster.easy_import.beans.Master_data;
import org.softcaster.easy_import.beans.Master_dataDAO;
import org.softcaster.easy_import.beans.Roll_convention;
import org.softcaster.easy_import.beans.Roll_conventionDAO;
import org.softcaster.easy_import.beans.Settlement_type;
import org.softcaster.easy_import.beans.Settlement_typeDAO;
import org.softcaster.easy_import.beans.Type_of_interest;
import org.softcaster.easy_import.beans.Type_of_interestDAO;

/**
 *
 * @author softc
 */
public class ForexImportMgr implements IImportMgr {

    private static ForexImportMgr _instance = null;

    // DAO
    private CurrencyDAO currencyDAO = null;
    private CountryDAO countryDAO = null;
    private IssuerDAO issuerDAO = null;
    private Type_of_interestDAO toiDAO = null;
    private FormDAO formDAO = null;
    private DaycountDAO daycountDAO = null;
    private Roll_conventionDAO rollConvDAO = null;
    private Accrual_schedule_typeDAO astDAO = null;
    private FrequencyDAO frequencyDAO = null;
    private Asset_classDAO asset_classDAO = null;
    private Settlement_typeDAO settlement_typeDAO = null;
    private Amortization_schedule amortization_schedule = null;
    private Master_dataDAO masterDataDAO = null;
    private Forex_master_dataDAO forex_master_dataDAO = null;

    // Bean
    private Currency currency = null;
    private Issuer issuer = null;
    private Type_of_interest toi = null;
    private Form form = null;
    private Daycount daycount = null;
    private Roll_convention rollConv = null;
    private Accrual_schedule_type ast = null;
    private Frequency frequency = null;
    private Asset_class asset_class = null;
    private Settlement_type settlement_type = null;
    private Amortization_scheduleDAO amortization_scheduleDAO = null;
    private Master_data masterData = null;
    private Forex_master_data forex_master_data = null;

    private void createDAOs() {
        currencyDAO = new CurrencyDAO();
        countryDAO = new CountryDAO();
        issuerDAO = new IssuerDAO();
        toiDAO = new Type_of_interestDAO();
        formDAO = new FormDAO();
        daycountDAO = new DaycountDAO();
        rollConvDAO = new Roll_conventionDAO();
        astDAO = new Accrual_schedule_typeDAO();
        frequencyDAO = new FrequencyDAO();
        asset_classDAO = new Asset_classDAO();
        settlement_typeDAO = new Settlement_typeDAO();
        amortization_scheduleDAO = new Amortization_scheduleDAO();
        masterDataDAO = new Master_dataDAO();
        forex_master_dataDAO = new Forex_master_dataDAO();
    }

    private void createBeans() {
        currency = new Currency();
        issuer = new Issuer();

        // Bean predefiniti        
        toi = new Type_of_interest();
        toi.setCode("FIXED");
        toiDAO.loadByIdx(toi);

        form = new Form();
        form.setCode("BEARER");
        formDAO.loadByIdx(form);

        daycount = new Daycount();
        daycount.setCode("ACT_ACT");
        daycountDAO.loadByIdx(daycount);

        rollConv = new Roll_convention();
        rollConv.setCode("RC_NONE");
        rollConvDAO.loadByIdx(rollConv);

        ast = new Accrual_schedule_type();
        ast.setCode("AST_NONE");
        astDAO.loadByIdx(ast);

        frequency = new Frequency();
        frequency.setCode("CUSTOM");
        frequencyDAO.loadByIdx(frequency);

        asset_class = new Asset_class();
        asset_class.setCode("BFU");
        asset_classDAO.loadByIdx(asset_class);

        settlement_type = new Settlement_type();
        settlement_type.setCode("CASH");
        settlement_typeDAO.loadByIdx(settlement_type);

        amortization_schedule = new Amortization_schedule();
        amortization_schedule.setCode("IOL");
        amortization_scheduleDAO.loadByIdx(amortization_schedule);
    }

    public static ForexImportMgr getInstance() {
        if (_instance == null) {
            _instance = new ForexImportMgr();
        }
        return _instance;
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/curr_pair.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        ItemCurrencyPair itemCurrencyPair = new ItemCurrencyPair();
        try {
            csvImport.startImport(config);
            for (String[] s : csvImport.getBuffer()) {
                if (s[0].isEmpty()) {
                    System.out.println("Error: " + s[0].trim());
                    continue;
                }
                itemCurrencyPair.bcy = s[0].trim();
                itemCurrencyPair.ccy = s[1].trim();
                itemCurrencyPair.bcy_irc = s[2].trim();
                itemCurrencyPair.ccy_irc = s[3].trim();
                saveRecord(itemCurrencyPair);
            }

        } catch (Exception ex) {
            String error = "Error Currency Pair: " + itemCurrencyPair.bcy + "/" + itemCurrencyPair.ccy + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
        if (currencyDAO != null) {
            currencyDAO.closeStatements();
        }

        if (countryDAO != null) {
            countryDAO.closeStatements();
        }

        if (formDAO != null) {
            formDAO.closeStatements();
        }

        if (daycountDAO != null) {
            daycountDAO.closeStatements();
        }

        if (rollConvDAO != null) {
            rollConvDAO.closeStatements();
        }

        if (astDAO != null) {
            astDAO.closeStatements();
        }

        if (frequencyDAO != null) {
            frequencyDAO.closeStatements();
        }

        if (toiDAO != null) {
            toiDAO.closeStatements();
        }

        if (issuerDAO != null) {
            issuerDAO.closeStatements();
        }

        if (masterDataDAO != null) {
            masterDataDAO.closeStatements();
        }

        if (asset_classDAO != null) {
            asset_classDAO.closeStatements();
        }

        if (settlement_typeDAO != null) {
            settlement_typeDAO.closeStatements();
        }

        if (amortization_scheduleDAO != null) {
            amortization_scheduleDAO.closeStatements();
        }

        if (masterDataDAO != null) {
            masterDataDAO.closeStatements();
        }

        if (forex_master_dataDAO != null) {
            forex_master_dataDAO.closeStatements();
        }

        LoggerMgr.logInfo("Import terminated");
    }

    private ForexImportMgr() {
        createDAOs();
        createBeans();
    }

    private boolean saveRecord(ItemCurrencyPair itemCurrencyPair) {
        try {
            currency.setIso_code(itemCurrencyPair.bcy);
            currencyDAO.loadByIsoCode(currency);

            issuer.setShort_issuer_name("MARKET");
            issuerDAO.loadByCode(issuer);

            // Salvo testata
            masterData = new Master_data();
            masterData.setAccrual_schedule_type(ast.getId_accrual_schedule_type());
            masterData.setBusiness_days(2);
            masterData.setCalendar(currency.getCalendar());
            masterData.setCurrency(currency.getId_currency());
            masterData.setDaycount(daycount.getId_daycount());
            masterData.setForm(form.getId_form());
            masterData.setInterest_rate(0.);
            masterData.setIssue_date(new Date().sqlDate());
            masterData.setIssue_price(0.);
            masterData.setMaturity_date(new Date().sqlDate());
            masterData.setRedempion_price(0.);
            masterData.setRoll_convention(rollConv.getId_roll_convention());
            masterData.setType_of_interest(toi.getId_type_of_interest());
            masterData.setFrequency(frequency.getId_frequency());
            masterData.setAsset_class(asset_class.getId_asset_class());
            masterData.setAmortization_schedule(amortization_schedule.getId_amortization_schedule());
            masterData.setCode(itemCurrencyPair.bcy + itemCurrencyPair.ccy);
            masterDataDAO.insertOrUpdate(masterData);
            masterDataDAO.loadByIdx(masterData);

            // Salvo dettaglio currency pair           
            forex_master_data = new Forex_master_data();
            forex_master_data.setId_master_data(masterData.getId_master_data());
            forex_master_data.setBcy(currency.getId_currency());
            currency.setIso_code(itemCurrencyPair.ccy);
            currencyDAO.loadByIsoCode(currency);
            forex_master_data.setCcy(currency.getId_currency());
            forex_master_data.setBcy_irc(itemCurrencyPair.bcy_irc);
            forex_master_data.setCcy_irc(itemCurrencyPair.ccy_irc);
            forex_master_dataDAO.insertOrUpdate(forex_master_data);

            return true;
        } catch (Exception ex) {
            String error = "Error importing Currency Pair: " + itemCurrencyPair.bcy + "/" + itemCurrencyPair.ccy + " function saveRecord - error: [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            return false;
        }
    }

}
