/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider;

/**
 *
 * @author ep
 */
public class DataNode {
    private String ric = "";
    private double bid = 0;
    private double ask = 0;

    /**
     * @return the ric
     */
    public String getRic() {
        return ric;
    }

    /**
     * @param ric the ric to set
     */
    public void setRic(String ric) {
        this.ric = ric;
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
    
}
