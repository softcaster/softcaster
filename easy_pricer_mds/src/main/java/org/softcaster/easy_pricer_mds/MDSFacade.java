/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds;

import org.softcaster.easy_pricer_core.data.AmortizationSchedule;
import org.softcaster.easy_pricer_core.data.AmortizationScheduleDAO;
import org.softcaster.easy_pricer_core.data.AssetClass;
import org.softcaster.easy_pricer_core.data.AssetClassDAO;
import org.softcaster.easy_pricer_core.data.BondFutureMasterDataDAO;
import org.softcaster.easy_pricer_core.data.CounterpartyDAO;
import org.softcaster.easy_pricer_core.data.CounterpartyTypeDAO;
import org.softcaster.easy_pricer_core.data.CountryDAO;
import org.softcaster.easy_pricer_core.data.CurrencyDAO;
import org.softcaster.easy_pricer_core.data.Daycount;
import org.softcaster.easy_pricer_core.data.DaycountDAO;
import org.softcaster.easy_pricer_core.data.ForexMasterDataDAO;
import org.softcaster.easy_pricer_core.data.Form;
import org.softcaster.easy_pricer_core.data.FormDAO;
import org.softcaster.easy_pricer_core.data.Frequency;
import org.softcaster.easy_pricer_core.data.FrequencyDAO;
import org.softcaster.easy_pricer_core.data.InstrumentQuoteDAO;
import org.softcaster.easy_pricer_core.data.IssuerDAO;
import org.softcaster.easy_pricer_core.data.PortfolioMasterDataDAO;
import org.softcaster.easy_pricer_core.data.PositionMasterDataDAO;
import org.softcaster.easy_pricer_core.data.RollConvention;
import org.softcaster.easy_pricer_core.data.RollConventionDAO;
import org.softcaster.easy_pricer_core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_core.data.SettlementTypeDAO;
import org.softcaster.easy_pricer_core.data.TypeOfInterest;
import org.softcaster.easy_pricer_core.data.TypeOfInterestDAO;
import org.softcaster.easy_pricer_core.data.YieldCurveDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ep
 */
@Service
public class MDSFacade {

    @Autowired
    private InstrumentQuoteDAO instrumentQuoteDAO;

    @Autowired
    private SecurityMasterDataDAO securityMasterDataDAO;
    @Autowired
    private BondFutureMasterDataDAO bondFutureMasterDataDAO;
    @Autowired
    private ForexMasterDataDAO forexMasterDataDAO;

    @Autowired
    private IssuerDAO issuerDAO;
    @Autowired
    private CounterpartyDAO counterpartyDAO;
    @Autowired
    private PortfolioMasterDataDAO portfolioMasterDataDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;

    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private CountryDAO countryDAO;
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
    @Autowired
    private YieldCurveDAO yieldCurveDAO;
    @Autowired
    private CounterpartyTypeDAO counterpartyTypeDAO;

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

    public Form findForm(String code) {
        return formDAO.findByCode(code);
    }

    public Frequency findFrequency(String code) {
        return frequencyDAO.findByCode(code);
    }

    public RollConvention findRollConvention(String code) {
        return rollConventionDAO.findByCode(code);
    }

    public AmortizationSchedule findAmortizationSchedule(String code) {
        return amortizationScheduleDAO.findByCode(code);
    }

    public AssetClass findAssetClass(String code) {
        return assetClassDAO.findByCode(code);
    }

    /**
     * @return the issuerDAO
     */
    public IssuerDAO getIssuerDAO() {
        return issuerDAO;
    }

    /**
     * @return the forexMasterDataDAO
     */
    public ForexMasterDataDAO getForexMasterDataDAO() {
        return forexMasterDataDAO;
    }

    /**
     * @return the yieldCurveDAO
     */
    public YieldCurveDAO getYieldCurveDAO() {
        return yieldCurveDAO;
    }

    /**
     * @return the counterparty
     */
    public CounterpartyDAO getCounterpartyDAO() {
        return counterpartyDAO;
    }
                                                                                            
    /**
     * @return the countryDAO
     */
    public CountryDAO getCountryDAO() {
        return countryDAO;
    }

    /**
     * @return the counterpartyTypeDAO
     */
    public CounterpartyTypeDAO getCounterpartyTypeDAO() {
        return counterpartyTypeDAO;
    }

    /**
     * @return the portfolioMasterDataDAO
     */
    public PortfolioMasterDataDAO getPortfolioMasterDataDAO() {
        return portfolioMasterDataDAO;
    }

    /**
     * @return the positionMasterDataDAO
     */
    public PositionMasterDataDAO getPositionMasterDataDAO() {
        return positionMasterDataDAO;
    }

    /**
     * @return the settlementTypeDAO
     */
    public SettlementTypeDAO getSettlementTypeDAO() {
        return settlementTypeDAO;
    }

    /**
     * @return the instrumentQuoteDAO
     */
    public InstrumentQuoteDAO getInstrumentQuoteDAO() {
        return instrumentQuoteDAO;
    }

}
