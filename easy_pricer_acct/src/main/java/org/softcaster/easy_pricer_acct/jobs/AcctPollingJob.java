/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.jobs;

import org.softcaster.easy_pricer_acct.services.TradeAccountingEventService;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_acct.services.EngineStateManager;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AcctPollingJob {

    private static final Logger log = LoggerFactory.getLogger(AcctPollingJob.class);

    @Autowired
    private EngineStateManager engineStateManager;

    @Autowired
    private AccountingEventDAO accountingEventDAO;

    @Autowired
    private TradeAccountingEventService tradeAccountingEventService;


    private void elabAmendedTradeEvents(List<AccountingEvent> pendingEvents) {

    }

    private void elabCancelledTradeEvents(List<AccountingEvent> pendingEvents) {

    }

    private void elabExecutedTradeEvents(List<AccountingEvent> pendingEvents) {
        for (AccountingEvent event : pendingEvents) {
            try {
                tradeAccountingEventService.processEvent(event);
            } catch (Exception e) {
                log.error("### ERROR ID {}: {}", event.getEventId(), e.getMessage());
                LoggerMgr.logError(e.getLocalizedMessage());
            }
        }
    }

    private void poolPendingAmendedTradeEvents() {
        List<AccountingEvent> pendingEvents = accountingEventDAO.findTradeEvents(EventSourceType.TRADE,
                EventType.TRADE_AMENDED, AccountingEventStatus.NEW);

        if (!pendingEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} AMENDED transaction(s) ===", pendingEvents.size());
            elabAmendedTradeEvents(pendingEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    private void poolPendingCancelledTradeEvents() {
        List<AccountingEvent> pendingEvents = accountingEventDAO.findTradeEvents(EventSourceType.TRADE,
                EventType.TRADE_CANCELED, AccountingEventStatus.NEW);

        if (!pendingEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} CANCELLED transaction(s) ===", pendingEvents.size());
            elabCancelledTradeEvents(pendingEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    private void poolPendingExecutedTradeEvents() {
        List<AccountingEvent> pendingEvents = accountingEventDAO.findTradeEvents(EventSourceType.TRADE,
                EventType.TRADE_EXECUTED, AccountingEventStatus.NEW);

        if (!pendingEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} EXECUTED transaction(s) ===", pendingEvents.size());
            elabExecutedTradeEvents(pendingEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    @Transactional
    protected void pollPendingAcctingTradeEvents() {
        /*
        List<EventType> targetTypes = List.of(
                EventType.TRADE_EXECUTED,
                EventType.TRADE_AMENDED,
                EventType.TRADE_CANCELED
        );
         */
        poolPendingAmendedTradeEvents();
        poolPendingCancelledTradeEvents();
        poolPendingExecutedTradeEvents();
    }

    // Esegue il polling ogni 15 secondi (15000 millisecondi)
    @Scheduled(fixedDelay = 15000)
    public void pollAccountinEvents() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [PSRV] Service is suspended ===\n");
            return;
        }
        //loadScript();
        pollPendingAcctingTradeEvents();
    }
}
