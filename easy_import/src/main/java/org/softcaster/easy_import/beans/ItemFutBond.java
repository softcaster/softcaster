/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_import.beans;

import java.sql.Date;

/**
 *
 * @author softc
 */
public class ItemFutBond {

    public String isincode;
    public String description;
    public String underliyngIsincode;
    public String issuer;
    public String currency;
    public String calendar;
    public String settlementType;
    public double contractValue;
    public Date issuedate;
    public double issueprice;
    public Date redemptiondate;
    public double redemptionprice;
    public double taxrate;
    public double tickSize;
    // Percentuale del ctv
    public double initialMargin;
}
