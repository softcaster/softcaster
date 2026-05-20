/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.ui.model;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.easy_pricer_mds.bean.ODFBean;

/**
 *
 * @author ep
 */
public class YieldCurveRateModel extends FndtTableModel<ODFBean>{
    
    public YieldCurveRateModel(ODFBean prototype) {
        super(prototype);
    }

    @Override
    public void setData(List<ODFBean> newData) {

        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
    
}
