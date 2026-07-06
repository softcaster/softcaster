/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.easy_pricer_lc.exceptions.LifeCycleException;

public interface LifeCycleHandler {
    public AccountingEvent generateEvent(EventInfo info) throws LifeCycleException;
}
