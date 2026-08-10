/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionTxnLinksDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import static org.softcaster.engine.enums.EventType.ACCRUAL;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondScheduler implements IScheduler {

    @Autowired
    private BondCalculator bondCalculator;
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
            double accrualAmount = (operationalAmount - memoAmount) * smd.getMultiplier();
            // accrual in base 100
            double accrualsFrom = bondCalculator.getAccruals(smd, from);
            double accrualsTo = bondCalculator.getAccruals(smd, to);
            double accruals = (accrualsTo - accrualsFrom) * accrualAmount;
            accountingEvent.setAccrualAmount(accruals);
            accountingEvent.setAccountingNominal(accrualAmount);
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
            accountingEvent.setEventKey(smd.getCode() + " [" + detail.getIdPositionDetail() + "] " + "[" + EventType.ACCRUAL.getCode() + "]" + LocalDate.now());
            accountingEvent.setGeneratedBy(smd.getIdMasterData());
            accountingEvent.setGeneratedRef(smd.getCode());
            accountingEvent.setSourceId(detail.getIdPositionDetail());
        }

        return accountingEvent;
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
            case SETTLEMENT:
            case MATURITY:
            default:
                break;
        }

        return events;
    }
}
