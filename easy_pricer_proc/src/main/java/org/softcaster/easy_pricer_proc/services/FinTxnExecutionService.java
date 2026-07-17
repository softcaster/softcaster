/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.services;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionTxnLinks;
import org.softcaster.core.data.PositionTxnLinksDAO;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.easy_pricer_proc.processors.ITxnProcessor;
import org.softcaster.easy_pricer_proc.processors.ProcessorDispatcher;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import static org.softcaster.engine.enums.TxnStatus.EXECUTED;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinTxnExecutionService {

    private static final Logger log = LoggerFactory.getLogger(FinTxnExecutionService.class);

    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private ProcessorDispatcher processorDispatcher;
    @Autowired
    AccountingEventDAO accountingEventDAO;
    @Autowired
    PositionTxnLinksDAO positionTxnLinksDAO;
    @Autowired
    private SystemBusinessCalendarDAO systemBusinessCalendarDAO;

    // Richiede una NUOVA transazione per salvare lo stato di REJECTED 
    // anche se la transazione principale fallisce e fa rollback
    // Vedere commento a FinTxnPollingJob.elabFinancialTxnList
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatusOnFailure(Integer txnId, TxnStatus status) {
        try {
            FinancialTxn txn = financialTxnDAO.findByIdWithMasterData(txnId);
            if (txn != null) {
                txn.setTxnStatus(status);
                financialTxnDAO.saveOrUpdate(txn);
            }
        } catch (Exception ex) {
            log.error("Could not update status to REJECTED for txnId {}", txnId, ex);
        }
    }

    // REQUIRES_NEW sospende la transazione del Job e ne apre una nuova di zecca.
    // Al fallimento, distrugge l'intera sessione di Hibernate locale, ripulendo la memoria.
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public void elabFinancialTxn(Integer txnId) {
        FinancialTxn txn = financialTxnDAO.findByIdWithMasterData(txnId);

        // Determino nuovo stato "potenziale"
        TxnStatus newStatus = TxnStatus.REJECTED;
        switch (txn.getTxnStatusPreElab()) {
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
            log.error("Invalid Status " + txn.getTxnStatus().getCode());
            throw new TxnProcessingException("Invalid Status");
        }

        try {
            elabFinancialTxn(txn, newStatus);
            //generateAccountingEvent(txn, newStatus);
            updateStatus(txn, newStatus);
            log.info("Processed transaction: " + txn.getIdFinancialTxn());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw e;
        }
    }

    private void insertLink(FinancialTxn txn, PositionDetail position) {
        PositionTxnLinks link = new PositionTxnLinks();
        link.setFinancialTxn(txn.getIdFinancialTxn());
        link.setTxnAcctPhase(txn.getTxnAcctPhase());
        double sign = txn.getTxnSide() == TxnSide.BUY ? 1. : -1.;
        link.setQuantity(txn.getQuantity() * sign);
        link.setPrice(txn.getPrice());
        link.setFxRate(txn.getFxRate());
        link.setSettlement(txn.getSettlement());
        link.setPositionDetail(position.getIdPositionDetail());
        positionTxnLinksDAO.saveOrUpdate(link);
    }

    private void elabFinancialTxn(FinancialTxn txn, TxnStatus status) {

        PositionDetail position = getPositionDetail(txn);
        processFinancialTxn(txn, position);
        position.setLastMtmExecuted(LocalDateTime.now());
        PositionDetail lastSaved = positionDetailDAO.saveOrUpdate(position);
        // A questo punto salvo il link
        if (status == TxnStatus.EXECUTED) {
            insertLink(txn, position);
        }
        // Genero accounting event 
        generateAccountingEvent(txn, lastSaved.getIdPositionDetail(), status);
    }

    private String getEventKey(FinancialTxn txn, TxnStatus status) {

        String eventKey = txn.getMasterData().getCode() + " [" + txn.getIdFinancialTxn() + "] " + "[" + status.getCode() + "]" + txn.getSettlement();
        return eventKey;
    }

    private void generateAccountingEvent(FinancialTxn txn, Integer positionDetailId, TxnStatus status) {
        if (txn == null) {
            log.error("Invalid Txn");
            throw new TxnProcessingException("Invalid Txn");
        }

        String eventKey = getEventKey(txn, status);
        // Controlla se evento e`gia`stato generato
        AccountingEvent event = accountingEventDAO.findByEventKey(eventKey);
        if (event != null) {
            log.error("Event already generated.");
            return;
        }

        try {
            // Genero AccountingEvent
            event = new AccountingEvent();
            switch (status) {
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
            event.setEventKey(eventKey);
            event.setCreatedAt(LocalDateTime.now());
            event.setGeneratedBy(txn.getMasterData().getIdMasterData());
            event.setGeneratedRef("");
            event.setPositionDetail(positionDetailId);
            accountingEventDAO.saveOrUpdate(event);
        } catch (Exception ex) {
            LoggerMgr.logInfo(ex.getLocalizedMessage());
            log.error(ex.getLocalizedMessage());
            throw new TxnProcessingException("IAccounting event generation failed");
        }
    }

    private void updateStatus(FinancialTxn txn, TxnStatus status) {

        txn.setTxnStatus(status);
        financialTxnDAO.saveOrUpdate(txn);
    }

    private PositionDetail getPositionDetail(FinancialTxn txn) {
        PositionDetail position = positionDetailDAO.findByPositionMdAndMasterDataAndCounterparty(
                txn.getPositionMd().getIdPosition(), txn.getMasterData().getIdMasterData(),
                txn.getCounterparty().getIdCounterparty()).orElseGet(() -> {
            PositionDetail newPosition = new PositionDetail();
            newPosition.setPositionMd(txn.getPositionMd().getIdPosition());
            newPosition.setMasterData(txn.getMasterData().getIdMasterData());
            newPosition.setCounterparty(txn.getCounterparty().getIdCounterparty());
            newPosition.setOfficialDate(systemBusinessCalendarDAO.findBySbcId(1).getOfficialDate());
            newPosition.initialize();
            return newPosition;
        });

        return position;
    }

    private void processFinancialTxn(FinancialTxn txn, PositionDetail position) {

        ITxnProcessor processor = processorDispatcher.dispatch(txn.getMasterData().getAssetClass().getCode());
        if (processor != null) {
            processor.process(txn, position);
        } else {
            log.error("Invalid ITxnProcessor");
            throw new TxnProcessingException("Invalid ITxnProcessor");
        }
    }
}
