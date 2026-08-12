/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.jobs;

import org.softcaster.easy_pricer_acct.services.TradeAccountingEventService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_acct.services.AccrualAccountingEventService;
import org.softcaster.easy_pricer_acct.services.EngineStateManager;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
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
    
    @Autowired
    private AccrualAccountingEventService accrualAccountingEventService;
    
    @Autowired
    @Qualifier("acctEventExecutor")
    private TaskExecutor taskExecutor;

    private void elabSettlementTradeEvents(List<AccountingEvent> settlementEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (AccountingEvent event : settlementEvents) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    tradeAccountingEventService.processEvent(event);
                } catch (Exception e) {
                    log.error("### ERROR AMENDED ID {}: {}", event.getEventId(), e.getMessage());
                    LoggerMgr.logError(e.getLocalizedMessage());
                }
            }, taskExecutor));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }
    
    // Raddoppiato il pattern asincrono anche per gli Amended
    private void elabAmendedTradeEvents(List<AccountingEvent> pendingEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (AccountingEvent event : pendingEvents) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    tradeAccountingEventService.processEvent(event);
                } catch (Exception e) {
                    log.error("### ERROR AMENDED ID {}: {}", event.getEventId(), e.getMessage());
                    LoggerMgr.logError(e.getLocalizedMessage());
                }
            }, taskExecutor));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    // Raddoppiato il pattern asincrono anche per i Cancelled
    private void elabCancelledTradeEvents(List<AccountingEvent> pendingEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (AccountingEvent event : pendingEvents) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    tradeAccountingEventService.processEvent(event);
                } catch (Exception e) {
                    log.error("### ERROR CANCELLED ID {}: {}", event.getEventId(), e.getMessage());
                    LoggerMgr.logError(e.getLocalizedMessage());
                }
            }, taskExecutor));
        }
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private void elabExecutedTradeEvents(List<AccountingEvent> pendingEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (AccountingEvent event : pendingEvents) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    tradeAccountingEventService.processEvent(event);
                } catch (Exception e) {
                    log.error("### ERROR EXECUTED ID {}: {}", event.getEventId(), e.getMessage());
                    LoggerMgr.logError(e.getLocalizedMessage());
                }
            }, taskExecutor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }

    private void elabAccrualEvents(List<AccountingEvent> accrualEvents) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (AccountingEvent event : accrualEvents) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    accrualAccountingEventService.processEvent(event);
                } catch (Exception e) {
                    log.error("### ERROR EXECUTED ID {}: {}", event.getEventId(), e.getMessage());
                    LoggerMgr.logError(e.getLocalizedMessage());
                }
            }, taskExecutor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }
    
    private void poolPendingAmendedTradeEvents() {
        List<AccountingEvent> pendingEvents = accountingEventDAO.fetchAndClaimEvents(EventSourceType.TRADE,
                EventType.TRADE_AMENDED);

        if (!pendingEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} AMENDED transaction(s) ===", pendingEvents.size());
            elabAmendedTradeEvents(pendingEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    private void poolPendingCancelledTradeEvents() {
        List<AccountingEvent> pendingEvents = accountingEventDAO.fetchAndClaimEvents(EventSourceType.TRADE,
                EventType.TRADE_CANCELED);

        if (!pendingEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} CANCELLED transaction(s) ===", pendingEvents.size());
            elabCancelledTradeEvents(pendingEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    private void poolPendingExecutedTradeEvents() {
        List<AccountingEvent> pendingEvents = accountingEventDAO.fetchAndClaimEvents(EventSourceType.TRADE,
                EventType.TRADE_EXECUTED);

        if (!pendingEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} EXECUTED transaction(s) ===", pendingEvents.size());
            elabExecutedTradeEvents(pendingEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollPendingAccountingTradeEvents() {
        poolPendingExecutedTradeEvents();
        poolPendingAmendedTradeEvents();
        poolPendingCancelledTradeEvents();
    }

    protected void pollPendingSettlementTradeEvents() {
        List<AccountingEvent> settlementEvents = accountingEventDAO.fetchAndClaimEvents(EventSourceType.TRADE,
                EventType.SETTLEMENT);

        if (!settlementEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} MEMO POSTED transaction(s) ===", settlementEvents.size());
            elabSettlementTradeEvents(settlementEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollPendingAccrualEvents() {
        List<AccountingEvent> accrualEvents = accountingEventDAO.fetchAndClaimEvents(EventSourceType.INSTRUMENT,
                EventType.ACCRUAL);

        if (!accrualEvents.isEmpty()) {
            log.info("=== [BATCH START] find {} ACCRUAL event(s) ===", accrualEvents.size());
            elabAccrualEvents(accrualEvents);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    @Scheduled(fixedDelay = 15000)
    public void pollAccountinEvents() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [ASRV] Service is suspended ===\n");
            return;
        }
        pollPendingAccountingTradeEvents();
        pollPendingSettlementTradeEvents();
        pollPendingAccrualEvents();
    }

}
