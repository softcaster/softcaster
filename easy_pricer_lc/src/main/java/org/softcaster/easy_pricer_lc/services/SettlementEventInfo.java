/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.services;

import org.softcaster.core.data.PositionTxnLinks;

/**
 *
 * @author ep
 */
public class SettlementEventInfo extends EventInfo {
    private PositionTxnLinks link;

    /**
     * @return the link
     */
    public PositionTxnLinks getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(PositionTxnLinks link) {
        this.link = link;
    }
    
    
}
