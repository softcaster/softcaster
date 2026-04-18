/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.master_data_mgr.models.beans.MmFutBean;

/**
 *
 * @author ep
 */
public class MmFutTableModel extends FndtTableModel<MmFutBean> {

    public MmFutTableModel(MmFutBean prototype) {
        super(prototype);
    }

    /**
     *
     * @param newData
     */
    @Override
    public void setData(List<MmFutBean> newData) {

        super.setData(newData); // Questo chiama fireTableDataChanged()
    }

}
