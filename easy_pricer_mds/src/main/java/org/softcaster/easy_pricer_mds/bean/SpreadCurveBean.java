/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.SpreadCurveEntity;

/**
 *
 * @author ep
 */
public class SpreadCurveBean implements IFndtModel {

    private final SpreadCurveEntity spreadCurve;

    public SpreadCurveBean(SpreadCurveEntity spreadCurve) {
        this.spreadCurve = spreadCurve;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        if(spreadCurve == null)
            return null;
        
        return switch (columnIndex) {
            case 0 ->
                spreadCurve.getDescription();
            case 1 ->
                spreadCurve.getCode();
            case 2 ->
                spreadCurve.getBaseCurve().getCurrency().getIsoCode();
            case 3 ->
                spreadCurve.getBaseCurve().getCurrency().getCalendar().getCode();
            case 4 ->
                spreadCurve.getBaseCurve().getCode();
            case 5 ->
                spreadCurve.getBaseCurve().getDescription();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Description", "Code", "Currency", "Calendar", "Base Curve", "Description"};
    }
    
    public SpreadCurveEntity getspreadCurve() {
        return spreadCurve;
    }
}
