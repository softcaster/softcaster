/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccrualLyfeCycleService implements LifeCycleHandler {

    private static final Logger log = LoggerFactory.getLogger(SettlementLyfeCycleService.class);
    
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    
    @Override
    public AccountingEvent generateEvent(EventInfo info) throws LifeCycleException {
        if (!(info instanceof AccrualEventInfo accrualEventInfo)) {
            String error = "### Invalid EventInfo";
            log.error(error);
            throw new LifeCycleException(error);
        }

        PositionDetail detail = accrualEventInfo.getDetail();
        if(detail == null) {
            String error = "### Invalid Detal";
            log.error(error);
            throw new LifeCycleException(error);
        }
        
        return generateAccountingEventAccruals(detail);
    }

    private AccountingEvent generateAccountingEventAccruals(PositionDetail detail) {
        AccountingEventAccruals event = null;
        /*
        Integer masterDataId = positionTxnLinksDAO.findMasterDataIdByTxnLinkId(link.getPosTxnLinkId());
        if(masterDataId != null) {
            
        }
        */
        return event;
    }    
}
