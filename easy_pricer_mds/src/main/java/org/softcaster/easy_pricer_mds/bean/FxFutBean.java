/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;

/**
 *
 * @author ep
 */
public class FxFutBean implements IFndtModel, ITrendable {

    private final String code;
    private Double bid;
    private Double ask;
    private int trendBid = 0;
    private int trendAsk = 0;

    public FxFutBean(String code, double bid, double ask) {
        this.code = code;
        this.bid = bid;
        this.ask = ask;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                code;
            case 1 ->
                getBid();
            case 2 ->
                getAsk();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Bid", "Ask"};
    }

    /**
     * @return the bid
     */
    public Double getBid() {
        return bid;
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(Double bid) {
        this.bid = bid;
    }

    /**
     * @return the ask
     */
    public Double getAsk() {
        return ask;
    }

    /**
     * @param ask the ask to set
     */
    public void setAsk(Double ask) {
        this.ask = ask;
    }

    /**
     * @return the trendBid
     */
    public int getTrendBid() {
        return trendBid;
    }

    /**
     * @param trendBid the trendBid to set
     */
    public void setTrendBid(int trendBid) {
        this.trendBid = trendBid;
    }

    /**
     * @return the trendAsk
     */
    public int getTrendAsk() {
        return trendAsk;
    }

    /**
     * @param trendAsk the trendAsk to set
     */
    public void setTrendAsk(int trendAsk) {
        this.trendAsk = trendAsk;
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        if (columnIndex == 1) {
            return trendBid;
        }
        if (columnIndex == 2) {
            return trendAsk;
        }
        return 0;
    }

}
