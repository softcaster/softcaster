/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.ui.models;

import java.util.List;
import org.softcaster.commons.ui.model.FndtTableModel;

/**
 *
 * @author ep
 */
public class DescriptorsTableModel extends FndtTableModel<DescriptorBean> {
    
    public DescriptorsTableModel(DescriptorBean prototype) {
        super(prototype);
    }
    
    @Override
    public void setData(List<DescriptorBean> newData) {
        
        super.setData(newData); // Questo chiama fireTableDataChanged()
    }
}
