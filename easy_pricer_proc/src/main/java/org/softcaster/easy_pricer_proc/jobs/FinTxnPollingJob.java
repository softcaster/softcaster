package org.softcaster.easy_pricer_proc.jobs; // adatta al tuo package

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
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
// + gli import del tuo progetto (FinancialTxn, TxnStatus, EngineStateManager, LoggerMgr, ...)

@Component
public class FinTxnPollingJob {

    private static final Logger log = LoggerFactory.getLogger(FinTxnPollingJob.class);

    /**
     * Protezione contro un drain che non converge (txn che tornano sempre
     * reclamabili).
     */
    private static final int MAX_DRAIN_ROUNDS = 50;

    @Autowired
    private FinTxnExecutionService finTxnExecutionService;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private EngineStateManager engineStateManager;
    @Autowired
    @Qualifier("txnExecutor")
    private TaskExecutor taskExecutor;

    /**
     * Un solo giro di elaborazione alla volta: sia lo scheduler sia il drain
     * dell'EOD passano da qui. Chi vuole "aspettare la fine del ciclo in corso"
     * prende questo lock.
     */
    private final ReentrantLock cycleLock = new ReentrantLock();

    // ------------------------------------------------------------------
    // Elaborazione di una singola txn, con retry sulla race di creazione detail
    // ------------------------------------------------------------------
    private void elabWithRetry(Integer txnId) {
        try {
            finTxnExecutionService.elabFinancialTxn(txnId);
        } catch (Exception e) {
            if (isUniqueViolation(e)) {
                // Il detail e' stato creato da un altro scrittore tra la SELECT e l'INSERT.
                // La transazione REQUIRES_NEW e' gia' in rollback: il retry va fatto QUI,
                // fuori transazione. Al secondo tentativo il detail esiste e viene letto con FOR UPDATE.
                log.warn("Unique violation su position_detail per txn {}, retry", txnId);
                finTxnExecutionService.elabFinancialTxn(txnId);
            } else {
                throw e;
            }
        }
    }

    private static boolean isUniqueViolation(Throwable t) {
        for (Throwable c = t; c != null; c = c.getCause()) {
            if (c instanceof org.springframework.dao.DataIntegrityViolationException
                    || c instanceof org.hibernate.exception.ConstraintViolationException) {
                return true;
            }
            if (c.getCause() == c) {
                break;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Elaborazione di una lista: un thread per posizione, sequenziale dentro la posizione
    // ------------------------------------------------------------------
    protected boolean elabFinancialTxnList(List<FinancialTxn> financialTxnList) {

        Map<String, List<FinancialTxn>> tradesByPosition = financialTxnList.stream()
                .collect(Collectors.groupingBy(txn
                        -> txn.getPositionMd().getIdPosition() + "-"
                + txn.getMasterData().getIdMasterData() + "-"
                + txn.getCounterparty().getIdCounterparty()));

        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (List<FinancialTxn> positionTrades : tradesByPosition.values()) {

            // Ordine deterministico di cattura dentro la posizione
            positionTrades.sort(Comparator.comparing(FinancialTxn::getIdFinancialTxn));

            futures.add(CompletableFuture.supplyAsync(() -> {
                boolean allSuccess = true;
                for (FinancialTxn txn : positionTrades) {
                    try {
                        elabWithRetry(txn.getIdFinancialTxn());
                    } catch (Exception e) {
                        // Non si puo spostare la funzione updateStatusOnFailure all'interno della classe
                        // perchè In Spring, l'annotazione @Transactional funziona tramite proxy.
                        // usare un metodo della stessa classe per chiamare  un altro metodo della stessa classe 
                        // non funziona perche'il proxy di Spring viene saltato completamente.
                        // Di conseguenza, REQUIRES_NEW viene ignorato. Il codice girerà senza una transazione autonoma 
                        // o tenterà di usare quella precedente (che è fallita e marcata per il rollback), 
                        log.error("### ERROR ID {}: {}", txn.getIdFinancialTxn(), e.getMessage());
                        LoggerMgr.logError(e.getLocalizedMessage());
                        finTxnExecutionService.updateStatusOnFailure(txn.getIdFinancialTxn(), TxnStatus.REJECTED);
                        allSuccess = false;
                    }
                }
                return allSuccess;
            }, taskExecutor));
        }

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        return futures.stream().map(CompletableFuture::join).reduce(true, (a, b) -> a && b);
    }

    // ------------------------------------------------------------------
    // Poll per stato: ritornano il numero di txn reclamate (serve al drain)
    // ------------------------------------------------------------------
    private int pollByStatus(String statusCode) {
        List<FinancialTxn> txns = financialTxnDAO.findAndClaimByTxnStatusCode(statusCode);
        if (!txns.isEmpty()) {
            log.info("=== [BATCH START] find {} {} transaction(s) ===", txns.size(), statusCode);
            elabFinancialTxnList(txns);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
        return txns.size();
    }

    protected int pollToAmendTrades() {
        return pollByStatus("TO_AMEND");
    }

    protected int pollToCancelTrades() {
        return pollByStatus("TO_CANCEL");
    }

    protected int pollPendingTrades() {
        return pollByStatus("PENDING");
    }

    /**
     * Un giro completo: amend, cancel, pending. Ritorna le txn reclamate.
     */
    private int runOneRound() {
        return pollToAmendTrades() + pollToCancelTrades() + pollPendingTrades();
    }

    // ------------------------------------------------------------------
    // Scheduler
    // ------------------------------------------------------------------
    @Scheduled(fixedDelay = 15000)
    public void pollTrades() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [PSRV] Service is suspended ===\n");
            return;
        }
        // Se un drain EOD sta girando, salto il giro
        if (!cycleLock.tryLock()) {
            return;
        }
        try {
            // Ricontrollo sotto lock: l'EOD puo' aver sospeso tra il primo check e il lock
            if (engineStateManager.isSuspended()) {
                return;
            }
            runOneRound();
        } finally {
            cycleLock.unlock();
        }
    }

    // ------------------------------------------------------------------
    // Per l'EOD: sospendi l'engine, poi chiama drain()
    // ------------------------------------------------------------------
    /**
     * Precondizione: l'engine e' gia' sospeso (altrimenti lo scheduler riempie
     * la coda mentre si svuota). Prende il lock del ciclo, quindi ATTENDE la
     * fine del giro in corso, poi ripete i giri finche' non c'e' piu' nulla da
     * reclamare. Ritorna il numero totale di txn elaborate. Il gate finale
     * resta countOpenUpTo(...) == 0, che verifica anche txn non reclamabili.
     */
    public int drain() {
        cycleLock.lock();
        try {
            int total = 0;
            for (int round = 0; round < MAX_DRAIN_ROUNDS; round++) {
                int n = runOneRound();
                total += n;
                if (n == 0) {
                    return total;
                }
            }
            throw new IllegalStateException("Coda non smaltita dopo " + MAX_DRAIN_ROUNDS + " giri");
        } finally {
            cycleLock.unlock();
        }
    }
}
