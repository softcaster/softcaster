/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondScheduler implements IScheduler {

    @Autowired
    private BondCalculator bondCalculator;

    /**
     *
     * @param detail
     * @param masterData
     * @param from
     * @param to
     * @return
     */
    @Override
    public AccountingEventAccruals getAccountingEventAccrual(PositionDetail detail, MasterData masterData, LocalDate from, LocalDate to) {
        AccountingEventAccruals accountingEvent = null;
        if (masterData instanceof SecurityMasterData smd) {
            accountingEvent = new AccountingEventAccruals();

            double accrualsFrom = bondCalculator.getAccruals(smd, from);
            double accrualsTo = bondCalculator.getAccruals(smd, to);
            double quantity = (detail.getBuyQty() - detail.getSellQty()) * smd.getMultiplier();
            double accruals = (accrualsTo - accrualsFrom) * quantity;
            accountingEvent.setAccrualAmount(accruals);
            accountingEvent.setAccountingNominal(quantity);
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
            accountingEvent.setEventKey(smd.getCode() + " [" + detail.getIdPositionDetail() + "] " + "[" +EventType.ACCRUAL.getCode() + "]" + LocalDate.now());
            accountingEvent.setGeneratedBy(smd.getIdMasterData());
            accountingEvent.setGeneratedRef(smd.getCode());
            accountingEvent.setSourceId(detail.getIdPositionDetail());
        }

        return accountingEvent;
    }
}
