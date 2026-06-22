/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.services;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemDateManager {

    @Autowired
    private SystemBusinessCalendarDAO systemBusinessCalendarDAO;

    private LocalDate officialBusinessDate;

    @PostConstruct
    public void init() {
        SystemBusinessCalendar systemBusinessCalendar = systemBusinessCalendarDAO.findBySbcId(1);
        this.officialBusinessDate = systemBusinessCalendar.getOfficialDate();
    }

    public LocalDate getOfficialBusinessDate() {
        return this.officialBusinessDate;
    }

    public void setOfficialBusinessDate(LocalDate newDate) {
        this.officialBusinessDate = newDate;
    }
}
