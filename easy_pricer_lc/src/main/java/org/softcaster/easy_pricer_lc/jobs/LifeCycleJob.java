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
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;
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
import org.softcaster.easy_pricer_lc.services.SettlementLyfeCycleService;
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

    public String runSettlementLyfeCycleOnLine() {
        String result = "";
        try {
            runSettlementLyfeCycle();
        } catch (Exception ex) {
            result = ex.getLocalizedMessage();
        }
        return result;
    }

    public String runAccrualLyfeCycleOnLine() {
        String result = "";
        try {
            runAccrualLyfeCycle();
        } catch (Exception ex) {
            result = ex.getLocalizedMessage();
        }
        return result;
    }

    public String runCouponLyfeCycleOnLine() {
        String result = "";
        try {
            runCouponLyfeCycle();
        } catch (Exception ex) {
            result = ex.getLocalizedMessage();
        }
        return result;
    }

    public void runLifeCycles() {
        runAccrualLyfeCycle();
        runSettlementLyfeCycle();
    }

    // -------------------------------------------------------------------------
    //  Settlement LifeCycle
    // -------------------------------------------------------------------------
    private void runSettlementLyfeCycle() {
        try {
            List<PositionTxnLinks> links = positionTxnLinksDAO.fetchAndClaimLinks(sbcDAO.findBySbcId(1).getOfficialDate());
            if (!links.isEmpty()) {
                for (PositionTxnLinks link : links) {
                    generateSettlementEvent(link);
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    void generateSettlementEvent(PositionTxnLinks link) {

        try {
            LinkEventInfo info = new LinkEventInfo();
            info.setLink(link);
            List<AccountingEvent> events = slc.generateEvents(info);
            if (events != null && !events.isEmpty()) {
                for (AccountingEvent event : events) {
                    accountingEventDAO.saveOrUpdate(event);
                }
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
    private void runAccrualLyfeCycle() {
        try {
            SystemBusinessCalendar sbc = sbcDAO.findBySbcId(1);
            if (sbc.getNextBusinessDate() == null) {
                throw new LifeCycleException("next_business_date not set: the accrual life cycle runs during the EOD");
            }
            fetchPosition(EventType.ACCRUAL, sbc.getOfficialDate(), sbc.getNextBusinessDate());
        } catch (Exception ex) {
            throw ex;
        }
    }

    // Cedole della chiusura: staccano in (last_official_date, official_date]. Quelle
    // fino a last_official_date dei deal retrodatati le genera BackdatedTxnProcessor.
    private void runCouponLyfeCycle() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(1);
        fetchPosition(EventType.COUPON, sbc.getLastOfficialDate(), sbc.getOfficialDate());
    }

    private void fetchPosition(EventType eventType, LocalDate from, LocalDate to) {
        List<PositionMasterData> positions = positionMasterDataDAO.findAll();

        try {
            if (!positions.isEmpty()) {
                for (PositionMasterData pmd : positions) {
                    fetchPositionDetails(pmd, eventType, from, to);
                }
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void fetchPositionDetails(PositionMasterData pmd, EventType eventType, LocalDate from, LocalDate to) {
        List<PositionDetail> details = positionDetailDAO.fetchAndClaimByPositionMasterData(pmd).orElse(null);

        if (details != null && !details.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PositionDetail detail : details) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        generateAccrualEvent(detail, eventType, from, to);
                    } catch (Exception ex) {
                        log.error("### Life Cycle error for position {}: {}", detail.getIdPositionDetail(), ex.getMessage());
                        throw ex;
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }

    private void generateAccrualEvent(PositionDetail detail, EventType eventType, LocalDate from, LocalDate to) {
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
                                continue;
                            }
                            accountingEventDAO.saveOrUpdate(event);
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
