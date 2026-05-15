/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cme;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 *
 * @author ep
 */
public class Quote {

    public String last;
    public String change;
    public String priorSettle;
    @JsonProperty("open")
    public String myopen;
    public String close;
    public String high;
    public String low;
    public String highLimit;
    public String lowLimit;
    public String volume;
    public String mdKey;
    public String quoteCode;
    public String escapedQuoteCode;
    public String code;
    public Date updated;
    public String percentageChange;
    public String productName;
    public String productCode;
    public String uri;
    public int productId;
    public String exchangeCode;
    public String notionalValue;
    public String expirationMonth;
    public String expirationCode;
    public String expirationDate;
    public String optionUri;
    public boolean hasOption;
    public Date lastTradeDate;
    public PriceChart priceChart;
    public String group;
    public String groupRank;
    public String watchlistPercentage;
    public String netChangeStatus;
    public String highLowLimits;
    public Date lastUpdated;
    public boolean isFrontMonth;
}
