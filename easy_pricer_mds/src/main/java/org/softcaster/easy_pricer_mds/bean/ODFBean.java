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
        if(odf == null)
            return null;
        
        return switch (columnIndex) {
            case 0 -> 
                odf.date();
            case 1 ->
                odf.discountFactor();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Maturity", "Discount Factor"};
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        return 0;
    }
    
}
