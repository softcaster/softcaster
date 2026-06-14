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
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalDsl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TradeAccountingEventService {

    private static final Logger log = LoggerFactory.getLogger(TradeAccountingEventService.class);

    // Variabile "volatile" per garantire la visibilità immediata tra i thread dopo l'init
    private volatile CompiledScript cachedScript = null;
    private String scriptAbsolutePath = "";

    @Autowired
    private ScriptEngine groovyEngine;

    /**
     * COMPILAZIONE UNICA ALL'AVVIO Avviene una sola volta, all'avvio del
     * Service di Spring.
     */
    @PostConstruct
    public void init() {
        log.info("=== [INITIALIZING ACCOUNTING SCRIPT] ===");
        try {
            String userDir = System.getProperty("user.dir");
            Path scriptPath;

            if (userDir.endsWith("easy_pricer_acct")) {
                scriptPath = Paths.get(userDir, "scripts", "accounting_rules.groovy");
            } else {
                scriptPath = Paths.get(userDir, "easy_pricer_acct", "scripts", "accounting_rules.groovy");
            }

            File file = scriptPath.toFile();
            this.scriptAbsolutePath = file.getAbsolutePath();

            if (!file.exists()) {
                log.error("CRITICAL: Script not found in: " + scriptAbsolutePath);
                LoggerMgr.logError("Script not found in: " + scriptAbsolutePath);
                return;
            }

            // Configura il motore e compila lo script
            groovyEngine.put(ScriptEngine.FILENAME, scriptAbsolutePath);

            if (groovyEngine instanceof Compilable compilableEngine) {
                try (FileReader reader = new FileReader(file)) {
                    this.cachedScript = compilableEngine.compile(reader);
                    log.info("Script compiled successfully and cached for lifecycle.");
                }
            } else {
                throw new ScriptException("The scripting engine does not support source compilation.");
            }

        } catch (IOException | ScriptException ex) {
            log.error("Failed to compile accounting script during startup!", ex);
            LoggerMgr.logError("Script compilation failed: " + ex.getLocalizedMessage());
        }
    }

    /**
     *
     * @param event
     */
    @Transactional // <--- Fondamentale: ogni evento viene elaborato e committato singolarmente
    public void processEvent(AccountingEvent event) {
        log.info("Process event: {}", event.getEventId());

        if (cachedScript == null) {
            log.error("Skipping event {}. Script was not compiled during startup.", event.getEventId());
            return;
        }

        try {
            JournalDsl dsl = new JournalDsl();
            // Utilizza il tipo di evento corrente in modo dinamico
            AccountingContext ctx = new AccountingContext(null, dsl, event.getEventType());

            Bindings bindings = new SimpleBindings();
            bindings.put("ctx", ctx);
            bindings.put(ScriptEngine.FILENAME, this.scriptAbsolutePath);

            // Esecuzione immediata in memoria del codice pre-compilato (Thread-Safe)
            cachedScript.eval(bindings);

            log.info("Script result for event {}: {}", event.getEventId(), dsl.build());

        } catch (ScriptException ex) {
            LoggerMgr.logError("Error executing script for event " + event.getEventId() + ": " + ex.getLocalizedMessage());
        }
    }
}
