/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.commons.utils.NumberUtils;
import org.softcaster.easy_import.beans.Asset_class;
import org.softcaster.easy_import.beans.Asset_classDAO;
import org.softcaster.easy_import.beans.Currency_pairDAO;
import org.softcaster.easy_import.beans.Instrument_quote;
import org.softcaster.easy_import.beans.Instrument_quoteDAO;
import org.softcaster.easy_import.beans.Master_data;
import org.softcaster.easy_import.beans.Master_dataDAO;
import org.softcaster.easy_pricer.Currency_pair;
import org.softcaster.marketdataprovider.REQUEST_TYPE;
import org.softcaster.marketdataprovider.euronext.EuroNextProvider;

/**
 *
 * @author ep
 */
public class InstrumentQuoteImportMgr implements IImportMgr {

    private static InstrumentQuoteImportMgr _instance = null;

    // DAO
    private Asset_classDAO asset_classDAO = null;
    private Master_dataDAO masterDataDAO = null;
    private Instrument_quoteDAO instrument_quoteDAO = null;
    private Currency_pairDAO currency_pairDAO = null;

    // Bean
    private Asset_class asset_class = null;
    private Instrument_quote instrument_quote = null;
    private Currency_pair currency_pair = null;

    EuroNextProvider provider = null;

    private InstrumentQuoteImportMgr() {
        createDAOs();
        createBeans();
        provider = EuroNextProvider.getInstance();
    }

    private void createDAOs() {
        asset_classDAO = new Asset_classDAO();
        masterDataDAO = new Master_dataDAO();
        instrument_quoteDAO = new Instrument_quoteDAO();
        currency_pairDAO = new Currency_pairDAO();
    }

    private void createBeans() {
        asset_class = new Asset_class();
        instrument_quote = new Instrument_quote();
        currency_pair = new Currency_pair();
    }

    private void saveRecord(Master_data record) {
        if (record != null) {
            asset_class.setId_asset_class(record.getAsset_class());
            asset_classDAO.loadByPKey(asset_class);
            double quotation = 0;
            switch (asset_class.getCode()) {
                case "FRN", "XRN", "FRB", "XRB", "BLL" -> {
                    quotation = provider.getBondQuote(record.getCode(), REQUEST_TYPE.MIDDLE);
                }
                case "BFU" -> {
                    quotation = provider.getFutureQuote(record.getCode(), REQUEST_TYPE.MIDDLE);
                }
                default -> {
                }
            }
            if (!NumberUtils.isZero(quotation)) {
                instrument_quote.setCode(record.getCode());
                instrument_quote.setBid(quotation);
                instrument_quote.setAsk(quotation);
                instrument_quote.setMaster_data(record.getId_master_data());
                instrument_quoteDAO.insertOrUpdate(instrument_quote);
            }
        }
    }

    private void saveRecord(Currency_pair record) {
        if (record != null) {
            double quotation = provider.getForexQuote(record.getCode().substring(0, 3), record.getCode().substring(3, 6), REQUEST_TYPE.MIDDLE);
            record.setAsk(quotation);
            record.setBid(quotation);
            currency_pairDAO.insertOrUpdate(record);
        }
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        try {
            List<Master_data> records = masterDataDAO.loadRecordList("", null);
            List<Currency_pair> currencies = currency_pairDAO.loadRecordList("", null);
            int progress = (records.size() + currencies.size()) / 10;
            if (progress == 0) {
                progress = records.size() + currencies.size();
            }
            progress = 100 / progress;
            int cnt = 1;

            for (Master_data record : records) {
                saveRecord(record);

                if (progressInfo != null) {
                    progressInfo.setProgress(progress + cnt);
                    cnt++;
                }
            }

            for (Currency_pair record : currencies) {
                saveRecord(record);

                if (progressInfo != null) {
                    progressInfo.setProgress(progress + cnt);
                    cnt++;
                }
            }
        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    @Override
    public void terminate() {
        if (asset_classDAO != null) {
            asset_classDAO.closeStatements();
        }
        if (masterDataDAO != null) {
            masterDataDAO.closeStatements();
        }
        if (instrument_quoteDAO != null) {
            instrument_quoteDAO.closeStatements();
        }
    }

    public static InstrumentQuoteImportMgr getInstance() {
        if (_instance == null) {
            _instance = new InstrumentQuoteImportMgr();
        }
        return _instance;
    }
}
