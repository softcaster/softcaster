/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.easy_pricer_core.data.CashFlowItem;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class CashFlowBean implements IMasterDataModel {
    
    private final CashFlowItem item;

    public CashFlowBean(CashFlowItem item) {
        this.item = item;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                item.getStartDate();
            case 1 ->
                item.getEnddate();
            case 2 ->
                item.getInterest();
            case 3 ->
                item.getAmount();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Start Date", "Maturity", "Interest", "Amount"};
    }
    
}
