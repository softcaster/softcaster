package org.softcaster.easy_pricer_eod.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.EodRun;
import org.softcaster.core.data.EodStepRun;
import org.softcaster.core.data.EodStepRunDAO;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.core.data.account.JournalEntryLinesDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.ServiceType;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Esegue la chiusura di giornata, indipendente dalla UI: gli step di EodStep in
 * ordine, ognuno registrato su eod_step_run. Si ferma al primo FAILED; il
 * calendario resta in CLOSING e il rilancio salta gli step gia' riusciti per la
 * data.
 */
@Service
public class EodBatchRunner {

    private static final Logger log = LoggerFactory.getLogger(EodBatchRunner.class);

    private static final Set<TxnStatus> OPEN_TXN = Set.of(TxnStatus.PENDING, TxnStatus.VALIDATING, TxnStatus.TO_AMEND,
            TxnStatus.TO_CANCEL, TxnStatus.RESTARTING, TxnStatus.CLAIMED);
    private static final Set<AccountingEventStatus> OPEN_EVENTS = Set.of(AccountingEventStatus.NEW, AccountingEventStatus.PROCESSING);
    private static final Set<AccountingEventStatus> FAILED_EVENTS = Set.of(AccountingEventStatus.FAILED);

    // Come BaseAccountingEventService: oltre e' un errore di quadratura
    private static final double BALANCE_TOLERANCE = 0.01;
    private static final long POLL_MS = 5000;
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int TASK_TIMEOUT_MS = 30 * 60 * 1000;

    @Autowired
    private EodRunStore store;
    @Autowired
    private EodStepRunDAO eodStepRunDAO;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private AccountingEventDAO accountingEventDAO;
    @Autowired
    private JournalEntryLinesDAO journalEntryLinesDAO;
    @Autowired
    private MicroserviceDispatcher dispatcher;
    @Autowired
    private MicroserviceLauncher launcher;

    @Value("${eod.wait.timeout.seconds:600}")
    private long waitTimeoutSeconds;
    @Value("${eod.service.start.timeout.seconds:180}")
    private long serviceStartTimeoutSeconds;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile Consumer<String> listener = message -> {
    };

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Lancio rimasto RUNNING senza che questa console lo stia eseguendo.
     */
    public Optional<EodRun> findInterruptedRun() {
        return running.get() ? Optional.empty() : store.findRunning();
    }

    public void failInterruptedRun(EodRun run) {
        store.failInterruptedRun(run);
    }

    /**
     * Avvia la chiusura in un thread dedicato.
     *
     * @param listener riceve i messaggi per l'operatore
     * @return false se una chiusura e' gia' in corso
     */
    public boolean start(Consumer<String> listener) {
        if (!running.compareAndSet(false, true)) {
            return false;
        }
        this.listener = listener;
        Thread thread = new Thread(this::runBatch, "eod-batch");
        thread.start();
        return true;
    }

    private void runBatch() {
        boolean mtmSuspended = false;
        EodRun run = null;
        try {
            run = store.beginRun();
            info("End of day for " + run.getBusinessDate() + " started, next business date " + run.getNextBusinessDate());
            startMissingServices();
            // Processor e contabilita' devono lavorare; il MtM di polling si ferma:
            // il MtM della chiusura lo lancia lo step MTM
            post(ServiceType.PSRV, "/internal/system/resume");
            post(ServiceType.ASRV, "/internal/system/resume");
            post(ServiceType.MSRV, "/internal/system/suspend");
            mtmSuspended = true;

            EodStatus outcome = executeSteps(run);
            info(outcome == EodStatus.FAILED
                    ? "End of day FAILED: the calendar stays in closing. Fix the problem and start it again."
                    : "End of day completed: " + outcome.getDescription());
        } catch (Exception e) {
            log.error("End of day failed", e);
            info("End of day FAILED: " + e.getLocalizedMessage());
            if (run != null) {
                try {
                    store.endRun(run, EodStatus.FAILED, e.getLocalizedMessage());
                } catch (Exception ex) {
                    log.error("Could not record the failed run", ex);
                }
            }
        } finally {
            if (mtmSuspended) {
                try {
                    post(ServiceType.MSRV, "/internal/system/resume");
                } catch (Exception e) {
                    info("WARNING: could not resume the MtM service: " + e.getLocalizedMessage());
                }
            }
            running.set(false);
        }
    }

