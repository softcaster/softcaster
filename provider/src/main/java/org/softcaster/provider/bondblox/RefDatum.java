/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.bondblox;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;

public class RefDatum {

    public String issuerName;
    public String issuerNameBold;
    public String issuerNameNormal;
    public String coupon;
    public String maturityDate;
    public String maturityDateMMYY;
    public String issuedCurrency;
    public String creditRisk;
    public String seniority;
    public String couponType;
    public String countryOfRisk;
    public String industry;
    public String callable;
    public String perpetual;
    public String guarantor;
    public String ytc;
    public String zSpread;
    public String duration;
    public String nextCouponDate;
    public String ai;
    public String nextCallDate;
    public String nextCallPrice;
    public String liquidity;
    public String nextResetDate;
    public String resetIndexCL;
    public String spread;
    public String resetIndex;
    @JsonProperty("yield")
    public String myyield;
    public String issuerRating1;
    public String lastActionRating1;
    public String issuerRatingWatch;
    public String amountOutstanding;
    public String issuerRating2;
    public String lastRatingAction2;
    public String isin;
    public String registrationType;
    public String amountIssued;
    public String issueDate;
    public double issuePrice;
    public int cpnFreq;
    public String dayCount;
    public String redemptionValue;
    public String minDenom;
    public String overallRating;
    public ArrayList<IssuerRating> issuerRatings;
}
