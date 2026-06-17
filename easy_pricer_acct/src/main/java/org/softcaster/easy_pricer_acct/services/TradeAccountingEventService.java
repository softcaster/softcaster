/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalDsl;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeAccountingEventService {

    private static final Logger log = LoggerFactory.getLogger(TradeAccountingEventService.class);

    // Variabile "volatile" per garantire la visibilità immediata tra i thread dopo l'init
    private volatile CompiledScript cachedMainScript = null;

    // Cache per le strategie delle singole Asset Class (FSP, EQ, ecc.)
    private final Map<String, CompiledScript> cachedStrategies = new ConcurrentHashMap<>();

    // Memorizza i percorsi assoluti
    private String mainScriptAbsolutePath = "";
    private String strategiesFolderAbsolutePath = "";

    @Autowired
    private ScriptEngine groovyEngine;

    @Autowired
    private FinancialTxnDAO financialTxnDAO;

    /**
     * COMPILAZIONE UNICA ALL'AVVIO Avviene una sola volta, all'avvio del
     * Service di Spring.
     */
    @PostConstruct
    public void init() {
        log.info("=== [INITIALIZING ACCOUNTING SCRIPT] ===");
        try {
            String userDir = System.getProperty("user.dir");
            Path baseScriptsPath = userDir.endsWith("easy_pricer_acct")
                    ? Paths.get(userDir, "scripts")
                    : Paths.get(userDir, "easy_pricer_acct", "scripts");

            // Salva il path assoluto del file principale
            File mainScriptFile = baseScriptsPath.resolve("accounting_rules.groovy").toFile();
            this.mainScriptAbsolutePath = mainScriptFile.getAbsolutePath();

            if (!mainScriptFile.exists()) {
                log.error("CRITICAL: Main script not found in: {}", mainScriptAbsolutePath);
                return;
            }

            if (groovyEngine instanceof Compilable compilableEngine) {

                // 1. COMPILAZIONE SCRIPT PRINCIPALE
                // Impostiamo il FILENAME nel contesto globale del motore prima di compilare
                groovyEngine.put(ScriptEngine.FILENAME, mainScriptFile.getAbsolutePath());
                try (FileReader reader = new FileReader(mainScriptFile)) {
                    this.cachedMainScript = compilableEngine.compile(reader);
                }

                // 2. COMPILAZIONE DELLE SOTTO-STRATEGIE
                Path strategiesPath = baseScriptsPath.resolve("strategies");
                this.strategiesFolderAbsolutePath = strategiesPath.toFile().getAbsolutePath();
                File strategiesDir = strategiesPath.toFile();

                if (strategiesDir.exists() && strategiesDir.isDirectory()) {
                    File[] files = strategiesDir.listFiles((dir, name) -> name.endsWith(".groovy"));
                    if (files != null) {
                        for (File file : files) {
                            String assetClassName = file.getName().replace(".groovy", "");

                            // Aggiorna il FILENAME nel motore per ogni singola strategia prima della compilazione
                            String debugPath = "strategies" + File.separator + file.getName();
                            groovyEngine.put(ScriptEngine.FILENAME, debugPath);
                            try (FileReader reader = new FileReader(file)) {
                                CompiledScript compiledStrategy = compilableEngine.compile(reader);
                                cachedStrategies.put(assetClassName, compiledStrategy);
                            }
                        }
                    }
                }
                log.info("All scripts compiled and cached successfully with debug info.");
            }
        } catch (IOException | ScriptException ex) {
            log.error("Failed to compile accounting script during startup!", ex);
        }
    }

    /**
     *
     * @param event
     */
    @Transactional // <--- Fondamentale: ogni evento viene elaborato e committato singolarmente
    public void processEvent(AccountingEvent event) {
        log.info("Process event: {}", event.getEventId());

        if (cachedMainScript == null) {
            log.error("Skipping event {}. Script was not compiled during startup.", event.getEventId());
            return;
        }

        try {
            // Carico txn
            FinancialTxn txn = financialTxnDAO.findByIdWithMasterData(event.getEventId());
            if (txn == null) {
                throw new AccountingException(" Invalid txn!");
            }

            // Inizializzazione del DSL contabile e del contesto
            JournalDsl dsl = new JournalDsl();
            AccountingContext ctx = new AccountingContext(txn, dsl, event);

            // Predisposizione dei Bindings condivisibili dagli script
            Bindings bindings = new SimpleBindings();
            bindings.put("ctx", ctx);
            bindings.put(ScriptEngine.FILENAME, this.mainScriptAbsolutePath);

            // 4. Esecuzione delle regole contabili generali (Orchestratore principale)
            cachedMainScript.eval(bindings);

            // Esecuzione dinamica della strategia specifica di Asset Class
            if (txn.getMasterData() != null && txn.getMasterData().getAssetClass() != null) {
                String assetCode = txn.getMasterData().getAssetClass().getCode();

                // Recuperiamo la strategia pre-compilata dalla cache
                CompiledScript assetStrategy = cachedStrategies.get(assetCode);

                if (assetStrategy != null) {
                    // Aggiorna il FILENAME con il percorso specifico della sotto-strategia prima del lancio
                    String strategyDebugPath = "strategies" + File.separator + assetCode + ".groovy";
                    // Eseguiamo la sotto-strategia condividendo lo stesso contesto contabile
                    bindings.put(ScriptEngine.FILENAME, strategyDebugPath);
                    assetStrategy.eval(bindings);
                } else {
                    log.warn("No specific strategy script found cached for Asset Class: {}", assetCode);
                }
            }

            // Output finale dei movimenti generati nel ciclo transazionale
            log.info("Script result for event {}: {}", event.getEventId(), dsl.build());

        } catch (ScriptException ex) {
            String error = "Error executing script for event " + event.getEventId() + ": " + ex.getLocalizedMessage();
            LoggerMgr.logError(error);
            throw new AccountingException(error);
        }
    }
}
