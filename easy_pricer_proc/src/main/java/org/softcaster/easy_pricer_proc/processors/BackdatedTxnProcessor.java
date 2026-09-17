/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.TxnSide;

public class BackdatedTxnProcessor {

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
                                    event.setGeneratedRef("");
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
}
