/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_lc.schedulers;

import java.time.LocalDate;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.account.AccountingEventAccruals;

public interface IScheduler {

    public AccountingEventAccruals getAccountingEventAccrual(PositionDetail detail, MasterData masterData, LocalDate from, LocalDate to);
}