    private EodStatus executeSteps(EodRun run) {
        List<EodStep> completed = new ArrayList<>(eodStepRunDAO.findCompletedSteps(run.getBusinessDate()));
        for (EodStep step : EodStep.values()) {
            if (completed.contains(step)) {
                info(step.getCode() + ": already completed, skipped");
                continue;
            }
            info(step.getCode() + ": " + step.getDescription() + "...");
            EodStepRun stepRun = store.beginStep(run, step);

            if (step == EodStep.DATE_ROLL) {
                // anche i lanci precedenti della stessa data: gli step riusciti non si rieseguono
                EodStatus outcome = eodStepRunDAO.hasWarnings(run.getBusinessDate()) ? EodStatus.WARNING : EodStatus.OK;
                try {
                    store.rollDate(run, stepRun, outcome);
                } catch (RuntimeException e) {
                    store.endStep(stepRun, EodTaskResult.failed(step, run.getBusinessDate(), e.getLocalizedMessage()));
                    throw e;
                }
                info(step.getCode() + ": official date is now " + run.getNextBusinessDate());
                refreshOfficialDate();
                return outcome;
            }

            EodTaskResult result = execute(step, run);
            store.endStep(stepRun, result);
            info(step.getCode() + ": " + result.status() + " processed=" + result.processed() + " created=" + result.created()
                    + " skipped=" + result.skipped() + " errors=" + result.errors()
                    + (result.message() == null || result.message().isBlank() ? "" : " - " + result.message()));
            if (result.status() == EodStatus.FAILED) {
                store.endRun(run, EodStatus.FAILED, step.getCode() + ": " + result.message());
                return EodStatus.FAILED;
            }
        }
        throw new IllegalStateException("End of day finished without rolling the date");
    }

    private EodTaskResult execute(EodStep step, EodRun run) {
        LocalDate date = run.getBusinessDate();
        try {
            return switch (step) {
                case OPEN_CLOSING -> openClosing(run);
                case COUPON -> callTask(step, date, ServiceType.LSRV, "/internal/system/coupon");
                case SETTLEMENT -> callTask(step, date, ServiceType.LSRV, "/internal/system/settlement");
                case ACCRUAL -> callTask(step, date, ServiceType.LSRV, "/internal/system/accrual");
                case FX_ROLLING -> fxRolling(date);
                case MTM -> callTask(step, date, ServiceType.MSRV, "/internal/mtm/eod");
                case WAIT_SETTLEMENT_POSTED, WAIT_EOD_POSTED -> waitForAccounting(step, date);
                case CONTROLS -> controls(date);
                default -> throw new IllegalStateException("Unexpected step " + step);
            };
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return EodTaskResult.failed(step, date, "Interrupted");
        } catch (Exception e) {
            log.error("Step {} failed", step, e);
            return EodTaskResult.failed(step, date, e.getLocalizedMessage());
        }
    }

    // ---------------------------------------------------------------------------
    // Step
    // ---------------------------------------------------------------------------
    private EodTaskResult openClosing(EodRun run) throws InterruptedException {
        store.openClosing(run.getNextBusinessDate());
        // i deal entrati prima del blocco devono arrivare in posizione
        long open = waitForOpenTrades();
        if (open > 0) {
            return EodTaskResult.of(EodStep.OPEN_CLOSING, run.getBusinessDate(), (int) open, 0, 0, (int) open,
                    open + " trades not processed after " + waitTimeoutSeconds + " seconds");
        }
        return EodTaskResult.of(EodStep.OPEN_CLOSING, run.getBusinessDate(), 0, 0, 0, 0, "Trades blocked, none left to process");
    }

    /**
     * I deal del rolling devono entrare in posizione prima del MtM di chiusura.
     */
    private EodTaskResult fxRolling(LocalDate date) throws InterruptedException {
        EodTaskResult result = callTask(EodStep.FX_ROLLING, date, ServiceType.TSRV, "/internal/system/fx_rolling");
        if (result.status() == EodStatus.FAILED) {
            return result;
        }
        long open = waitForOpenTrades();
        if (open > 0) {
            return EodTaskResult.of(EodStep.FX_ROLLING, date, result.processed(), result.created(), result.skipped(), (int) open,
                    open + " trades not processed after " + waitTimeoutSeconds + " seconds");
        }
        return result;
    }

    /**
     * @return i deal rimasti da elaborare allo scadere del timeout, 0 se finiti
     */
    private long waitForOpenTrades() throws InterruptedException {
        long deadline = deadline();
        long open = financialTxnDAO.countByTxnStatus(OPEN_TXN);
        while (open > 0 && System.currentTimeMillis() <= deadline) {
            Thread.sleep(POLL_MS);
            open = financialTxnDAO.countByTxnStatus(OPEN_TXN);
        }
        return open;
    }

