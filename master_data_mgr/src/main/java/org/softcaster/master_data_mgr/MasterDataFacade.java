/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr;

import org.softcaster.easy_pricer_core.data.AmortizationSchedule;
import org.softcaster.easy_pricer_core.data.AmortizationScheduleDAO;
import org.softcaster.easy_pricer_core.data.AssetClass;
import org.softcaster.easy_pricer_core.data.AssetClassDAO;
import org.softcaster.easy_pricer_core.data.BondFutureMasterDataDAO;
import org.softcaster.easy_pricer_core.data.CurrencyDAO;
import org.softcaster.easy_pricer_core.data.Daycount;
import org.softcaster.easy_pricer_core.data.DaycountDAO;
import org.softcaster.easy_pricer_core.data.Form;
import org.softcaster.easy_pricer_core.data.FormDAO;
import org.softcaster.easy_pricer_core.data.Frequency;
import org.softcaster.easy_pricer_core.data.FrequencyDAO;
import org.softcaster.easy_pricer_core.data.IssuerDAO;
import org.softcaster.easy_pricer_core.data.RollConvention;
import org.softcaster.easy_pricer_core.data.RollConventionDAO;
import org.softcaster.easy_pricer_core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_core.data.SettlementTypeDAO;
import org.softcaster.easy_pricer_core.data.TypeOfInterest;
import org.softcaster.easy_pricer_core.data.TypeOfInterestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service
public class MasterDataFacade {

    @Autowired
    private SecurityMasterDataDAO securityMasterDataDAO;
    @Autowired
    private BondFutureMasterDataDAO bondFutureMasterDataDAO;
    @Autowired
    private IssuerDAO issuerDAO;

    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private DaycountDAO daycountDAO;
    @Autowired
    private SettlementTypeDAO settlementTypeDAO;
    @Autowired
    private TypeOfInterestDAO typeOfInterestDAO;
    @Autowired
    private FormDAO formDAO;
    @Autowired
    private FrequencyDAO frequencyDAO;
    @Autowired
    private RollConventionDAO rollConventionDAO;
    @Autowired
    private AmortizationScheduleDAO amortizationScheduleDAO;
    @Autowired
    private AssetClassDAO assetClassDAO;

    /**
    * @return the securityMasterDataDAO
    */

    public SecurityMasterDataDAO getSecurityMasterDataDAO() {
        return securityMasterDataDAO;
    }

    /**
     * @return the currencyDAO
     */
    public CurrencyDAO getCurrencyDAO() {
        return currencyDAO;
    }

    /**
     * @return the bondFutureMasterDataDAO
     */
    public BondFutureMasterDataDAO getBondFutureMasterDataDAO() {
        return bondFutureMasterDataDAO;
    }

    /**
     * @return the daycountDAO
     */
    public DaycountDAO getDaycountDAO() {
        return daycountDAO;
    }

    /**
     * @return the settlementTypeDAO
     */
    public SettlementTypeDAO getSettlementTypeDAO() {
        return settlementTypeDAO;
    }

    /**
     * @return the typeOfInterestDAO
     */
    public TypeOfInterestDAO getTypeOfInterestDAO() {
        return typeOfInterestDAO;
    }

    /**
     * @return the formDAO
     */
    public FormDAO getFormDAO() {
        return formDAO;
    }

    /**
     * @return the frequencyDAO
     */
    public FrequencyDAO getFrequencyDAO() {
        return frequencyDAO;
    }

    /**
     * @return the rollConventionDAO
     */
    public RollConventionDAO getRollConventionDAO() {
        return rollConventionDAO;
    }

    /**
     * @return the amortizationScheduleDAO
     */
    public AmortizationScheduleDAO getAmortizationScheduleDAO() {
        return amortizationScheduleDAO;
    }

    /**
     * @return the assetClassDAO
     */
    public AssetClassDAO getAssetClassDAO() {
        return assetClassDAO;
    }

    public TypeOfInterest findTypeOfInterest(String code) {
        return typeOfInterestDAO.findByCode(code);
    }

    public Daycount findDaycount(String code) {
        return daycountDAO.findByCode(code);
    }
    
    public Form  findForm(String code) {
        return formDAO.findByCode(code);
    }

    public Frequency  findFrequency(String code) {
        return frequencyDAO.findByCode(code);
    }

    public RollConvention  findRollConvention(String code) {
        return rollConventionDAO.findByCode(code);
    }

    public AmortizationSchedule  findAmortizationSchedule(String code) {
        return amortizationScheduleDAO.findByCode(code);
    }

    public AssetClass  findAssetClass(String code) {
        return assetClassDAO.findByCode(code);
    }

    /**
     * @return the issuerDAO
     */
    public IssuerDAO getIssuerDAO() {
        return issuerDAO;
    }

    /**
     * @param issuerDAO the issuerDAO to set
     */
    public void setIssuerDAO(IssuerDAO issuerDAO) {
        this.issuerDAO = issuerDAO;
    }
    
    
}
