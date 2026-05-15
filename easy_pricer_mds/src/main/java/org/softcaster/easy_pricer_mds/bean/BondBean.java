/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.SecurityMasterData;

/**
 *
 * @author ep
 */
public class BondBean implements IFndtModel, ITrendable {

    private final InstrumentQuote iQuote;

    private int trendBid = 0;
    private int trendAsk = 0;

    public BondBean(InstrumentQuote iQuote) {
        this.iQuote = iQuote;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> {
                String code = "";
                if (iQuote.getMasterData() instanceof SecurityMasterData bfmd) {
                    code = bfmd.getCode() + " - " + bfmd.getDescription();
                }
                yield code;
            }
            case 1 ->
                iQuote.getCode();
            case 2 ->
                iQuote.getMasterData().getCurrency().getIsoCode();
            case 3 ->
                iQuote.getBid();
            case 4 ->
                iQuote.getAsk();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Description", "Code", "Currency", "Bid", "Ask"};
    }

    /**
     * @return the bid
     */
    public Double getBid() {
        return iQuote.getBid();
    }

    /**
     * @param bid the bid to set
     */
    public void setBid(Double bid) {
        iQuote.setBid(bid);
    }

    /**
     * @return the ask
     */
    public Double getAsk() {
        return iQuote.getAsk();
    }

    /**
     * @param ask the ask to set
     */
    public void setAsk(Double ask) {
        iQuote.setAsk(ask);
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
        if (columnIndex == 3) {
            return trendBid;
        }
        if (columnIndex == 4) {
            return trendAsk;
        }
        return 0;
    }

    public InstrumentQuote getInstrumentQuote() {
        return iQuote;
    }
}
