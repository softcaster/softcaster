/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cnbc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author softc
 */
public class FormattedQuote {

    public String symbol;
    public String symbolType;
    public int code;
    public String name;
    public String shortName;
    public String onAirName;
    public String altName;
    public String last;
    public String last_timedate;
    public Date last_time;
    public String changetype;
    public String type;
    public String subType;
    public String exchange;
    public String source;
    @JsonProperty("open")
    public String myopen;
    public String high;
    public String low;
    public String change;
    public String change_pct;
    public String currencyCode;
    public String volume;
    public String volume_alt;
    public String provider;
    public String previous_day_closing;
    public String altSymbol;
    public String realTime;
    public String curmktstatus;
    public String yrhiprice;
    public String yrhidate;
    public String yrloprice;
    public String yrlodate;
    public String streamable;
    public String bond_last_price;
    public String bond_change_price;
    public String bond_change_pct_price;
    public String bond_open_price;
    public String bond_high_price;
    public String bond_low_price;
    public String bond_prev_day_closing_price;
    public String bond_changetype;
    public String maturity_date;
    public String coupon;
    public String issue_id;
    public String countryCode;
    public String timeZone;
    public String feedSymbol;
    public String portfolioindicator;
}
