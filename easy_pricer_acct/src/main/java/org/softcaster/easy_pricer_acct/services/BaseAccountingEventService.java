/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.core.data.account.GlAccount;
import org.softcaster.core.data.account.GlAccountDAO;
import org.softcaster.core.data.account.GlAccountSlots;
import org.softcaster.core.data.account.GlAccountSlotsDAO;
import org.softcaster.core.data.account.JournalEntries;
import org.softcaster.core.data.account.JournalEntriesDAO;
import org.softcaster.core.data.account.JournalEntryLines;
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalLine;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.softcaster.engine.enums.JournalEntryStatus;
import org.softcaster.engine.enums.JournalEntryType;
import org.softcaster.engine.enums.NormalBalance;
import static org.softcaster.engine.enums.NormalBalance.CREDIT;
import static org.softcaster.engine.enums.NormalBalance.DEBIT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class BaseAccountingEventService {

    private static final Logger log = LoggerFactory.getLogger(TradeAccountingEventService.class);

    // Variabile "volatile" per garantire la visibilità immediata tra i thread dopo l'init
    protected volatile CompiledScript cachedMainScript = null;

    // Cache per le strategie delle singole Asset Class (FSP, EQ, ecc.)
    protected final Map<String, CompiledScript> cachedStrategies = new ConcurrentHashMap<>();

    // Memorizza i percorsi assoluti
    protected String mainScriptAbsolutePath = "";

    @Autowired
    protected ScriptEngine groovyEngine;

    @Autowired
    protected JournalEntriesDAO journalEntriesDAO;

    @Autowired
    protected GlAccountDAO glAccountDAO;

    @Autowired
    protected AccountingEventDAO accountingEventDAO;

    @Autowired
    protected AccountResolverService accountResolverService;

    @Autowired
    protected AccountingEventStatusService eventStatusService;

    @Autowired
    private GlAccountSlotsDAO glAccountSlotsDAO;

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
                //this.strategiesFolderAbsolutePath = strategiesPath.toFile().getAbsolutePath();
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

    protected abstract void completeJournalEntries(JournalEntries entry, AccountingContext ctx);

    protected void addJournalEntries(AccountingContext ctx) {
        List<JournalLine> lines = ctx.getJournal().build();
        if (lines.isEmpty()) {
        }

        checkCurrencyBalancing(lines);

        JournalEntries entry = new JournalEntries();
        entry.setAccountingEvent(ctx.getEvent());
        entry.setEntryStatus(JournalEntryStatus.UNCONSOLIDATED);
        entry.setEntryType(JournalEntryType.ACCOUNTING);
        entry.setCreatedAt(LocalDateTime.now());
        completeJournalEntries(entry, ctx);
        for (JournalLine line : lines) {
            JournalEntryLines jel = getJournalEntryLines(line);
            if (jel != null) {
                // addLine aggiorna anche LineNo
                entry.addLine(jel);
            } else {
                String error = "Invalid JournalEntryLines null value";
                log.warn(error);
                LoggerMgr.logWarning(error);
                throw new AccountingException(error);
            }
        }
        journalEntriesDAO.saveOrUpdate(entry);
    }

    private JournalEntryLines getJournalEntryLines(JournalLine line) {
        JournalEntryLines jel = new JournalEntryLines();
        jel = new JournalEntryLines();
        jel.setDebitAmount(0.);
        jel.setCreditAmount(0.);

        Integer finalAccountSlotId;
        String glAccountDescription = "";
        String accountKey = line.account();
        // Controllo se è una linea di storno
        if (accountKey != null && accountKey.startsWith("SLOT:")) {
            finalAccountSlotId = Integer.valueOf(accountKey.substring(5));
            glAccountDescription = finalAccountSlotId.toString();
        } else {
            // Gestione standard
            GlAccount glAccount = glAccountDAO.findByCode(line.account());
            if (glAccount == null) {
                throw new AccountingException(" Invalid account!");
            }
            glAccountDescription = glAccount.getCode();
            // Conto e divisa determinano la slot da utilizzare
            GlAccountSlots glAccountSlots = glAccountSlotsDAO.findByAccountAndCurrency(glAccount.getAccountId(), line.currency());
            if (glAccountSlots == null) {
                throw new AccountingException(" Invalid slot!");
            }
            finalAccountSlotId = glAccountSlots.getAccountSlotId();
        }
        // Conto e divisa determinano la slot da utilizzare
        jel.setAccountSlot(finalAccountSlotId);
        jel.setCurrency(line.currency());
        jel.setDescription(glAccountDescription);
        switch (line.balance()) {
            case DEBIT ->
                jel.setDebitAmount(line.amount());
            case CREDIT ->
                jel.setCreditAmount(line.amount());
            default ->
                throw new AccountingException(" Invalid balance!");
        }

        return jel;
    }

    /**
     * Controlla che ogni valuta coinvolta nel DSL sia perfettamente quadrata a
     * zero. Se una valuta è sbilanciata, lancia una AccountingException
     * bloccando il flusso.
     */
    private void checkCurrencyBalancing(List<JournalLine> lines) {
        Map<Integer, Double> balanceMap = new HashMap<>();

        for (JournalLine line : lines) {
            int ccyId = line.currency();
            double amount = line.amount();

            // DEBIT incrementa il saldo del registro, CREDIT lo decrementa
            double effect = (line.balance() == NormalBalance.DEBIT) ? amount : -amount;
            balanceMap.put(ccyId, balanceMap.getOrDefault(ccyId, 0.0) + effect);
        }

        // Tolleranza per micro-frazioni decimali (es. arrotondamenti float/double)
        double tolerance = 0.00001;

        for (Map.Entry<Integer, Double> entry : balanceMap.entrySet()) {
            double balance = entry.getValue();
            if (Math.abs(balance) > tolerance) {
                String outOfBalanceError = String.format(
                        "### CRITICAL ACCOUNTING MISMATCH: Currency ID %d is out of balance by %.5f!",
                        entry.getKey(), balance);
                log.error(outOfBalanceError);
                LoggerMgr.logError(outOfBalanceError);
                throw new AccountingException(outOfBalanceError);
            }
        }
    }
}
