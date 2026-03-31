/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.softcaster.marketdataprovider;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 *
 * @author svil
 */
public class Node {
    
    private java.util.Date maturity = null;
    private double bid = 0.;
    private double ask = 0.;
    private double middle = 0.;
    
    public Node() {
    }

    /**
     * @return the maturity
     */
    public java.util.Date getMaturity() {
        return maturity;
    }

    /**
     * @param maturity the maturity to set
     */
    public void setMaturity(java.util.Date maturity) {
        this.maturity = maturity;
    }

    /**
     * @return the bid
     */
    public double getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(double bid) {
        this.bid = bid;
    }

    /**
     * @return the ask
     */
    public double getAsk() {
        return ask;
    }

    /**
     * @param ask the ask to set
     */
    public void setAsk(double ask) {
        this.ask = ask;
    }

    /**
     * @return the middle
     */
    public double getMiddle() {
        return middle;
    }

    /**
     * @param middle the middle to set
     */
    public void setMiddle(double middle) {
        this.middle = middle;
    }
    
    @Override
    public int hashCode() {
        int result = 0;
        if(maturity != null)
            result = maturity.hashCode();
        result = 31 * result + Double.hashCode(bid);
        result = 31 * result + Double.hashCode(ask);
        result = 31 * result + Double.hashCode(middle);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Node other = (Node) obj;
        if (Double.doubleToLongBits(this.bid) != Double.doubleToLongBits(other.bid)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ask) != Double.doubleToLongBits(other.ask)) {
            return false;
        }
        if (Double.doubleToLongBits(this.middle) != Double.doubleToLongBits(other.middle)) {
            return false;
        }
        return Objects.equals(this.maturity, other.maturity);
    }
    
    @Override
    public String toString() {
        String pattern = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        DecimalFormat df = new DecimalFormat("#.#####");
        df.setRoundingMode(RoundingMode.DOWN);
        
        String output = dateFormat.format(maturity) + " [Bid: " + df.format(bid) + "] - [Ask: " + df.format(ask) + "] - [Middle: " + df.format(middle) + "]";
        return output;
    }
}
