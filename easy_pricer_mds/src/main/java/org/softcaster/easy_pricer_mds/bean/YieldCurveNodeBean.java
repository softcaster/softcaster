/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.types.Date;
import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;

/**
 *
 * @author softc
 */
public class YieldCurveNodeBean implements IFndtModel, ITrendable {

    private final Date maturity;
    private final double bid;
    private final double ask;

    public YieldCurveNodeBean(Date maturity, double bid, double ask) {
        this.maturity = maturity;
        this.bid = bid;
        this.ask = ask;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                maturity;
            case 1 ->
                bid;
            case 2 ->
                ask;
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Maturity", "Bid", "Ask"};
    }

    @Override
    public int getTrendForColumn(int i) {
        return 0;
    }

}
