/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.jobs;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
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
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;
import org.softcaster.easy_pricer_lc.services.AccrualEventInfo;
import org.softcaster.easy_pricer_lc.services.AccrualLyfeCycleService;
import org.softcaster.easy_pricer_lc.services.LinkEventInfo;
import org.softcaster.easy_pricer_lc.services.SettlementLyfeCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LifeCycleJob {

    private static final Logger log = LoggerFactory.getLogger(LifeCycleJob.class);
    private SystemBusinessCalendar sbc = null;

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

    @PostConstruct
    public void init() {
        sbc = sbcDAO.findBySbcId(1);
    }

    public void runLifeCycles() {
        runSettlementLyfeCycle();
        runAccrualLyfeCycle();
    }

    // -------------------------------------------------------------------------
    //  Settlement LifeCycle
    // -------------------------------------------------------------------------
    private void runSettlementLyfeCycle() {
        List<PositionTxnLinks> links = positionTxnLinksDAO.fetchAndClaimLinks(sbc.getOfficialDate());
        if (!links.isEmpty()) {
            for (PositionTxnLinks link : links) {
                generateSettlementEvent(link);
            }
        }
    }

    void generateSettlementEvent(PositionTxnLinks link) {

        try {
            LinkEventInfo info = new LinkEventInfo();
            info.setLink(link);
            AccountingEvent event = slc.generateEvent(info);
            accountingEventDAO.saveOrUpdate(event);
        } catch (Exception e) {
            log.error("### Error processing txn: " + link.getFinancialTxn());
            throw new LifeCycleException(e.getLocalizedMessage());
        }
    }
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    //  Accrual LifeCycle
    // -------------------------------------------------------------------------
    private void runAccrualLyfeCycle() {
        fetchPosition();
    }

    private void fetchPosition() {
        List<PositionMasterData> positions = positionMasterDataDAO.findAll();

        if (!positions.isEmpty()) {
            for (PositionMasterData pmd : positions) {
                fetchPositionDetails(pmd);
            }
        }
    }

    private void fetchPositionDetails(PositionMasterData pmd) {
        List<PositionDetail> details = positionDetailDAO.fetchAndClaimByPositionMasterData(pmd).orElse(null);

        if (details != null && !details.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PositionDetail detail : details) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        generateAccrualEvent(detail);
                    } catch (Exception e) {
                        log.error("### Life Cycle error for position {}: {}", detail.getIdPositionDetail(), e.getMessage());
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }

    private void generateAccrualEvent(PositionDetail detail) {
        try {
            AccrualEventInfo info = new AccrualEventInfo();
            info.setDetail(detail);
            info.setFrom(sbc.getLastOfficialDate());
            info.setTo(sbc.getOfficialDate());
            AccountingEvent event = alc.generateEvent(info);
            if (event != null) {
                accountingEventDAO.saveOrUpdate(event);
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
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }
}
