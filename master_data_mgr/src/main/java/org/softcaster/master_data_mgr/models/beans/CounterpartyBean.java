/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.easy_pricer_core.data.Counterparty;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class CounterpartyBean implements IMasterDataModel {

    private final Counterparty ctp;

    public CounterpartyBean(Counterparty ctp) {
        this.ctp = ctp;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                ctp.getCode();
            case 1 ->
                ctp.getDescription();
            case 2 ->
                ctp.getCountry().getAlfa3Code();
            case 3 ->
                ctp.getCtpType().getCode();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Code", "Description", "Country", "Nature of Entity"};
    }
    public Counterparty getCounterparty() {
        return ctp;
    }

}
