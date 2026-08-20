/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_eod.ui.models;

import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.Descriptors;

/**
 *
 * @author ep
 */
public class DescriptorBean implements IFndtModel {

    private final Descriptors descriptor;

    public DescriptorBean(Descriptors descriptor) {
        this.descriptor = descriptor;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        if(descriptor == null)
            return null;
        
        return switch (columnIndex) {
            case 0 ->
                descriptor.getDescriptorId();
            case 1 ->
                descriptor.getServiceType().getCode();
            case 2 ->
                descriptor.getJarPath();
            case 3 ->
                descriptor.getActiveProfile();
            case 4 ->
                descriptor.getPort();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Id", "Type", "Path", "Profile", "Port"};
    }

    /**
     * @return the descriptor
     */
    public Descriptors getDescriptor() {
        return descriptor;
    }

}
