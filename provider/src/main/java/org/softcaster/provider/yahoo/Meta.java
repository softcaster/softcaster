/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.yahoo;

import java.util.ArrayList;

/**
 *
 * @author ep
 */
public class Meta {

    public String currency;
    public String symbol;
    public String exchangeName;
    public String fullExchangeName;
    public String instrumentType;
    public int firstTradeDate;
    public int regularMarketTime;
    public boolean hasPrePostMarketData;
    public int gmtoffset;
    public String timezone;
    public String exchangeTimezoneName;
    public double regularMarketPrice;
    public double fiftyTwoWeekHigh;
    public double fiftyTwoWeekLow;
    public double regularMarketDayHigh;
    public double regularMarketDayLow;
    public int regularMarketVolume;
    public String longName;
    public String shortName;
    public double chartPreviousClose;
    public double previousClose;
    public int scale;
    public int priceHint;
    public CurrentTradingPeriod currentTradingPeriod;
    public ArrayList<ArrayList<Object>> tradingPeriods;
    public String dataGranularity;
    public String range;
    public ArrayList<String> validRanges;

}
