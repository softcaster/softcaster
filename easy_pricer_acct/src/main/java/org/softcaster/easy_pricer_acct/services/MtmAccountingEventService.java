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
import org.softcaster.core.data.account.AccountingEventMtm;
import org.softcaster.core.data.account.AccountingEventMtmDAO;
import org.softcaster.core.data.account.JournalEntries;
import org.softcaster.easy_pricer_acct.context.AccountingContext;
import org.softcaster.easy_pricer_acct.context.JournalDsl;
import org.softcaster.easy_pricer_acct.exceptions.AccountingException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Eventi MTM di fine giornata (accounting_event_mtm): evento di posizione,
 * senza transazione. Il contesto riceve la fotografia del giorno e l'ultima
 * gia' contabilizzata della stessa posizione; lo script dell'asset class
 * registra la variazione (FUT: margine di variazione, XRB: fair value).
 *
 * Gli eventi vanno elaborati in ordine di data (AcctPollingJob li elabora uno
 * alla volta): la variazione si calcola sull'ultima fotografia PROCESSED.
 */
@Service
public class MtmAccountingEventService extends BaseAccountingEventService {

    private static final Logger log = LoggerFactory.getLogger(MtmAccountingEventService.class);

    // limite superiore per "qualunque data" (LocalDate.MAX non sta in una DATE di PostgreSQL)
    private static final LocalDate LAST_DATE = LocalDate.of(9999, 12, 31);

    @Autowired
    private MasterDataDAO masterDataDAO;

    @Autowired
    private PositionDetailDAO positionDetailDAO;

    @Autowired
    private AccountingEventMtmDAO accountingEventMtmDAO;

    @Transactional // ogni evento viene elaborato e committato singolarmente
    public void processEvent(AccountingEvent event) {
        try {
            if (!(event instanceof AccountingEventMtm eventMtm)) {
                throw new AccountingException("Invalid MTM event");
            }

            PositionDetail detail = positionDetailDAO.findByIdPositionDetail(eventMtm.getPositionDetail());
            MasterData masterData = detail == null ? null : masterDataDAO.findByIdMasterData(detail.getMasterData());
            if (masterData == null) {
                throw new AccountingException("Invalid accounting event: position detail " + eventMtm.getPositionDetail());
            }

            // una fotografia piu' recente gia' contabilizzata copre anche questa data:
            // registrarla ora conterebbe due volte la stessa variazione
            accountingEventMtmDAO.findLastPosted(eventMtm.getPositionDetail(), LAST_DATE)
                    .filter(last -> last.getValuationDate().isAfter(eventMtm.getValuationDate()))
                    .ifPresent(last -> {
                        throw new AccountingException("MTM of " + eventMtm.getValuationDate()
                                + " superseded by the posted MTM of " + last.getValuationDate());
                    });

            JournalDsl dsl = new JournalDsl();
            AccountingContext ctx = new AccountingContext(null, dsl, masterData.getMasterDataCcy(),
                    masterData.getSettlementCcy(), eventMtm);
            ctx.setPreviousMtm(accountingEventMtmDAO.findLastPosted(eventMtm.getPositionDetail(), eventMtm.getValuationDate())
                    .orElse(null));
            ctx.setMasterData(masterData);

            Bindings bindings = new SimpleBindings();
            bindings.put("ctx", ctx);
            bindings.put(ScriptEngine.FILENAME, this.mainScriptAbsolutePath);
            bindings.put("accountResolver", accountResolverService);

            // regole contabili generali
            cachedMainScript.eval(bindings);

            String scriptCode = strategyCode(masterData.getAssetClass(), event.getEventType());
            CompiledScript assetStrategy = cachedStrategies.get(scriptCode);
            if (assetStrategy == null) {
                throw new AccountingException("No strategy script " + scriptCode + " for asset class "
                        + masterData.getAssetClass().getCode());
            }
            bindings.put(ScriptEngine.FILENAME, "strategies" + File.separator + scriptCode + ".groovy");
            assetStrategy.eval(bindings);
            addJournalEntries(ctx);

            event.setEventStatus(AccountingEventStatus.PROCESSED);
            accountingEventDAO.saveOrUpdate(event);
        } catch (Exception ex) {
            String error = "Error executing script for event " + event.getEventId() + ": " + ex.getLocalizedMessage();
            log.error(error);
            LoggerMgr.logError(error);
            eventStatusService.markEventAsFailed(event.getEventId());
            // ROLLBACK di giornale e linee
            throw new AccountingException(error);
        }
    }

    @Override
    protected void completeJournalEntries(JournalEntries entry, AccountingContext ctx) {
        AccountingEventMtm eventMtm = (AccountingEventMtm) ctx.getEvent();
        entry.setBusinessDate(java.sql.Date.valueOf(eventMtm.getValuationDate()));
        entry.setDescription("Mtm");
        entry.setReference("detail: " + eventMtm.getPositionDetail());
    }
}
