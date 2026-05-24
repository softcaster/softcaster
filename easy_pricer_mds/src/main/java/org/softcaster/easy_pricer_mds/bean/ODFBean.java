/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.engine.curve.OrderedDiscountFactor;

/**
 *
 * @author ep
 */
public class ODFBean implements IFndtModel, ITrendable {

    private final OrderedDiscountFactor odf;

    public ODFBean(OrderedDiscountFactor odf) {
        this.odf = odf;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        if (odf == null || odf.days() == 0) {
            return null;
        }

        return switch (columnIndex) {
            case 0 ->
                odf.date();
            case 1 -> {
                double tenor = odf.days() / 360.;
                double rate = (1 - odf.discountFactor()) / (odf.discountFactor() * tenor);
                rate *= 100.;
                yield rate;
            }
            case 2 ->
                odf.discountFactor();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Maturity", "Rate%", "Discount Factor"};
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        return 0;
    }

}
