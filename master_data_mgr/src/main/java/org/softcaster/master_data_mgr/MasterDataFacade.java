/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr;

import org.softcaster.core.data.AssetClass;
import org.softcaster.core.data.AssetClassDAO;
import org.softcaster.core.data.BondFutureMasterDataDAO;
import org.softcaster.core.data.BrokerInstrumentRulesDAO;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.CountryDAO;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.ForexMasterDataDAO;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.IssuerDAO;
import org.softcaster.core.data.MmFutureMasterDataDAO;
import org.softcaster.core.data.PortfolioMasterDataDAO;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.SecurityMasterDataDAO;
import org.softcaster.core.data.SettlementTypeDAO;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.core.data.account.GlAccountDAO;
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
    private ForexMasterDataDAO forexMasterDataDAO;
    @Autowired
    private FxFutureMasterDataDAO fxFutureMasterDataDAO;
    @Autowired
    private MmFutureMasterDataDAO mmFutureMasterDataDAO;

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
    private SettlementTypeDAO settlementTypeDAO;
    @Autowired
    private AssetClassDAO assetClassDAO;
    @Autowired
    private YieldCurveDAO yieldCurveDAO;
    @Autowired
    private BrokerInstrumentRulesDAO brokerInstrumentRulesDAO;
    @Autowired
    private GlAccountDAO glAccountDAO;

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
     * @return the assetClassDAO
     */
    public AssetClassDAO getAssetClassDAO() {
        return assetClassDAO;
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
     * @return the fxFutureMasterDataDAO
     */
    public FxFutureMasterDataDAO getFxFutureMasterDataDAO() {
        return fxFutureMasterDataDAO;
    }

    /**
     * @return the mmFutureMasterDataDAO
     */
    public MmFutureMasterDataDAO getMmFutureMasterDataDAO() {
        return mmFutureMasterDataDAO;
    }

    /**
     * @return the brokerInstrumentRulesDAO
     */
    public BrokerInstrumentRulesDAO getBrokerInstrumentRulesDAO() {
        return brokerInstrumentRulesDAO;
    }

    /**
     * @return the glAccountDAO
     */
    public GlAccountDAO getGlAccountDAO() {
        return glAccountDAO;
    }
}
