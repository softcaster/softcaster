/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.bondblox;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LatestDatum {

    @JsonProperty("Bond PriceMid")
    public double bondPriceMid;
    @JsonProperty("Bid Price")
    public double bidPrice;
    @JsonProperty("Ask Price")
    public double askPrice;
    @JsonProperty("Bond Yield (Mid)")
    public double bondYield;
    public String mysqlDateTime;
    @JsonProperty("Ask Yield")
    public double askYield;
    @JsonProperty("Bid Yield")
    public double bidYield;
    public double zSpread;
    public double ytcMid;
    public double bondYieldMid;
    public double accruedInterest;
    public double duration;
}
