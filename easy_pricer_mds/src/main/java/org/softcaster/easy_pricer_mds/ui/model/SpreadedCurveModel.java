/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.ui.model;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.easy_pricer_mds.bean.SpreadCurveBean;

/**
 *
 * @author ep
 */
public class SpreadedCurveModel extends FndtTableModel<SpreadCurveBean> {
    
    public SpreadedCurveModel(SpreadCurveBean prototype) {
        super(prototype);
    }
    
    /**
     *
     * @param newData
     */
    @Override
    public void setData(List<SpreadCurveBean> newData) {
        
        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
}
