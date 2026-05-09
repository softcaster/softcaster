/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.io.Serializable;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OptionStyle;
import org.softcaster.engine.enums.OptionType;

/**
 *
 * @author ep
 */
public class OptionCalcInputData implements Serializable {

    private double spotPrice = 0;
    private double strike = 0;
    private double bcyRate = 0;
    private double ccyRate = 0;
    private double volatility = 0;
    private OptionStyle optionStyle;
    private OptionType optionType;
    private DaycountBasis daycount = DaycountBasis.ACT_365;
    private java.sql.Date settlementDate = null;
    private java.sql.Date maturityDate = null;

    /**
     * @return the spotPrice
     */
    public double getSpotPrice() {
        return spotPrice;
    }

    /**
     * @param spotPrice the spotPrice to set
     */
    public void setSpotPrice(double spotPrice) {
        this.spotPrice = spotPrice;
    }

    /**
     * @return the strike
     */
    public double getStrike() {
        return strike;
    }

    /**
     * @param strike the strike to set
     */
    public void setStrike(double strike) {
        this.strike = strike;
    }

    /**
     * @return the bcyRate
     */
    public double getBcyRate() {
        return bcyRate;
    }

    /**
     * @param bcyRate the bcyRate to set
     */
    public void setBcyRate(double bcyRate) {
        this.bcyRate = bcyRate;
    }

    /**
     * @return the ccyRate
     */
    public double getCcyRate() {
        return ccyRate;
    }

    /**
     * @param ccyRate the ccyRate to set
     */
    public void setCcyRate(double ccyRate) {
        this.ccyRate = ccyRate;
    }

    /**
     * @return the volatility
     */
    public double getVolatility() {
        return volatility;
    }

    /**
     * @param volatility the volatility to set
     */
    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }

    /**
     * @return the settlementDate
     */
    public java.sql.Date getSettlementDate() {
        return settlementDate;
    }

    /**
     * @param settlementDate the settlementDate to set
     */
    public void setSettlementDate(java.sql.Date settlementDate) {
        this.settlementDate = settlementDate;
    }

    /**
     * @return the maturityDate
     */
    public java.sql.Date getMaturityDate() {
        return maturityDate;
    }

    /**
     * @param maturityDate the maturityDate to set
     */
    public void setMaturityDate(java.sql.Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    /**
     * @return the daycount
     */
    public DaycountBasis getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(DaycountBasis daycount) {
        this.daycount = daycount;
    }    

    /**
     * @return the optionStyle
     */
    public OptionStyle getOptionStyle() {
        return optionStyle;
    }

    /**
     * @param optionStyle the optionStyle to set
     */
    public void setOptionStyle(OptionStyle optionStyle) {
        this.optionStyle = optionStyle;
    }

    /**
     * @return the optionType
     */
    public OptionType getOptionType() {
        return optionType;
    }

    /**
     * @param optionType the optionType to set
     */
    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }
}
