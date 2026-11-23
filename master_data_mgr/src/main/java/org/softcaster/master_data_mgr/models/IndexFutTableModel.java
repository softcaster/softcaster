/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;
import org.softcaster.master_data_mgr.models.beans.IndexFutBean;

/**
 *
 * @author ep
 */
public class IndexFutTableModel extends FndtTableModel<IndexFutBean> {

    public IndexFutTableModel(IndexFutBean prototype) {
        super(prototype);
    }

    @Override
    public void setData(List<IndexFutBean> newData) {

        super.setData(newData); // Questo chiama fireTableDataChanged()
    }

}
