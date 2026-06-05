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
import org.softcaster.commons.types.DateParser;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import static org.softcaster.easy_import.IImportMgr.IMPORT_PATH;
import org.softcaster.easy_import.beans.Accrual_schedule_type;
import org.softcaster.easy_import.beans.Accrual_schedule_typeDAO;
import org.softcaster.easy_import.beans.Amortization_schedule;
import org.softcaster.easy_import.beans.Amortization_scheduleDAO;
import org.softcaster.easy_import.beans.Asset_class;
import org.softcaster.easy_import.beans.Asset_classDAO;
import org.softcaster.easy_import.beans.Bond_future_master_data;
import org.softcaster.easy_import.beans.Bond_future_master_dataDAO;
import org.softcaster.easy_import.beans.Country;
import org.softcaster.easy_import.beans.CountryDAO;
import org.softcaster.easy_import.beans.Currency;
import org.softcaster.easy_import.beans.CurrencyDAO;
import org.softcaster.easy_import.beans.Daycount;
import org.softcaster.easy_import.beans.DaycountDAO;
import org.softcaster.easy_import.beans.Form;
import org.softcaster.easy_import.beans.FormDAO;
import org.softcaster.easy_import.beans.Frequency;
import org.softcaster.easy_import.beans.FrequencyDAO;
import org.softcaster.easy_import.beans.Future_master_data;
import org.softcaster.easy_import.beans.Future_master_dataDAO;
import org.softcaster.easy_import.beans.Issuer;
import org.softcaster.easy_import.beans.IssuerDAO;
import org.softcaster.easy_import.beans.ItemFutBond;
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
public class BondFutImportMgr implements IImportMgr {

    private static BondFutImportMgr _instance = null;

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
    private Future_master_dataDAO future_master_dataDAO = null;
    private Bond_future_master_dataDAO bond_future_master_dataDAO = null;

    // Bean
    private Currency currency = null;
    private Country country = null;
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
    private Future_master_data future_master_data = null;
    private Bond_future_master_data bond_future_master_data = null;

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
        future_master_dataDAO = new Future_master_dataDAO();
        bond_future_master_dataDAO = new Bond_future_master_dataDAO();
    }

    private void createBeans() {
        currency = new Currency();
        country = new Country();
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

    public static BondFutImportMgr getInstance() {
        if (_instance == null) {
            _instance = new BondFutImportMgr();
        }
        return _instance;
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        CsvImport csvImport = new CsvImport();
        Path path = Paths.get(IMPORT_PATH + "/fut_bonds.csv");

        ImportConfig config = new ImportConfig();
        config.setSeparator(',');
        config.setFilePath(path);
        config.setStartData(0);
        config.setCharset(StandardCharsets.UTF_8); // utf-8
        ItemFutBond itemFutBond = new ItemFutBond();
        try {
            csvImport.startImport(config);
            DateParser parser = null;
            Date dt = null;
            for (String[] s : csvImport.getBuffer()) {
                if (s[0].isEmpty()) {
                    System.out.println("Error: " + s[0].trim());
                    continue;
                }
                itemFutBond.isincode = s[0].trim();
                itemFutBond.description = s[1].trim();
                itemFutBond.underliyngIsincode = s[2].trim();
                itemFutBond.issuer = s[3].trim();
                itemFutBond.currency = s[4].trim();
                itemFutBond.calendar = s[5].trim();
                parser = new DateParser(s[6].trim());
                dt = new Date(parser.year(), parser.month(), parser.day());
                itemFutBond.issuedate = dt.sqlDate();
                itemFutBond.issueprice = Converter.toDouble(s[7].trim(), true);
                parser = new DateParser(s[8].trim());
                dt = new Date(parser.year(), parser.month(), parser.day());
                itemFutBond.redemptiondate = dt.sqlDate();
                itemFutBond.redemptionprice = Converter.toDouble(s[9].trim(), true);
                itemFutBond.tickSize = Converter.toDouble(s[10].trim(), true);
                itemFutBond.initialMargin = Converter.toDouble(s[11].trim(), true);
                itemFutBond.taxrate = Converter.toDouble(s[12].trim(), false);
                itemFutBond.contractValue = Converter.toDouble(s[13].trim(), true);
                saveRecord(itemFutBond);
            }

        } catch (Exception ex) {
            String error = "Error importing Future: " + itemFutBond.isincode + " [" + ex.getLocalizedMessage() + "]";
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

        if (future_master_dataDAO != null) {
            future_master_dataDAO.closeStatements();
        }

        if (bond_future_master_dataDAO != null) {
            bond_future_master_dataDAO.closeStatements();
        }

        if (bond_future_master_dataDAO != null) {
            bond_future_master_dataDAO.closeStatements();
        }

        LoggerMgr.logInfo("Import terminated");
    }

    private BondFutImportMgr() {
        createDAOs();
        createBeans();
    }

    private boolean saveRecord(ItemFutBond futBond) {
        try {
            currency.setIso_code(futBond.currency);
            currencyDAO.loadByIsoCode(currency);

            country.setAlfa_3_code(futBond.calendar);
            countryDAO.loadByAlfa3Code(country);

            issuer.setShort_issuer_name(futBond.issuer);
            issuerDAO.loadByCode(issuer);

            // Salvo testata
            masterData = new Master_data();
            masterData.setAccrual_schedule_type(ast.getId_accrual_schedule_type());
            masterData.setBusiness_days(2);
            masterData.setCurrency(currency.getId_currency());
            masterData.setDaycount(daycount.getId_daycount());
            masterData.setForm(form.getId_form());
            masterData.setInterest_rate(0.);
            masterData.setIssue_date(futBond.issuedate);
            masterData.setIssue_price(futBond.issueprice);
            masterData.setMaturity_date(futBond.redemptiondate);
            masterData.setRedempion_price(futBond.redemptionprice);
            masterData.setRoll_convention(rollConv.getId_roll_convention());
            masterData.setType_of_interest(toi.getId_type_of_interest());
            masterData.setFrequency(frequency.getId_frequency());
            masterData.setAsset_class(asset_class.getId_asset_class());
            masterData.setAmortization_schedule(amortization_schedule.getId_amortization_schedule());
            masterData.setCode(futBond.isincode);
            masterDataDAO.insertOrUpdate(masterData);
            masterDataDAO.loadByIdx(masterData);

            // Salvo dettaglio Future           
            future_master_data = new Future_master_data();
            future_master_data.setId_master_data(masterData.getId_master_data());
            future_master_data.setIsin(futBond.isincode);
            future_master_data.setDescription(futBond.description);
            future_master_data.setSettlement_type(settlement_type.getId_settlement_type());
            future_master_dataDAO.insertOrUpdate(future_master_data);

            // Salvo Dettaglio Future Bond
            bond_future_master_data = new Bond_future_master_data();
            bond_future_master_data.setId_master_data(masterData.getId_master_data());
            bond_future_master_data.setContract_value(futBond.contractValue);
            bond_future_master_data.setInitial_margin(futBond.initialMargin);
            bond_future_master_data.setTick_size(futBond.tickSize);
            bond_future_master_dataDAO.insertOrUpdate(bond_future_master_data);

            return true;
        } catch (Exception ex) {
            String error = "Error importing Bond: " + futBond.isincode + " function saveRecord - error: [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            return false;
        }
    }

}
