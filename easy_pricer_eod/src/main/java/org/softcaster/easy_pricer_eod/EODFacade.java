/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod;

import org.softcaster.core.data.CalendarDAO;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.easy_pricer_eod.services.MicroserviceLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EODFacade {

    @Autowired
    private MicroserviceLauncher microserviceLauncher;

    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private CalendarDAO calendarDAO;
    @Autowired
    private SystemBusinessCalendarDAO systemBusinessCalendarDAO;
    /**
     * @return the microserviceLauncher
     */
    public MicroserviceLauncher getMicroserviceLauncher() {
        return microserviceLauncher;
    }

    /**
     * @return the currencyDAO
     */
    public CurrencyDAO getCurrencyDAO() {
        return currencyDAO;
    }

    /**
     * @return the calendarDAO
     */
    public CalendarDAO getCalendarDAO() {
        return calendarDAO;
    }

    /**
     * @return the systemBusinessCalendarDAO
     */
    public SystemBusinessCalendarDAO getSystemBusinessCalendarDAO() {
        return systemBusinessCalendarDAO;
    }
}
