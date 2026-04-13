/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.easy_pricer_mds.model.IMDSModel;

/**
 *
 * @author ep
 */
public class CurrencyPairBean implements IMDSModel {

    private final String bcy;
    private final String ccy;
    private final Double bid;
    private final Double ask;
    
    public CurrencyPairBean(String bcy, String ccy, double bid, double ask) {
        this.bcy = bcy;
        this.ccy = ccy;
        this.bid = bid;
        this.ask = ask;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                bcy;
            case 1 ->
                ccy;
            case 2 ->
                bid;
            case 3 ->
                ask;
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Bcy", "Ccy", "Bid", "Ask"};
    }
    
}
