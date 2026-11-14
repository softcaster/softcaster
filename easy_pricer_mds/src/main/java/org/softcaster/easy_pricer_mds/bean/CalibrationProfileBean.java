/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.bean;

import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.CalibrationProfile;

/**
 *
 * @author ep
 */
public class CalibrationProfileBean implements IFndtModel {

    private final CalibrationProfile bean;

    public CalibrationProfileBean(CalibrationProfile bean) {
        this.bean = bean;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                getBean().getDescription();
            case 1 ->
                getBean().getCode();
            case 2 ->
                getBean().getCurrency().getIsoCode();
            case 3 ->
                getBean().getLastUpdate();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Description", "Code", "Currency", "Last Update"};
    }
    
    /**
     * @return the bean
     */
    public CalibrationProfile getBean() {
        return bean;
    }

}
