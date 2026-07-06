/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.PositionTxnLinks;
import org.softcaster.core.data.account.AccountingEvent;
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

    @Override
    public AccountingEvent generateEvent(EventInfo info) throws LifeCycleException {

        if (!(info instanceof SettlementEventInfo settlementEventInfo)) {
            String error = "### Invalid EventInfo";
            log.error(error);
            throw new LifeCycleException(error);
        }

        PositionTxnLinks link = settlementEventInfo.getLink();
        if(link == null) {
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
            // Genero AccountingEvent
            event = new AccountingEvent();
            event.setSourceId(link.getFinancialTxn());
            event.setEventStatus(AccountingEventStatus.NEW);
            event.setEventType(EventType.SETTLEMENT);
            event.setSourceType(EventSourceType.TRADE);
            event.setEventKey(txn.getMasterData().getCode() + " [" + txn.getIdFinancialTxn() + "] " + "[" + EventType.SETTLEMENT.getCode() + "]" + LocalDate.now());
            event.setCreatedAt(LocalDateTime.now());
            event.setGeneratedBy(txn.getMasterData().getIdMasterData());
            event.setGeneratedRef("");
        } catch (Exception e) {
            log.error("### Error processing txn: " + link.getFinancialTxn());
            throw new LifeCycleException(e.getLocalizedMessage());
        }

        return event;
    }
}
