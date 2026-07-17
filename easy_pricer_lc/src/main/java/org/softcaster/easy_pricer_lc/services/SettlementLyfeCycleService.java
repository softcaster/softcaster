/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.PositionTxnLinks;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettlementLyfeCycleService implements LifeCycleHandler {

    private static final Logger log = LoggerFactory.getLogger(SettlementLyfeCycleService.class);

    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    
    @Autowired
    AccountingEventDAO accountingEventDAO;

    private String getEventKey(FinancialTxn txn) {        
        String eventKey = txn.getMasterData().getCode() + " [" + txn.getIdFinancialTxn() + "] " + "[" + EventType.SETTLEMENT.getCode() + "]" + txn.getSettlement();
        return eventKey;
    }
    
    @Override
    public List<AccountingEvent> generateEvents(EventInfo info) throws LifeCycleException {

        if (!(info instanceof LinkEventInfo settlementEventInfo)) {
            String error = "### Invalid EventInfo";
            log.error(error);
            throw new LifeCycleException(error);
        }

        PositionTxnLinks link = settlementEventInfo.getLink();
        if (link == null) {
            String error = "### Invalid PositionTxnLinks";
            log.error(error);
            throw new LifeCycleException(error);
        }

        AccountingEvent event = null;
        try {
            FinancialTxn txn = financialTxnDAO.findByIdFinancialTxn(link.getFinancialTxn());
            if (txn == null) {
                String error = "### Error loading txn: " + link.getFinancialTxn();
                log.error(error);
                throw new LifeCycleException(error);
            }
            
            String eventKey = getEventKey(txn);
            // Controlla se evento e`gia`stato generato
            event = accountingEventDAO.findByEventKey(eventKey);
            if(event != null) {
                log.error("Event already generated.");
                return null;
            }
            
            // Genero AccountingEvent
            event = new AccountingEvent();
            event.setSourceId(link.getFinancialTxn());
            event.setEventStatus(AccountingEventStatus.NEW);
            event.setEventType(EventType.SETTLEMENT);
            event.setSourceType(EventSourceType.TRADE);
            event.setEventKey(eventKey);
            event.setCreatedAt(LocalDateTime.now());
            event.setPositionDetail(link.getPositionDetail());
            event.setGeneratedBy(txn.getMasterData().getIdMasterData());
            event.setGeneratedRef("");
        } catch (Exception e) {
            log.error("### Error processing txn: " + link.getFinancialTxn());
            throw new LifeCycleException(e.getLocalizedMessage());
        }
        if (event != null) {
            List<AccountingEvent> events = new ArrayList<>();
            events.add(event);
            return events;
        } else {
            return null;
        }
    }
}
