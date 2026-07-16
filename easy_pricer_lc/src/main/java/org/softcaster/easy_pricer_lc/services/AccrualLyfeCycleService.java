/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;
import org.softcaster.easy_pricer_lc.schedulers.IScheduler;
import org.softcaster.easy_pricer_lc.schedulers.SchedulerDispatcher;
import org.softcaster.engine.enums.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccrualLyfeCycleService implements LifeCycleHandler {

    private static final Logger log = LoggerFactory.getLogger(SettlementLyfeCycleService.class);

    @Autowired
    private MasterDataDAO masterDataDAO;

    @Autowired
    SchedulerDispatcher schedulerDispatcher;

    @Override
    public List<AccountingEvent> generateEvents(EventInfo info) throws LifeCycleException {
        if (!(info instanceof AccrualEventInfo accrualEventInfo)) {
            String error = "### Invalid EventInfo";
            log.error(error);
            throw new LifeCycleException(error);
        }

        PositionDetail detail = accrualEventInfo.getDetail();
        if (detail == null) {
            String error = "### Invalid Detal";
            log.error(error);
            throw new LifeCycleException(error);
        }

        return generateAccountingEventAccruals(detail, accrualEventInfo.getFrom(), accrualEventInfo.getTo());
    }

    private List<AccountingEvent> generateAccountingEventAccruals(PositionDetail detail, LocalDate from, LocalDate to) {

        Integer masterDataId = detail.getMasterData();
        if (masterDataId != null && masterDataId > 0) {
            MasterData masterData = masterDataDAO.findByIdMasterData(masterDataId);
            if (masterData != null) {
                IScheduler scheduler = schedulerDispatcher.dispatch(masterData.getAssetClass().getCode());
                if (scheduler != null) {
                    List<AccountingEvent> events = scheduler.getAccountingEvents(EventType.ACCRUAL, detail, masterData, from, to);
                    return events;
                }
            }
        }

        return null;
    }
}
