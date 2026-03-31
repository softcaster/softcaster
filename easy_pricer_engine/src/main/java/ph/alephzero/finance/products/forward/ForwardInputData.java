/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ph.alephzero.finance.products.forward;

import ph.alephzero.finance.Compounding;
import ph.alephzero.finance.DayCountBasis;

/**
 *
 * @author ep
 */
public class ForwardInputData {
    // Prezzo mercato dell underlying
    protected double spotPrice = 0;
    // Free risk rate da settlement a maturity (repo rate)
    protected double rate = 0;
    protected DayCountBasis daycount = DayCountBasis.ACT_ACT;
    protected Compounding compounding = Compounding.SIMPLE;
    // Data valutazione
    protected java.sql.Date settlementDate = null;
    // Data scadenza future
    protected java.sql.Date maturityDate = null;

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
     * @return the rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * @return the daycount
     */
    public DayCountBasis getDaycount() {
        return daycount;
    }

    /**
     * @param daycount the daycount to set
     */
    public void setDaycount(DayCountBasis daycount) {
        this.daycount = daycount;
    }

    /**
     * @return the compounding
     */
    public Compounding getCompounding() {
        return compounding;
    }

    /**
     * @param compounding the compounding to set
     */
    public void setCompounding(Compounding compounding) {
        this.compounding = compounding;
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
    
}
