/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.jobs;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalDsl;
import org.softcaster.easy_pricer_acct.services.EngineStateManager;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AcctPollingJob {

    private static final Logger log = LoggerFactory.getLogger(AcctPollingJob.class);
    private CompiledScript cachedScript = null;
    private long lastScriptModifiedTime = 0;

    @Autowired
    private EngineStateManager engineStateManager;
    @Autowired
    private ScriptEngine groovyEngine;

    private void loadScript() {
        try {
            String userDir = System.getProperty("user.dir");
            Path scriptPath;
            log.info("=== [BATCH START]  ===");

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
                return;
            }
            // Usiamo il motore globale, ma isoliamo i dati della transazione corrente
            // in un oggetto Bindings locale al thread di esecuzione.
            JournalDsl dsl = new JournalDsl();
            AccountingContext ctx = new AccountingContext(null, dsl, EventType.TRADE_EXECUTED);
            Bindings bindings = new SimpleBindings();
            bindings.put("ctx", ctx);

            bindings.put(
                    ScriptEngine.FILENAME,
                    scriptPath.toAbsolutePath().toString()
            );

            groovyEngine.eval(new FileReader(scriptPath.toFile()), bindings);
            log.info("Script result: " + dsl.build());

        } catch (FileNotFoundException | ScriptException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }

    }

    // Esegue il polling ogni 15 secondi (15000 millisecondi)
    @Scheduled(fixedDelay = 15000)
    public void pollTrades() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [PSRV] Service is suspended ===\n");
            return;
        }
        loadScript();
    }
}
