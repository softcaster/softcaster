/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cme;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author softc
 */
public class Put{
    public String last;
    public String change;
    public String priorSettle;
    @JsonProperty("open") 
    public String myopen;
    public String high;
    public String low;
    public String highLimit;
    public String lowLimit;
    public String volume;
    public String quoteCode;
    public String escapedQuoteCode;
    public String code;
    public Date updated;
    public String percentageChange;
    public String netChangeStatus;
    public String highLowLimits;
}

