/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.TxnSide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BackdatedTxnProcessor {

    @Autowired
    private BondCalculator bondCalculator;

    private static final Logger log = LoggerFactory.getLogger(BackdatedTxnProcessor.class);

    private String getEventKeyTrade(FinancialTxn txn) {
        String eventKey = txn.getMasterData().getCode() + " [" + txn.getIdFinancialTxn() + "] " + "[" + EventType.SETTLEMENT.getCode() + "]" + txn.getSettlement();
        return eventKey;
    }

    private String getEventKeyCoupon(MasterData masterData, Integer positionDetailId, LocalDate endDate) {
        String eventKey = masterData.getCode() + " [" + positionDetailId + "] " + "[" + EventType.COUPON.getCode() + "]" + endDate;
        return eventKey;
    }

    public List<AccountingEvent> generateEvents(FinancialTxn txn, Integer positionDetailId, LocalDate officialDate) throws TxnProcessingException {

        List<AccountingEvent> events = new ArrayList<>();
        try {
            // Evento di settlement
            generateSettlementEvent(txn, positionDetailId, events);
            // Eventuali cedole antergate
            generateCouponEvents(officialDate, txn, positionDetailId, events);
            // Ultimo rateo
            generateAccrualEvents(officialDate, txn, positionDetailId, events);
        } catch (Exception e) {
            log.error("### Error processing txn: " + txn.getIdFinancialTxn());
            throw new TxnProcessingException(e.getLocalizedMessage());
        }

        return events;
    }

    private void generateSettlementEvent(FinancialTxn txn, Integer positionDetailId, List<AccountingEvent> events) {
        if (txn != null && events != null) {
            AccountingEvent event = null;
            try {
                String eventKey = getEventKeyTrade(txn);

                // Genero AccountingEvent
                event = new AccountingEvent();
                event.setSourceId(txn.getIdFinancialTxn());
                event.setEventStatus(AccountingEventStatus.NEW);
                event.setEventType(EventType.SETTLEMENT);
                event.setSourceType(EventSourceType.TRADE);
                event.setEventKey(eventKey);
                event.setCreatedAt(LocalDateTime.now());
                event.setPositionDetail(positionDetailId);
                event.setGeneratedBy(txn.getMasterData().getIdMasterData());
                event.setGeneratedRef("");
            } catch (Exception e) {
                log.error("### Error generating settlement event for  txn: " + txn.getIdFinancialTxn());
                throw new TxnProcessingException(e.getLocalizedMessage());
            }
            events.add(event);
        }
    }

    private void generateCouponEvents(LocalDate officialDate, FinancialTxn txn, Integer positionDetailId, List<AccountingEvent> events) {
        if (txn != null && events != null) {
            if (txn.getMasterData() instanceof SecurityMasterData smd) {
                if (smd != null) {
                    try {
                        List<CashFlowItem> cashFlow = smd.getCashFlows();
                        if (cashFlow != null && !cashFlow.isEmpty()) {
                            AccountingEventAccruals event = null;
                            for (CashFlowItem item : cashFlow) {
                                LocalDate settlement = txn.getSettlement().toLocalDate();
                                LocalDate start = item.getStartDate().toLocalDate();
                                LocalDate end = item.getEnddate().toLocalDate();
                                if (start.isAfter(officialDate)) {
                                    break;
                                }
                                if (end.isAfter(settlement) && end.isBefore(officialDate)) {
                                    event = new AccountingEventAccruals();
                                    event.setSourceId(txn.getIdFinancialTxn());
                                    event.setEventStatus(AccountingEventStatus.NEW);
                                    event.setEventType(EventType.COUPON);
                                    event.setSourceType(EventSourceType.INSTRUMENT);
                                    event.setEventKey(getEventKeyCoupon(smd, positionDetailId, item.getEnddate().toLocalDate()));
                                    event.setCreatedAt(LocalDateTime.now());
                                    event.setPositionDetail(positionDetailId);
                                    event.setGeneratedBy(txn.getMasterData().getIdMasterData());
                                    event.setGeneratedRef(smd.getCode());
                                    double operationalAmount = txn.getQuantity();
                                    if (txn.getTxnSide() == TxnSide.SELL) {
                                        operationalAmount *= -1;
                                    }
                                    double accrualAmount = (operationalAmount * item.getInterest() * smd.getMultiplier());
                                    // Intero coupon
                                    event.setAccrualAmount(accrualAmount);
                                    event.setAccountingNominal(operationalAmount);
                                    event.setCouponRate(item.getInterest());
                                    event.setDaycount(smd.getAccrualDaycount());
                                    LocalDate to = item.getEnddate().toLocalDate();
                                    LocalDate from = item.getStartDate().toLocalDate();
                                    int days = (int) java.time.temporal.ChronoUnit.DAYS.between(from, to);
                                    event.setDays(days);
                                    events.add(event);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("### Error generating coupon events for txn: " + txn.getIdFinancialTxn());
                        throw new TxnProcessingException(e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    // Ho 2 casi
    // 1) Nessun coupon scade tra trade date e official date, calcolo rateo tra settlement e official date
    // 2) Ho almeno una cedola tra trade date e official date. Allora calcolo rateo tra end-date dell' ultima
    // cedola e official date
    private void generateAccrualEvents(LocalDate officialDate, FinancialTxn txn, Integer positionDetailId, List<AccountingEvent> events) {
        if (txn != null && events != null) {
            if (txn.getMasterData() instanceof SecurityMasterData smd) {
                if (smd != null) {
                    try {
                        // scorro eventi per vedere se esiste un evento di stacco cedola
                        LocalDate lastEndDate = txn.getSettlement().toLocalDate();
                        List<CashFlowItem> cashFlow = smd.getCashFlows();
                        if (cashFlow != null && !cashFlow.isEmpty()) {
                            for (CashFlowItem item : cashFlow) {
                                LocalDate start = item.getStartDate().toLocalDate();
                                LocalDate end = item.getEnddate().toLocalDate();
                                if (start.isAfter(officialDate)) {
                                    break;
                                }
                                if (end.isAfter(lastEndDate) && end.isBefore(officialDate)) {
                                    AccountingEventAccruals accountingEvent = new AccountingEventAccruals();
                                    double accrualAmount = txn.getQuantity() * smd.getMultiplier();
                                    // accrual fino a settlement txn
                                    double accrualsFrom = bondCalculator.getAccruals(smd, lastEndDate);
                                    // A scadenza cedola rateo pieno
                                    double accrualsTo = item.getInterest();
                                    double accruals = (accrualsTo - accrualsFrom) * accrualAmount;
                                    accountingEvent.setAccrualAmount(accruals);
                                    accountingEvent.setAccountingNominal(accrualAmount);
                                    accountingEvent.setCouponRate(smd.getInterestRate());
                                    accountingEvent.setDaycount(smd.getAccrualDaycount());
                                    long days = ChronoUnit.DAYS.between(lastEndDate, end);
                                    accountingEvent.setDays((int) days);
                                    accountingEvent.setCreatedAt(LocalDateTime.now());
                                    accountingEvent.setEventStatus(AccountingEventStatus.NEW);
                                    accountingEvent.setEventType(EventType.ACCRUAL);
                                    accountingEvent.setPositionDetail(positionDetailId);
                                    accountingEvent.setSourceId(positionDetailId);
                                    accountingEvent.setSourceType(EventSourceType.INSTRUMENT);
                                    accountingEvent.setEventKey(smd.getCode() + " [" + positionDetailId + "] " + "[" + EventType.ACCRUAL.getCode() + "]" + end);
                                    accountingEvent.setGeneratedBy(smd.getIdMasterData());
                                    accountingEvent.setGeneratedRef(smd.getCode());
                                    accountingEvent.setSourceId(positionDetailId);
                                    events.add(accountingEvent);

                                    lastEndDate = end;
                                }
                            }
                        }

                        // genero evento di accrual se nessuna cedola stacca nell' intervallo allora accrual
                        // da settlement a official date, se almeno una cedola stacca accrual da ultima end
                        // date cedola a official date
                        AccountingEventAccruals accountingEvent = new AccountingEventAccruals();
                        double accrualAmount = txn.getQuantity() * smd.getMultiplier();
                        // accrual in base 100
                        double accrualsFrom = bondCalculator.getAccruals(smd, lastEndDate);
                        double accrualsTo = bondCalculator.getAccruals(smd, officialDate);
                        double accruals = (accrualsTo - accrualsFrom) * accrualAmount;
                        accountingEvent.setAccrualAmount(accruals);
                        accountingEvent.setAccountingNominal(accrualAmount);
                        accountingEvent.setCouponRate(smd.getInterestRate());
                        accountingEvent.setDaycount(smd.getAccrualDaycount());
                        long days = ChronoUnit.DAYS.between(lastEndDate, officialDate);
                        accountingEvent.setDays((int) days);
                        accountingEvent.setCreatedAt(LocalDateTime.now());
                        accountingEvent.setEventStatus(AccountingEventStatus.NEW);
                        accountingEvent.setEventType(EventType.ACCRUAL);
                        accountingEvent.setPositionDetail(positionDetailId);
                        accountingEvent.setSourceId(positionDetailId);
                        accountingEvent.setSourceType(EventSourceType.INSTRUMENT);
                        accountingEvent.setEventKey(smd.getCode() + " [" + positionDetailId + "] " + "[" + EventType.ACCRUAL.getCode() + "]" + officialDate);
                        accountingEvent.setGeneratedBy(smd.getIdMasterData());
                        accountingEvent.setGeneratedRef(smd.getCode());
                        accountingEvent.setSourceId(positionDetailId);
                        events.add(accountingEvent);

                    } catch (Exception e) {
                        log.error("### Error generating accrual events for txn: " + txn.getIdFinancialTxn());
                        throw new TxnProcessingException(e.getLocalizedMessage());
                    }
                }
            }
        }
    }
}
