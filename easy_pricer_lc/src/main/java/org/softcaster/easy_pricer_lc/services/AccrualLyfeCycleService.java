/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.AccountingEventAccrualsDAO;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;
import org.softcaster.easy_pricer_lc.schedulers.IScheduler;
import org.softcaster.easy_pricer_lc.schedulers.SchedulerDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccrualLyfeCycleService implements LifeCycleHandler {

    private static final Logger log = LoggerFactory.getLogger(SettlementLyfeCycleService.class);

    @Autowired
    private MasterDataDAO masterDataDAO;

    @Autowired
    SchedulerDispatcher schedulerDispatcher;

    @Autowired
    AccountingEventAccrualsDAO accountingEventAccrualsDAO;

    @Override
    public AccountingEvent generateEvent(EventInfo info) throws LifeCycleException {
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

    private AccountingEvent generateAccountingEventAccruals(PositionDetail detail, LocalDate from, LocalDate to) {
        AccountingEventAccruals event = null;

        Integer masterDataId = detail.getMasterData();
        if (masterDataId != null && masterDataId > 0) {
            MasterData masterData = masterDataDAO.findByIdMasterData(masterDataId);
            if (masterData != null) {
                IScheduler scheduler = schedulerDispatcher.dispatch(masterData.getAssetClass().getCode());
                if (scheduler != null) {
                    event = scheduler.getAccountingEventAccrual(detail, masterData, from, to);
                    if (event != null) {
                        accountingEventAccrualsDAO.saveOrUpdate(event);
                    }
                }
            }
        }

        return event;
    }
}
