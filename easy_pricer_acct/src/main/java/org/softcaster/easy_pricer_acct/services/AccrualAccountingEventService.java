/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_acct.services;

import java.io.File;
import java.time.LocalDate;
import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.JournalEntries;
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalDsl;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccrualAccountingEventService extends BaseAccountingEventService {

    private static final Logger log = LoggerFactory.getLogger(AccrualAccountingEventService.class);

    @Autowired
    private MasterDataDAO masterDataDAO;

    @Autowired
    private PositionDetailDAO positionDetailDAO;

    @Transactional // <--- Fondamentale: ogni evento viene elaborato e committato singolarmente
    public void processEvent(AccountingEvent event) {
        try {
            if (event instanceof AccountingEventAccruals eventAccrual) {

                MasterData masterData = null;
                PositionDetail detail = positionDetailDAO.findByIdPositionDetail(eventAccrual.getPositionDetail());
                if (detail != null) {
                    Integer masterDataId = detail.getMasterData();
                    if (masterDataId != null && masterDataId > 0) {
                        masterData = masterDataDAO.findByIdMasterData(masterDataId);
                    }
                }

                if (masterData == null) {
                    // Chiamiamo un metodo dedicato per marcare l'evento come FAILED su una transazione pulita ed autonoma
                    eventStatusService.markEventAsFailed(event.getEventId());
                    throw new AccountingException("Invalid accounting event");

                }
                // Inizializzazione del DSL contabile e del contesto
                JournalDsl dsl = new JournalDsl();
                AccountingContext ctx = new AccountingContext(null, dsl, masterData.getMasterDataCcy().getIdCurrency(), 
                        masterData.getSettlementCcy().getIdCurrency(), eventAccrual);

                // Predisposizione dei Bindings condivisibili dagli script
                Bindings bindings = new SimpleBindings();
                bindings.put("ctx", ctx);
                bindings.put(ScriptEngine.FILENAME, this.mainScriptAbsolutePath);
                bindings.put("accountResolver", accountResolverService);

                // 4. Esecuzione delle regole contabili generali (Orchestratore principale)
                cachedMainScript.eval(bindings);

                // Esecuzione dinamica della strategia specifica di Asset Class
                String assetClass = masterData.getAssetClass().getCode();
                if (!assetClass.isBlank()) {
                    // Recuperiamo la strategia pre-compilata dalla cache
                    CompiledScript assetStrategy = cachedStrategies.get(assetClass);

                    if (assetStrategy != null) {
                        // Aggiorna il FILENAME con il percorso specifico della sotto-strategia prima del lancio
                        String strategyDebugPath = "strategies" + File.separator + assetClass + ".groovy";
                        // Eseguiamo la sotto-strategia condividendo lo stesso contesto contabile
                        bindings.put(ScriptEngine.FILENAME, strategyDebugPath);
                        assetStrategy.eval(bindings);
                        // se lo script ha generato delle line contabili allora aggiorno
                        // su db (testata + linee)
                        addJournalEntries(ctx);
                        // Aggiorno stato accounting event
                        event.setEventStatus(AccountingEventStatus.PROCESSED);
                        accountingEventDAO.saveOrUpdate(event);
                    } else {
                        log.warn("No specific strategy script found cached for Asset Class: {}", assetClass);
                    }
                } else {
                    // Chiamiamo un metodo dedicato per marcare l'evento come FAILED su una transazione pulita ed autonoma
                    eventStatusService.markEventAsFailed(event.getEventId());
                    throw new AccountingException("Invalid accounting event");
                }
            }
        } catch (Exception ex) {

            String error = "Error executing script for event " + event.getEventId() + ": " + ex.getLocalizedMessage();
            LoggerMgr.logError(error);

            eventStatusService.markEventAsFailed(event.getEventId());

            // Rilanciamo la RuntimeException per costringere Hibernate a fare il ROLLBACK di tutto il resto 
            throw new AccountingException(error);
        }
    }

    @Override
    protected void completeJournalEntries(JournalEntries entry, AccountingContext ctx) {
        LocalDate businnesDate = ctx.getEvent().getCreatedAt().toLocalDate();
        entry.setBusinessDate(java.sql.Date.valueOf(businnesDate));
        entry.setDescription("Accruals");
        entry.setReference("detail: " + ctx.getEvent().getPositionDetail());
    }
}
