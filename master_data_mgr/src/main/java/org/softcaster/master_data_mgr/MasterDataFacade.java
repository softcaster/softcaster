/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr;

import org.softcaster.easy_pricer_core.data.BondFutureMasterDataDAO;
import org.softcaster.easy_pricer_core.data.CurrencyDAO;
import org.softcaster.easy_pricer_core.data.DaycountDAO;
import org.softcaster.easy_pricer_core.data.SecurityMasterDataDAO;
import org.softcaster.easy_pricer_core.data.SettlementTypeDAO;
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
    private CurrencyDAO currencyDAO;
    @Autowired
    private DaycountDAO daycountDAO;
    @Autowired
    private SettlementTypeDAO settlementTypeDAO;
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
    
    
}
