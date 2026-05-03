/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.dto;

import java.io.Serializable;
import java.sql.Date;

/**
 *
 * @author softc
 */
public class PricingRequest implements Serializable {

    public String isin = "";
    public double referencePrice = 0.;
    public Date referenceDate = null;
}
