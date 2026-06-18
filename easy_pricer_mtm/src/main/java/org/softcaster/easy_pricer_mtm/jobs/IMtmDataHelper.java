/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.jobs;

import org.softcaster.provider.enums.RequestType;

/**
 *
 * @author ep
 */
public interface IMtmDataHelper {
    
    public double getSpotPrice(String ticker, RequestType request);
}
