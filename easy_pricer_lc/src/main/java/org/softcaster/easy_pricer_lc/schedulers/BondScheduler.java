/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionTxnLinksDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import static org.softcaster.engine.enums.EventType.ACCRUAL;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Ratei e cedole dei titoli dal piano salvato (cash_flow_item, interessi per
 * 100): tasso fisso (XRB, XRN) e variabile (FRB, FRN), le cui cedole sono
 * fissate dallo step FIXING prima che maturino.
 */
@Component("XRB")
public class BondScheduler implements IScheduler {

    /**
     * Note a tasso fisso.
     */
    @Component("XRN")
    public static class FixedRateNote extends BondScheduler {
    }

    /**
     * Titoli a tasso variabile.
     */
    @Component("FRB")
    public static class FloatingRateBond extends BondScheduler {
    }

    /**
     * Note a tasso variabile.
     */
    @Component("FRN")
    public static class FloatingRateNote extends BondScheduler {
    }

    @Autowired
    private CashFlowInstrumentCalculator bondCalculator;
    @Autowired
    PositionTxnLinksDAO positionTxnLinksDAO;

    /**
     *
     * @param detail
     * @param masterData
     * @param from
     * @param to
     * @return
     */
    private AccountingEventAccruals getAccountingEventAccrual(PositionDetail detail, MasterData masterData, LocalDate from, LocalDate to) {
        AccountingEventAccruals accountingEvent = null;
        if (masterData instanceof SecurityMasterData smd) {
            accountingEvent = new AccountingEventAccruals();

            // Gestisco back-dated transaction con differenza tra nominale operativo e 
            // nominale memo
            double memoAmount = positionTxnLinksDAO.sumQuantityByPhase(detail.getIdPositionDetail(),
                    AccountingPhase.MEMO_POSTED);
            double operationalAmount = (detail.getBuyQty() - detail.getSellQty());
            // nominale con segno, senza multiplier (PositionAccountingEvent)
            double nominal = operationalAmount - memoAmount;
            double accruals = accrualPer100(smd, from, to) * nominal * smd.getMultiplier();
            accountingEvent.setAccrualAmount(accruals);
            accountingEvent.setAccountingNominal(nominal);
            accountingEvent.setCouponRate(smd.getInterestRate());
            accountingEvent.setDaycount(smd.getAccrualDaycount());
            long days = ChronoUnit.DAYS.between(from, to);
            accountingEvent.setDays((int) days);
            accountingEvent.setCreatedAt(LocalDateTime.now());
            accountingEvent.setEventStatus(AccountingEventStatus.NEW);
            accountingEvent.setEventType(EventType.ACCRUAL);
            accountingEvent.setPositionDetail(detail.getIdPositionDetail());
            accountingEvent.setSourceId(detail.getIdPositionDetail());
            accountingEvent.setSourceType(EventSourceType.INSTRUMENT);
            accountingEvent.setEventKey(smd.getCode() + " [" + detail.getIdPositionDetail() + "] " + "[" + EventType.ACCRUAL.getCode() + "]" + from + "/" + to);
            accountingEvent.setGeneratedBy(smd.getIdMasterData());
            accountingEvent.setGeneratedRef(smd.getCode());
            accountingEvent.setSourceId(detail.getIdPositionDetail());
        }

        return accountingEvent;
    }

    /**
     * Rateo maturato in (from, to], in base 100. A ogni stacco nel periodo il
     * rateo arriva alla cedola piena e riparte da zero: la differenza semplice
     * dei ratei a from e a to sarebbe negativa.
     */
    private double accrualPer100(SecurityMasterData smd, LocalDate from, LocalDate to) {
        double accrual = 0.;
        LocalDate start = from;
        for (CashFlowItem item : couponsIn(smd, from, to)) {
            accrual += item.getInterest() - bondCalculator.getAccruals(smd, start);
            start = item.getEnddate().toLocalDate();
        }
        return accrual + bondCalculator.getAccruals(smd, to) - bondCalculator.getAccruals(smd, start);
    }

    /**
     * Cedole che staccano in (from, to], in ordine di data.
     */
    private static List<CashFlowItem> couponsIn(SecurityMasterData smd, LocalDate from, LocalDate to) {
        return smd.getCashFlows().stream()
                .filter(item -> item.getEnddate().toLocalDate().isAfter(from) && !item.getEnddate().toLocalDate().isAfter(to))
                .sorted(Comparator.comparing(CashFlowItem::getEnddate))
                .toList();
    }

    /**
     * Eventi delle cedole che staccano in (from, to] per chi ha diritto: i deal
     * regolati prima dello stacco. L'importo ha il segno della posizione.
     */
    private List<AccountingEvent> getAccountingEventCoupons(PositionDetail detail, MasterData masterData, LocalDate from, LocalDate to) {
        List<AccountingEvent> events = new ArrayList<>();
        if (masterData instanceof SecurityMasterData smd) {
            for (CashFlowItem item : couponsIn(smd, from, to)) {
                LocalDate couponDate = item.getEnddate().toLocalDate();
                double nominal = positionTxnLinksDAO.sumQuantitySettledBefore(detail.getIdPositionDetail(), couponDate);
                AccountingEventAccruals event = new AccountingEventAccruals();
                event.setAccrualAmount(nominal * item.getInterest() * smd.getMultiplier());
                event.setAccountingNominal(nominal);
                event.setCouponRate(item.getInterest());
                event.setDaycount(smd.getAccrualDaycount());
                event.setDays((int) ChronoUnit.DAYS.between(item.getStartDate().toLocalDate(), couponDate));
                event.setCreatedAt(LocalDateTime.now());
                event.setEventStatus(AccountingEventStatus.NEW);
                event.setEventType(EventType.COUPON);
                event.setPositionDetail(detail.getIdPositionDetail());
                event.setSourceId(detail.getIdPositionDetail());
                event.setSourceType(EventSourceType.INSTRUMENT);
                event.setEventKey(smd.getCode() + " [" + detail.getIdPositionDetail() + "] " + "[" + EventType.COUPON.getCode() + "]" + couponDate);
                event.setGeneratedBy(smd.getIdMasterData());
                event.setGeneratedRef(smd.getCode());
                events.add(event);
            }
        }
        return events;
    }

    @Override
    public List<AccountingEvent> getAccountingEvents(EventType eventType, PositionDetail detail, MasterData masterData, LocalDate from, LocalDate to) {
        List<AccountingEvent> events = null;

        switch (eventType) {
            case ACCRUAL:
                AccountingEvent event = getAccountingEventAccrual(detail, masterData, from, to);
                if (event != null && event instanceof AccountingEventAccruals aea) {
                    if (!NumberUtils.isZero(aea.getAccrualAmount())) {
                        events = new ArrayList<>();
                        events.add(event);
                    }
                }
                break;
            case COUPON:
                events = getAccountingEventCoupons(detail, masterData, from, to);
                break;
            case SETTLEMENT:
            case MATURITY:
            default:
                break;
        }

        return events;
    }
}
