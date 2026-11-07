/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.jobs;

import org.slf4j.Logger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.PositionTxnLinks;
import org.softcaster.core.data.PositionTxnLinksDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;
import org.softcaster.easy_pricer_lc.services.AccrualEventInfo;
import org.softcaster.easy_pricer_lc.services.AccrualLyfeCycleService;
import org.softcaster.easy_pricer_lc.services.LinkEventInfo;
import org.softcaster.easy_pricer_lc.services.LoanRepaymentService;
import org.softcaster.easy_pricer_lc.services.SettlementLyfeCycleService;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LifeCycleJob {

    private static final Logger log = LoggerFactory.getLogger(LifeCycleJob.class);

    @Autowired
    private PositionDetailDAO positionDetailDAO;

    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;

    @Autowired
    private PositionTxnLinksDAO positionTxnLinksDAO;

    @Autowired
    private AccountingEventDAO accountingEventDAO;

    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;

    // Iniezione del pool di thread 
    @Autowired
    @Qualifier("lifeCycleExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private SettlementLyfeCycleService slc;

    @Autowired
    private AccrualLyfeCycleService alc;

    @Autowired
    private LoanRepaymentService loanRepaymentService;

    /**
     * Contatori di un task: gli elementi girano anche in parallelo e un errore
     * su uno non ferma gli altri.
     */
    private static class TaskCounters {

        final AtomicInteger processed = new AtomicInteger();
        final AtomicInteger created = new AtomicInteger();
        final AtomicInteger skipped = new AtomicInteger();
        final AtomicInteger errors = new AtomicInteger();
        final AtomicReference<String> firstError = new AtomicReference<>();

        void error(String message) {
            errors.incrementAndGet();
            firstError.compareAndSet(null, message);
        }

        EodTaskResult toResult(EodStep task, LocalDate businessDate) {
            String message = firstError.get() == null ? "" : "First error: " + firstError.get();
            return EodTaskResult.of(task, businessDate, processed.get(), created.get(), skipped.get(), errors.get(), message);
        }
    }

    // -------------------------------------------------------------------------
    //  Settlement LifeCycle
    // -------------------------------------------------------------------------
    public EodTaskResult runSettlementLyfeCycle() {
        LocalDate officialDate = sbcDAO.findBySbcId(1).getOfficialDate();
        TaskCounters counters = new TaskCounters();
        for (PositionTxnLinks link : positionTxnLinksDAO.fetchAndClaimLinks(officialDate)) {
            counters.processed.incrementAndGet();
            try {
                generateSettlementEvent(link, counters);
            } catch (Exception ex) {
                counters.error("txn " + link.getFinancialTxn() + ": " + ex.getLocalizedMessage());
            }
        }
        return counters.toResult(EodStep.SETTLEMENT, officialDate);
    }

    void generateSettlementEvent(PositionTxnLinks link, TaskCounters counters) {

        try {
            LinkEventInfo info = new LinkEventInfo();
            info.setLink(link);
            List<AccountingEvent> events = slc.generateEvents(info);
            if (events != null && !events.isEmpty()) {
                for (AccountingEvent event : events) {
                    accountingEventDAO.saveOrUpdate(event);
                    counters.created.incrementAndGet();
                }
            } else {
                // rilancio della stessa chiusura: l'evento c'e' gia'
                counters.skipped.incrementAndGet();
            }
        } catch (Exception e) {
            log.error("### Error processing txn: " + link.getFinancialTxn());
            throw new LifeCycleException(e.getLocalizedMessage());
        }
    }
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    //  Accrual LifeCycle
    // -------------------------------------------------------------------------
    // Rateo della chiusura di T0: da official_date (T0) a next_business_date (T+1),
    // che il servizio EOD valorizza prima di lanciare il lifecycle. I retrodatati
    // registrati a T0 hanno gia' il rateo fino a T0 (BackdatedTxnProcessor).
    public EodTaskResult runAccrualLyfeCycle() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(1);
        if (sbc.getNextBusinessDate() == null) {
            return EodTaskResult.failed(EodStep.ACCRUAL, sbc.getOfficialDate(),
                    "next_business_date not set: the accrual life cycle runs during the EOD");
        }
        return fetchPosition(EodStep.ACCRUAL, EventType.ACCRUAL, sbc.getOfficialDate(), sbc.getOfficialDate(), sbc.getNextBusinessDate());
    }

    // Cedole della chiusura: staccano in (last_official_date, official_date]. Quelle
    // fino a last_official_date dei deal retrodatati le genera BackdatedTxnProcessor.
    // Per i mutui anche il rimborso della quota capitale delle rate (deal EOD).
    public EodTaskResult runCouponLyfeCycle() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(1);
        EodTaskResult coupons = fetchPosition(EodStep.COUPON, EventType.COUPON, sbc.getOfficialDate(), sbc.getLastOfficialDate(), sbc.getOfficialDate());
        EodTaskResult repayments = loanRepaymentService.generateRepayments();
        String message = String.join(" ", coupons.message(), repayments.message()).trim();
        return EodTaskResult.of(EodStep.COUPON, sbc.getOfficialDate(),
                coupons.processed() + repayments.processed(), coupons.created() + repayments.created(),
                coupons.skipped() + repayments.skipped(), coupons.errors() + repayments.errors(), message);
    }

    private EodTaskResult fetchPosition(EodStep task, EventType eventType, LocalDate businessDate, LocalDate from, LocalDate to) {
        TaskCounters counters = new TaskCounters();
        for (PositionMasterData pmd : positionMasterDataDAO.findAll()) {
            fetchPositionDetails(pmd, eventType, from, to, counters);
        }
        return counters.toResult(task, businessDate);
    }

    private void fetchPositionDetails(PositionMasterData pmd, EventType eventType, LocalDate from, LocalDate to, TaskCounters counters) {
        List<PositionDetail> details = positionDetailDAO.findByPositionMasterData(pmd);

        if (!details.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PositionDetail detail : details) {
                counters.processed.incrementAndGet();
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        generateAccrualEvent(detail, eventType, from, to, counters);
                    } catch (Exception ex) {
                        log.error("### Life Cycle error for position {}: {}", detail.getIdPositionDetail(), ex.getMessage());
                        counters.error("position detail " + detail.getIdPositionDetail() + ": " + ex.getMessage());
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }

    private void generateAccrualEvent(PositionDetail detail, EventType eventType, LocalDate from, LocalDate to, TaskCounters counters) {
        try {
            AccrualEventInfo info = new AccrualEventInfo();
            info.setDetail(detail);
            info.setEventType(eventType);
            info.setFrom(from);
            info.setTo(to);
            List<AccountingEvent> events = alc.generateEvents(info);
            if (events != null && !events.isEmpty()) {
                for (AccountingEvent event : events) {
                    if (event instanceof AccountingEventAccruals aea) {
                        if (!NumberUtils.isZero(aea.getAccrualAmount())) {
                            // rilancio della stessa chiusura: l'evento del periodo c'e' gia'
                            if (accountingEventDAO.findByEventKey(event.getEventKey()) != null) {
                                log.warn("Event already generated: {}", event.getEventKey());
                                counters.skipped.incrementAndGet();
                                continue;
                            }
                            accountingEventDAO.saveOrUpdate(event);
                            counters.created.incrementAndGet();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("### Error processing position: " + detail.getIdPositionDetail());
            throw new LifeCycleException(e.getLocalizedMessage());
        }
    }
    // -------------------------------------------------------------------------

    // Esempio chiamata
    //fetchPositionDetails(pmd,this::generateSettlementEvent);
    private void fetchPositionDetails(PositionMasterData pmd, Consumer<PositionDetail> processor) {
        List<PositionDetail> details = positionDetailDAO.fetchAndClaimByPositionMasterData(pmd, 15).orElse(null);

        if (details != null && !details.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PositionDetail detail : details) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        processor.accept(detail);
                    } catch (Exception e) {
                        log.error("### LIFECYCLE ERROR FOR POSITION DETAIL ID {}: {}", detail.getIdPositionDetail(), e.getMessage());
                        throw e;
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }
}