    private EodTaskResult waitForAccounting(EodStep step, LocalDate date) throws InterruptedException {
        long deadline = deadline();
        while (true) {
            long failed = accountingEventDAO.countByEventStatus(FAILED_EVENTS);
            if (failed > 0) {
                return EodTaskResult.of(step, date, (int) failed, 0, 0, (int) failed, failed + " accounting events failed");
            }
            long open = accountingEventDAO.countByEventStatus(OPEN_EVENTS);
            if (open == 0) {
                return EodTaskResult.of(step, date, 0, 0, 0, 0, "All accounting events posted");
            }
            if (System.currentTimeMillis() > deadline) {
                return EodTaskResult.of(step, date, (int) open, 0, 0, (int) open,
                        open + " accounting events not posted after " + waitTimeoutSeconds + " seconds");
            }
            Thread.sleep(POLL_MS);
        }
    }

    private EodTaskResult controls(LocalDate date) {
        long openTxn = financialTxnDAO.countByTxnStatus(OPEN_TXN);
        long openEvents = accountingEventDAO.countByEventStatus(OPEN_EVENTS);
        long failedEvents = accountingEventDAO.countByEventStatus(FAILED_EVENTS);
        long unbalanced = journalEntryLinesDAO.countUnbalancedEntries(BALANCE_TOLERANCE);
        String message = openTxn + " open trades, " + openEvents + " open accounting events, "
                + failedEvents + " failed accounting events, " + unbalanced + " unbalanced journal entries";
        int errors = (int) (openTxn + openEvents + failedEvents + unbalanced);
        return EodTaskResult.of(EodStep.CONTROLS, date, 4, 0, 0, errors, message);
    }

    private long deadline() {
        return System.currentTimeMillis() + waitTimeoutSeconds * 1000;
    }

    // ---------------------------------------------------------------------------
    // Servizi
    // ---------------------------------------------------------------------------
    private void startMissingServices() throws InterruptedException {
        List<RestServiceDescriptor> missing = new ArrayList<>();
        for (ServiceType type : ServiceType.values()) {
            RestServiceDescriptor descriptor = descriptor(type);
            if (!launcher.isAlive(descriptor)) {
                info("Starting service " + descriptor.getServiceName() + "...");
                launcher.startService(descriptor);
                missing.add(descriptor);
            }
        }
        long deadline = System.currentTimeMillis() + serviceStartTimeoutSeconds * 1000;
        for (RestServiceDescriptor descriptor : missing) {
            while (!launcher.isAlive(descriptor)) {
                if (System.currentTimeMillis() > deadline) {
                    throw new IllegalStateException("Service " + descriptor.getServiceName() + " not started after "
                            + serviceStartTimeoutSeconds + " seconds");
                }
                Thread.sleep(POLL_MS);
            }
            info("Service " + descriptor.getServiceName() + " is up");
        }
    }

    private RestServiceDescriptor descriptor(ServiceType type) {
        RestServiceDescriptor descriptor = dispatcher.getDescriptor(type);
        if (descriptor == null) {
            throw new IllegalStateException("No descriptor for service " + type.getCode());
        }
        return descriptor;
    }

    private String url(ServiceType type, String path) {
        return "http://localhost:" + descriptor(type).getPort() + "/api/v1" + path;
    }

    private void post(ServiceType type, String path) {
        client().post().uri(url(type, path)).retrieve().toBodilessEntity();
    }

    /**
     * Un FAILED arriva come HTTP 500 con l'esito nel body: si legge comunque.
     */
    private EodTaskResult callTask(EodStep step, LocalDate date, ServiceType type, String path) {
        EodTaskResult result = client().post().uri(url(type, path)).exchange((request, response) -> {
            try {
                return response.bodyTo(EodTaskResult.class);
            } catch (RuntimeException e) {
                throw new IllegalStateException("Unexpected answer from " + type.getCode() + ": HTTP " + response.getStatusCode(), e);
            }
        });
        if (result == null) {
            return EodTaskResult.failed(step, date, "Empty answer from " + type.getCode());
        }
        return result;
    }

    /**
     * srv e mtm tengono in memoria la data ufficiale. La chiusura e' gia' fatta:
     * un errore qui non la annulla, ma va segnalato all'operatore.
     */
    private void refreshOfficialDate() {
        for (ServiceType type : List.of(ServiceType.TSRV, ServiceType.MSRV)) {
            try {
                post(type, "/internal/system/refresh_date");
            } catch (Exception e) {
                log.error("Could not refresh the official date of {}", type, e);
                info("WARNING: " + type.getCode() + " did not refresh the official date, restart it: " + e.getLocalizedMessage());
            }
        }
    }

    private RestClient client() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        factory.setReadTimeout(TASK_TIMEOUT_MS);
        return RestClient.builder().requestFactory(factory).build();
    }

    private void info(String message) {
        log.info(message);
        listener.accept(message);
    }
}
