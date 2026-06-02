/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.jobs;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.easy_pricer_proc.services.FinTxnExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component
public class FinTxnPollingJob {

    private static final Logger log = LoggerFactory.getLogger(FinTxnPollingJob.class);

    @Autowired
    FinTxnExecutionService finTxnExecutionService;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;

    /*
    @Value("${app.scripts.path}")
    private String scriptsPath;

    
    @Autowired
    private EasyPricerEngine engine;
     */
    protected boolean elabFinancialTxnList(List<FinancialTxn> financialTxnList) {
        for (FinancialTxn txn : financialTxnList) {
            try {
                finTxnExecutionService.executeTxn(txn.getIdFinancialTxn());
            } catch (Exception e) {
                log.error("### ERROR ID {}: {}", txn.getIdFinancialTxn(), e.getMessage());
                LoggerMgr.logError(e.getLocalizedMessage());
                return false;
            }
        }

        return true;
    }

    protected void pollPendingTrades() {
        // 1. Cerca le transazioni PENDING
        List<FinancialTxn> pendingTxn = financialTxnDAO.findByTxnStatusCode("PENDING");

        if (!pendingTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} PENDING transaction(s) ===", pendingTxn.size());
            elabFinancialTxnList(pendingTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollToAmendTrades() {
        // 1. Cerca le transazioni PENDING
        List<FinancialTxn> pendingTxn = financialTxnDAO.findByTxnStatusCode("TO_AMEND");

        if (!pendingTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} TO_AMEND transaction(s) ===", pendingTxn.size());
            elabFinancialTxnList(pendingTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollToCancelTrades() {
        // 1. Cerca le transazioni TO_CANCELL
        List<FinancialTxn> cancelledTxn = financialTxnDAO.findByTxnStatusCode("TO_CANCEL");

        if (!cancelledTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} TO_CANCEL transaction(s) ===", cancelledTxn.size());
            elabFinancialTxnList(cancelledTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    // Esegue il polling ogni 15 secondi (15000 millisecondi)
    @Scheduled(fixedDelay = 15000)
    public void pollTrades() {
        
        // 1. Elabora le transazioni TO_AMEND
        pollToAmendTrades();

        // 2. Elabora le transazioni TO_CANCEL
        pollToCancelTrades();
        
        // 3. Elabora le transazioni PENDING (nuove)s
        pollPendingTrades();
    }
}
