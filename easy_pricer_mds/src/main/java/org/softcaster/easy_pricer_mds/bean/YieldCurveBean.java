/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.YieldCurve;

/**
 *
 * @author ep
 */
public class YieldCurveBean implements IFndtModel {

    private final YieldCurve yieldCurve;

    public YieldCurveBean(YieldCurve yieldCurve) {
        this.yieldCurve = yieldCurve;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        if(yieldCurve == null)
            return null;
        
        return switch (columnIndex) {
            case 0 ->
                yieldCurve.getDescription();
            case 1 ->
                yieldCurve.getCode();
            case 2 ->
                yieldCurve.getCurrency().getIsoCode();
            case 3 ->
                yieldCurve.getCalendar().getCode();
            case 4 ->
                yieldCurve.getProvider();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Description", "Code", "Currency", "Calendar", "Provider"};
    }

    public YieldCurve getYieldCurve() {
        return yieldCurve;
    }
}
