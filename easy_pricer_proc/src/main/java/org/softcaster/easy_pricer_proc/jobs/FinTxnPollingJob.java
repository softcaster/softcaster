/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.easy_pricer_proc.services.EngineStateManager;
import org.softcaster.easy_pricer_proc.services.FinTxnExecutionService;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
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
    private FinTxnExecutionService finTxnExecutionService;

    @Autowired
    private FinancialTxnDAO financialTxnDAO;

    @Autowired
    private EngineStateManager engineStateManager;

    // Iniezione del pool di thread 
    @Autowired
    @Qualifier("txnExecutor")
    private TaskExecutor taskExecutor;

    protected boolean elabFinancialTxnList(List<FinancialTxn> financialTxnList) {
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (FinancialTxn txn : financialTxnList) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    finTxnExecutionService.elabFinancialTxn(txn.getIdFinancialTxn());
                    return true;
                } catch (Exception e) {
                    // Non si puo spostare la funzione updateStatusOnFailure all'interno della classe
                    // perchè In Spring, l'annotazione @Transactional funziona tramite proxy.
                    // usare un metodo della stessa classe per chiamare  un altro metodo della stessa classe 
                    // non funziona perche'il proxy di Spring viene saltato completamente.
                    // Di conseguenza, REQUIRES_NEW viene ignorato. Il codice girerà senza una transazione autonoma 
                    // o tenterà di usare quella precedente (che è fallita e marcata per il rollback), 
                    // impedendo il salvataggio dello stato REJECTED
                    log.error("### ERROR ID {}: {}", txn.getIdFinancialTxn(), e.getMessage());
                    LoggerMgr.logError(e.getLocalizedMessage());
                    finTxnExecutionService.updateStatusOnFailure(txn.getIdFinancialTxn(), TxnStatus.REJECTED);
                    return false;
                }
            }, taskExecutor);

            futures.add(future);
        }

        // Attende che TUTTI i thread della lista corrente abbiano finito 
        // prima di restituire il controllo al chiamante
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        // Verifica se tutti i task hanno avuto successo (opzionale, per log/statistiche)
        return futures.stream()
                .map(CompletableFuture::join)
                .reduce(true, (a, b) -> a && b);
    }

    protected void pollPendingTrades() {
        // 1. Cerca le transazioni PENDING
        List<FinancialTxn> pendingTxn = financialTxnDAO.findAndClaimByTxnStatusCode("PENDING");

        if (!pendingTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} PENDING transaction(s) ===", pendingTxn.size());
            elabFinancialTxnList(pendingTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollToAmendTrades() {
        // 1. Cerca le transazioni PENDING
        List<FinancialTxn> pendingTxn = financialTxnDAO.findAndClaimByTxnStatusCode("TO_AMEND");

        if (!pendingTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} TO_AMEND transaction(s) ===", pendingTxn.size());
            elabFinancialTxnList(pendingTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollToCancelTrades() {
        // 1. Cerca le transazioni TO_CANCELL
        List<FinancialTxn> cancelledTxn = financialTxnDAO.findAndClaimByTxnStatusCode("TO_CANCEL");
        if (!cancelledTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} TO_CANCEL transaction(s) ===", cancelledTxn.size());
            elabFinancialTxnList(cancelledTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    // Esegue il polling ogni 15 secondi (15000 millisecondi)
    @Scheduled(fixedDelay = 15000)
    public void pollTrades() {

        if (engineStateManager.isSuspended()) {
            log.info("=== [PSRV] Service is suspended ===\n");
            return;
        }

        // 1. Elabora le transazioni TO_AMEND
        pollToAmendTrades();

        // 2. Elabora le transazioni TO_CANCEL
        pollToCancelTrades();

        // 3. Elabora le transazioni PENDING (nuove)
        pollPendingTrades();
    }
}
