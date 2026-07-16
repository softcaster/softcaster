/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_lc.schedulers;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.engine.enums.EventType;

public interface IScheduler {
    public List<AccountingEvent> getAccountingEvents(EventType eventType, PositionDetail detail, MasterData masterData, LocalDate from, LocalDate to);

}
