/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.ForexMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class ForexBean implements IMasterDataModel {

    private final ForexMasterData fmd;

    public ForexBean(ForexMasterData fmd) {
        this.fmd = fmd;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                fmd.getBcy().getIsoCode();
            case 1 ->
                fmd.getCcy().getIsoCode();
            case 2 ->
                fmd.getBcyIrc();
            case 3 ->
                fmd.getCcyIrc();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Bcy", "Ccy", "Bcy YC", "Ccy YC"};
    }
    public ForexMasterData getForexMasterData() {
        return fmd;
    }

}
