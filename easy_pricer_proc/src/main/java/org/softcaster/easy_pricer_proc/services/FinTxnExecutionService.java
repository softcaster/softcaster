/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.services;

import java.time.LocalDateTime;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.easy_pricer_proc.processors.ITxnProcessor;
import org.softcaster.easy_pricer_proc.processors.ProcessorDispatcher;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.TxnStatus;
import static org.softcaster.engine.enums.TxnStatus.EXECUTED;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinTxnExecutionService {

    private static final Logger log = LoggerFactory.getLogger(FinTxnExecutionService.class);
    private CompiledScript cachedScript = null;
    private long lastScriptModifiedTime = 0;

    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private PositionDetailDAO positionRepository;
    @Autowired
    private ProcessorDispatcher processorDispatcher;
    @Autowired
    private ScriptEngine groovyEngine;
    @Autowired
    AccountingEventDAO accountingEventDAO;

    public void executeTxn(Integer txnId) {
        FinancialTxn txn = financialTxnDAO.findByIdFinancialTxn(txnId);

        // Determino nuovo stato "potenziale"
        TxnStatus oldStatus = TxnStatus.fromCode(txn.getTxnStatus().getCode());
        TxnStatus newStatus = TxnStatus.REJECTED;
        switch (oldStatus) {
            case PENDING, RESTARTING ->
                newStatus = TxnStatus.EXECUTED;
            case TO_CANCEL ->
                newStatus = TxnStatus.CANCELLED;
            case TO_AMEND ->
                newStatus = TxnStatus.AMENDED;
            default -> {
            }
        }

        if (newStatus == TxnStatus.REJECTED) {
            throw new TxnProcessingException("Invalid Status");
        }

        try {
            processBusiness(txnId);
            updateStatus(txnId, newStatus);

        } catch (Exception e) {
            updateStatus(txnId, TxnStatus.REJECTED);
            throw e;
        }
    }

    // Non serve più aprire una nuova transazione visto che è aperta a
    // livello di job. Lascio comunque transactional per isolare i metodi
    @Transactional//(propagation = Propagation.REQUIRES_NEW)
    protected void updateStatus(Integer txnId, TxnStatus status) {

        FinancialTxn txn = financialTxnDAO.findByIdWithMasterData(txnId);
        txn.setTxnStatus(status);
        financialTxnDAO.saveOrUpdate(txn);

        postElabFinancialTxn(txn);
    }

    @Transactional//(propagation = Propagation.REQUIRES_NEW)
    protected void processBusiness(Integer txnId) {

        FinancialTxn txn = financialTxnDAO.findByIdWithMasterData(txnId);
        PositionDetail position = getPositionDetail(txn);

        elabFinancialTxn(txn, position);

        positionRepository.saveOrUpdate(position);
    }

    protected PositionDetail getPositionDetail(FinancialTxn txn) {
        PositionDetail position = positionRepository.findByPositionMdAndMasterDataAndCounterparty(
                txn.getPositionMd().getIdPosition(), txn.getMasterData().getIdMasterData(),
                txn.getCounterparty().getIdCounterparty()).orElseGet(() -> {
            PositionDetail newPosition = new PositionDetail();
            newPosition.setPositionMd(txn.getPositionMd().getIdPosition());
            newPosition.setMasterData(txn.getMasterData().getIdMasterData());
            newPosition.setCounterparty(txn.getCounterparty().getIdCounterparty());
            newPosition.initialize();
            return newPosition;
        });

        return position;
    }

    protected void elabFinancialTxn(FinancialTxn txn, PositionDetail position) {

        ITxnProcessor processor = processorDispatcher.dispatch(txn.getMasterData().getAssetClass().getCode());
        if (processor != null) {
            // Serve per calcolo unrealizedPnL
            position.setMarketPrice(txn.getPrice());
            processor.process(txn, position);
        } else {
            throw new TxnProcessingException("Invalid ITxnProcessor");
        }
    }

    private void postElabFinancialTxn(FinancialTxn txn) {
        if (txn == null) {
            throw new TxnProcessingException("Invalid Txn");
        }
        try {
            // Genero AccountingEvent
            AccountingEvent event = new AccountingEvent();
            switch (txn.getTxnStatus()) {
                case EXECUTED ->
                    event.setEventType(EventType.TRADE_EXECUTED);
                case AMENDED ->
                    event.setEventType(EventType.TRADE_AMENDED);
                case CANCELLED ->
                    event.setEventType(EventType.TRADE_CANCELED);
                default -> {
                    throw new TxnProcessingException("Invalid Status: " + txn.getTxnStatus().getCode());
                }
            }
            event.setSourceId(txn.getIdFinancialTxn());
            event.setEventStatus(AccountingEventStatus.NEW);
            event.setSourceType(EventSourceType.TRADE);
            event.setEventKey(txn.getMasterData().getCode() + " [" + txn.getIdFinancialTxn() + "]");
            event.setCreatedAt(LocalDateTime.now());
            event.setGeneratedBy(txn.getMasterData().getIdMasterData());
            event.setGeneratedRef("");
            accountingEventDAO.saveOrUpdate(event);
        } catch (Exception ex) {
            LoggerMgr.logInfo(ex.getLocalizedMessage());
            throw new TxnProcessingException("Invalid Status: " + txn.getTxnStatus().getCode());
        }
    }
    /*
    private boolean postElabFinancialTxn(FinancialTxn txn) {
        try {
            String userDir = System.getProperty("user.dir");
            Path scriptPath;
            log.info("=== [BATCH START] postElabFinancialTxn ===");

            // Se l'IDE si trova già dentro "easy_pricer_proc", il percorso parte da "./scripts/"
            if (userDir.endsWith("easy_pricer_proc")) {
                scriptPath = Paths.get(userDir, "scripts", "accounting_rules.groovy");
            } else {
                // Se l'IDE è avviato dalla radice globale, aggiungiamo il nome del sotto-modulo
                scriptPath = Paths.get(userDir, "easy_pricer_proc", "scripts", "accounting_rules.groovy");
            }
            File file = scriptPath.toFile();
            long currentModifiedTime = file.lastModified();

            // CONTROLLO TIMESTAMP: Rilanciamo la compilazione solo se il file è cambiato o non è mai stato caricato
            if (cachedScript == null || currentModifiedTime > lastScriptModifiedTime) {
                log.info("Loading the Groovy script...");

                if (groovyEngine instanceof Compilable compilableEngine) {
                    try (FileReader reader = new FileReader(file)) {
                        // Compiliamo lo script e lo salviamo in cache
                        this.cachedScript = compilableEngine.compile(reader);
                        this.lastScriptModifiedTime = currentModifiedTime;
                    } catch (IOException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                    }
                } else {
                    throw new ScriptException("The scripting engine does not support source compilation.");
                }
            }
            // Se il file non esiste sul disco, interrompiamo subito
            if (!file.exists()) {
                log.error("Script not found in: " + file.getAbsolutePath());
                LoggerMgr.logError("Script not found in: " + file.getAbsolutePath());
                return false;
            }
            // Usiamo il motore globale, ma isoliamo i dati della transazione corrente
            // in un oggetto Bindings locale al thread di esecuzione.
            JournalDsl dsl = new JournalDsl();
            AccountingContext ctx = new AccountingContext(txn, dsl, null);
            Bindings bindings = new SimpleBindings();
            bindings.put("ctx", ctx);

            bindings.put(
                    ScriptEngine.FILENAME,
                    scriptPath.toAbsolutePath().toString()
            );

            groovyEngine.eval(new FileReader(scriptPath.toFile()), bindings);
            log.info("Script result: " + dsl.build());

            return true;

        } catch (FileNotFoundException | ScriptException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return false;
        }
    }
     */
}
