/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.jobs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.TxnStatusDAO;
import org.softcaster.easy_pricer_proc.processors.ITxnProcessor;
import org.softcaster.easy_pricer_proc.processors.ProcessorDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ep
 */
@Component
public class FinTxnPollingJob {

    private static final Logger log = LoggerFactory.getLogger(FinTxnPollingJob.class);

    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private PositionDetailDAO positionRepository;
    @Autowired
    private ProcessorDispatcher processorDispatcher;
    @Autowired
    private TxnStatusDAO txnStatusDAO;
    @Autowired
    private ScriptEngine groovyEngine;

    private CompiledScript cachedScript = null;
    private long lastScriptModifiedTime = 0;

    /*
    @Value("${app.scripts.path}")
    private String scriptsPath;

    
    @Autowired
    private EasyPricerEngine engine;
     */
    protected boolean elabFinancialTxn(FinancialTxn txn, PositionDetail position) {

        if (!preElabFinancialTxn(txn)) {
            return false;
        }

        ITxnProcessor processor = processorDispatcher.dispatch(txn.getMasterData().getAssetClass().getCode());
        if (processor != null) {
            // Serve per calcolo unrealizedPnL
            position.setMarketPrice(txn.getPrice());
            processor.process(txn, position);
            return true;
        } else {
            return false;
        }
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

    protected boolean elabFinancialTxnList(List<FinancialTxn> financialTxnList) {
        for (FinancialTxn txn : financialTxnList) {
            try {
                // 1. Ricerca 
                PositionDetail position = getPositionDetail(txn);

                // 2. Elaborazione
                log.info("### Processing ID: {}", txn.getIdFinancialTxn());
                if (elabFinancialTxn(txn, position)) {
                    // 3. Salvataggio position e aggiornamento status transazione
                    positionRepository.saveOrUpdate(position);
                    if (txn.getTxnStatus().getCode().equalsIgnoreCase("PENDING")) {
                        txn.setTxnStatus(txnStatusDAO.findByCode("EXECUTED"));
                    } else if (txn.getTxnStatus().getCode().equalsIgnoreCase("CANCELLED")) {
                        txn.setTxnStatus(txnStatusDAO.findByCode("CANCELLED_EXECUTED"));
                    }
                    financialTxnDAO.saveOrUpdate(txn);
                }

            } catch (Exception e) {
                log.error("### ERROR ID {}: {}", txn.getIdFinancialTxn(), e.getMessage());
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

    protected void pollCancelledTrades() {
        // 1. Cerca le transazioni CANCELLED
        List<FinancialTxn> cancelledTxn = financialTxnDAO.findByTxnStatusCode("CANCELLED");

        if (!cancelledTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} CANCELLED transaction(s) ===", cancelledTxn.size());
            elabFinancialTxnList(cancelledTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    // Esegue il polling ogni 15 secondi (15000 millisecondi)
    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void pollTrades() {
        // 1. Elabora le transazioni PENDING
        pollPendingTrades();

        // 2. Elabora le transazioni CANCELLED
        pollCancelledTrades();
    }

    private boolean preElabFinancialTxn(FinancialTxn txn) {
    try {
        String userDir = System.getProperty("user.dir");
        Path scriptPath;

        // Gestione del percorso (lasciata invariata)
        if (userDir.endsWith("easy_pricer_proc")) {
            scriptPath = Paths.get(userDir, "scripts", "accounting_rules.groovy");
        } else {
            scriptPath = Paths.get(userDir, "easy_pricer_proc", "scripts", "accounting_rules.groovy");
        }

        File file = scriptPath.toFile();
        long currentModifiedTime = file.lastModified();

        // Se il file non esiste sul disco, interrompiamo subito
        if (!file.exists()) {
            log.error("Script non trovato in: " + file.getAbsolutePath());
            return false;
        }

        // CONTROLLO TIMESTAMP: Rilanciamo la compilazione solo se il file è cambiato o non è mai stato caricato
        if (cachedScript == null || currentModifiedTime > lastScriptModifiedTime) {
            log.info("Caricamento (o ricaricamento) dello script Groovy in corso...");
            
            if (groovyEngine instanceof Compilable compilableEngine) {
                try (FileReader reader = new FileReader(file)) {
                    // Compiliamo lo script e lo salviamo in cache
                    this.cachedScript = compilableEngine.compile(reader);
                    this.lastScriptModifiedTime = currentModifiedTime;
                    log.info("Script Groovy compilato con successo e salvato in cache.");
                }
            } else {
                throw new ScriptException("Il motore di scripting non supporta la compilazione dei sorgenti.");
            }
        }

        // Prepariamo i bindings locali per il thread corrente
        Bindings bindings = new SimpleBindings();
        bindings.put("txn", txn);
        
        // Questo serve ancora a NetBeans se decidi di fare debug dopo una modifica al volo
        bindings.put(ScriptEngine.FILENAME, file.getAbsolutePath());

        // Eseguiamo lo script pre-compilato dalla memoria (velocissimo)
        Object result = cachedScript.eval(bindings);
        log.info("Risultato script: " + result);

        return true;

    } catch (IOException | ScriptException ex) { 
        LoggerMgr.logError(ex.getLocalizedMessage());
        return false;
    }
}

}
