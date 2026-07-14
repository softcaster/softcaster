/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import org.softcaster.core.data.PositionDetail;

/**
 *
 * @author ep
 */
public class AccrualEventInfo extends EventInfo {
    private PositionDetail detail;

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

}
