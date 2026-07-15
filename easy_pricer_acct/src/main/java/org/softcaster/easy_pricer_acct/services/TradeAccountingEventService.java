/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import java.io.File;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.JournalEntries;
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalDsl;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeAccountingEventService extends BaseAccountingEventService {

    private static final Logger log = LoggerFactory.getLogger(TradeAccountingEventService.class);

    @Autowired
    private FinancialTxnDAO financialTxnDAO;

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
            FinancialTxn txn = financialTxnDAO.findByIdWithMasterData(event.getSourceId());
            if (txn == null) {
                throw new AccountingException(" Invalid txn!");
            }

            // Inizializzazione del DSL contabile e del contesto
            JournalDsl dsl = new JournalDsl();
            AccountingContext ctx = new AccountingContext(txn, dsl, txn.getMasterData().getCurrency().getIdCurrency(), event);

            // Predisposizione dei Bindings condivisibili dagli script
            Bindings bindings = new SimpleBindings();
            bindings.put("ctx", ctx);
            bindings.put(ScriptEngine.FILENAME, this.mainScriptAbsolutePath);
            bindings.put("accountResolver", accountResolverService);

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
                    // se lo script ha generato delle line contabili allora aggiorno
                    // su db (testata + linee)
                    addJournalEntries(ctx);
                    // Aggiorno stato accounting event
                    event.setEventStatus(AccountingEventStatus.PROCESSED);
                    accountingEventDAO.saveOrUpdate(event);
                    // Aggiorno stato contabile
                    txn.setTxnAcctPhase(ctx.getAccountingPhase());
                    financialTxnDAO.saveOrUpdate(txn);
                } else {
                    log.warn("No specific strategy script found cached for Asset Class: {}", assetCode);
                }
            }
        } catch (Exception ex) {
            String error = "Error executing script for event " + event.getEventId() + ": " + ex.getLocalizedMessage();
            LoggerMgr.logError(error);

            // Chiamiamo un metodo dedicato per marcare l'evento come FAILED su una transazione pulita ed autonoma
            eventStatusService.markEventAsFailed(event.getEventId());

            // Rilanciamo la RuntimeException per costringere Hibernate a fare il ROLLBACK di tutto il resto (giornale, linee, ecc.)
            throw new AccountingException(error);
        }
    }

    @Override
    protected void completeJournalEntries(JournalEntries entry, AccountingContext ctx) {
        entry.setBusinessDate(ctx.getTxn().getTradeDate());
        entry.setDescription(ctx.getTxn().getDescription());
        entry.setReference("txn: " + ctx.getTxn().getIdFinancialTxn());
    }
}
