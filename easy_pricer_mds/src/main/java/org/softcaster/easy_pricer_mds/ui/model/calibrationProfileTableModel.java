/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mds.ui.model;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.easy_pricer_mds.bean.CalibrationProfileBean;

/**
 *
 * @author softc
 */
public class CalibrationProfileTableModel extends FndtTableModel<CalibrationProfileBean> {

    public CalibrationProfileTableModel(CalibrationProfileBean prototype) {
        super(prototype);
    }

    @Override
    public void setData(List<CalibrationProfileBean> newData) {
        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
}
