/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.easy_import.beans.Cash_flow_item;
import org.softcaster.easy_import.beans.Cash_flow_itemDAO;
import org.softcaster.easy_import.beans.Daycount;
import org.softcaster.easy_import.beans.DaycountDAO;
import org.softcaster.easy_import.beans.Frequency;
import org.softcaster.easy_import.beans.FrequencyDAO;
import org.softcaster.easy_import.beans.Master_data;
import org.softcaster.easy_import.beans.Master_dataDAO;
import org.softcaster.easy_import.beans.Security_master_data;
import org.softcaster.easy_import.beans.Security_master_dataDAO;
import org.softcaster.easy_import.xml.BondLoaderMgr;
import org.softcaster.easy_import.xml.Coupon;
import org.softcaster.easy_import.xml.ItemBond;
import org.softcaster.engine.cashflow.BackwardScheduleGenerator;
import org.softcaster.engine.cashflow.BulletAmortizationStrategy;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.PaymentPeriod;

/**
 *
 * @author softc
 */
public class CashFlowImportMgr extends BondImportMgrHelper implements IImportMgr {

    private static CashFlowImportMgr _instance = null;

    // Beans
    Master_data master_data = null;
    private Daycount daycount = null;
    private Frequency frequency = null;
    private BackwardScheduleGenerator bsg = new BackwardScheduleGenerator();
    private BulletAmortizationStrategy bas = new BulletAmortizationStrategy();

    // DAOs 
    Master_dataDAO master_dataDAO = null;
    Security_master_dataDAO security_master_dataDAO = null;
    Cash_flow_itemDAO cashFlowItemDAO = null;
    private DaycountDAO daycountDAO = null;
    private FrequencyDAO frequencyDAO = null;

    private void createBeans() {
        master_data = new Master_data();
        daycount = new Daycount();
        frequency = new Frequency();
    }

    private void createDAOs() {
        master_dataDAO = new Master_dataDAO();
        security_master_dataDAO = new Security_master_dataDAO();
        cashFlowItemDAO = new Cash_flow_itemDAO();
        daycountDAO = new DaycountDAO();
        frequencyDAO = new FrequencyDAO();
    }

    private CashFlowImportMgr() {

        if (loader == null) {
            try {
                loader = BondLoaderMgr.getInstance();
                createBeans();
            } catch (FileNotFoundException | XMLStreamException ex) {
                String error = "Error creating ImportMgr: " + " [" + ex.getLocalizedMessage() + "]";
                LoggerMgr.logError(error);
                loader = null;
            }
        }
    }

    private boolean saveRecord(ItemBond bond) {

        master_data.setCode(bond.isincode);
        if (master_dataDAO.loadByIdx(master_data)) {

            frequency.setId_frequency(master_data.getFrequency());
            frequencyDAO.loadByPKey(frequency);

            daycount.setId_daycount(master_data.getDaycount());
            daycountDAO.loadByPKey(daycount);

            if (!bond.coupons.isEmpty()) {
                return saveCashFlow(bond.coupons);
            } else {
                return buildAndSaveCashFlow();
            }

        } else {
            return false;
        }
    }

    private boolean buildAndSaveCashFlow() {
        try {
            List<Cash_flow_item> cashFlows = new ArrayList<>();
            Security_master_data security_master_data = new Security_master_data();
            security_master_data.setIsin(master_data.getCode());
            security_master_dataDAO.loadByIdx(security_master_data);

            List<PaymentPeriod> periods = bsg.generate(master_data.getIssue_date().toLocalDate(),
                    master_data.getMaturity_date().toLocalDate(),
                    org.softcaster.engine.enums.Frequency.SEMI_ANNUAL,
                    org.softcaster.engine.enums.BusinessDayConvention.FORWARD,
                    org.softcaster.engine.enums.DaycountBasis.ACT_ACT_ICMA,
                    null);

            List<CashFlow> flows = bas.generateCashFlows(master_data.getIssue_price(), master_data.getInterest_rate(),
                    periods, org.softcaster.engine.enums.DaycountBasis.ACT_ACT_ICMA);

            for (CashFlow flow : flows) {
                Cash_flow_item item = new Cash_flow_item();
                item.setMaster_data(master_data.getId_master_data());
                item.setStart_date(java.sql.Date.valueOf(flow.accrualStart()));
                item.setEnd_date(java.sql.Date.valueOf(flow.accrualEnd()));
                item.setInterest(flow.interest());
                item.setAmount(flow.principal());
                cashFlows.add(item);
            }

            for (Cash_flow_item item : cashFlows) {
                cashFlowItemDAO.insertOrUpdate(item);
            }

            return true;
        } catch (Exception ex) {

            String error = "Error importing Bond's Cash Flows: " + master_data.getCode() + " function saveRecord - error: [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            return false;
        }
    }

    private boolean saveCashFlow(List<Coupon> coupons) {

        try {
            List<Cash_flow_item> cashFlows = new ArrayList<>();

            Date start = master_data.getIssue_date();
            Date end = null;

            for (Coupon coupon : coupons) {
                end = coupon.couponDate;
                Cash_flow_item item = new Cash_flow_item();
                item.setMaster_data(master_data.getId_master_data());
                item.setStart_date(start);
                item.setEnd_date(end);
                item.setInterest(coupon.couponValue);
                if (end == master_data.getMaturity_date()) {
                    item.setAmount(100.);
                }
                cashFlows.add(item);
                start = end;
            }
            for (Cash_flow_item item : cashFlows) {
                cashFlowItemDAO.insertOrUpdate(item);
            }
            return true;
        } catch (Exception ex) {
            String error = "Error importing Bond's Cash Flows: " + master_data.getCode() + " function saveRecord - error: [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
            return false;
        }
    }

    @Override
    public void start(IProgressInfo progressInfo) {
        String isin = "";
        createDAOs();
        try {
            // Lista Bond 
            List<String> items = getIsinList();
            int progress = items.size() / 10;
            if (progress == 0) {
                progress = items.size();
            }
            progress = 100 / progress;
            int cnt = 1;

            for (String item : items) {
                isin = item;

                // Carica dati bond dato isin
                ItemBond bond = getBondDataFromXML(isin);

                // Salva cash flow
                saveRecord(bond);
                if (progressInfo != null) {
                    progressInfo.setProgress(progress + cnt);
                    cnt++;
                }
            }

        } catch (XMLStreamException | IOException ex) {
            String error = "Error importing Bond: " + isin + " [" + ex.getLocalizedMessage() + "]";
            LoggerMgr.logError(error);
        } finally {
            terminate();
        }
    }

    @Override
    public void terminate() {
        if (master_dataDAO != null) {
            master_dataDAO.closeStatements();
            master_dataDAO = null;
        }

        if (security_master_dataDAO != null) {
            security_master_dataDAO.closeStatements();
            security_master_dataDAO = null;
        }

        if (cashFlowItemDAO != null) {
            cashFlowItemDAO.closeStatements();
            cashFlowItemDAO = null;
        }

        if (daycountDAO != null) {
            daycountDAO.closeStatements();
            daycountDAO = null;
        }

        if (frequencyDAO != null) {
            frequencyDAO.closeStatements();
            frequencyDAO = null;
        }
    }

    public static CashFlowImportMgr getInstance() {
        if (_instance == null) {
            _instance = new CashFlowImportMgr();
        }

        return _instance;
    }

}
