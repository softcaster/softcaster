/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds_core.dto;

import java.io.Serializable;

/**
 *
 * @author ep
 */
public class ForwardPricingResponse implements Serializable {

    public double theoreticalPrice = 0.;
    public String ctd = "";
    // bond future: analisi del CTD al prezzo a pronti (per 100 di nominale, tassi in decimale)
    public double ctdYield = 0.;
    public double ctdModifiedDuration = 0.;
    public double ctdDv01 = 0.;
    // DV01 del future per 100 di nominale (DV01 del CTD / CF)
    public double futureDv01 = 0.;
}
