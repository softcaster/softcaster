/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import java.util.List;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;

public interface LifeCycleHandler {

    /**
     *
     * @param info
     * @return
     * @throws LifeCycleException
     */
    public List<AccountingEvent> generateEvents(EventInfo info) throws LifeCycleException;
}
