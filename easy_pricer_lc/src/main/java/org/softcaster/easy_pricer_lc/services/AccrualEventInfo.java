/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import java.time.LocalDate;
import org.softcaster.core.data.PositionDetail;

/**
 *
 * @author ep
 */
public class AccrualEventInfo extends EventInfo {
    private PositionDetail detail;
    private LocalDate from;
    private LocalDate to;

    /**
     * @return the detail
     */
    public PositionDetail getDetail() {
        return detail;
    }

    /**
     * @param detail the detail to set
     */
    public void setDetail(PositionDetail detail) {
        this.detail = detail;
    }

    /**
     * @return the from
     */
    public LocalDate getFrom() {
        return from;
    }

    /**
     * @param from the from to set
     */
    public void setFrom(LocalDate from) {
        this.from = from;
    }

    /**
     * @return the to
     */
    public LocalDate getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(LocalDate to) {
        this.to = to;
    }
}
