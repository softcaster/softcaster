/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.easy_pricer_core.data.BondFutureMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class FutBondBean implements IMasterDataModel {

    private BondFutureMasterData bfmd;
    
    public FutBondBean(BondFutureMasterData bfmd) {
        this.bfmd = bfmd;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                bfmd.getIsin();
            case 1 ->
                bfmd.getDescription();
            case 2 ->
                bfmd.getMaturityDate();
            case 3 ->
                bfmd.getContractValue();
            case 4 ->
                bfmd.getTickSize();
            case 5 ->
                bfmd.getSettlementType().getCode();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Isin", "Description", "Maturity", "Contract Value", "Tick Size", "Settlement"};
    }
    
    public BondFutureMasterData getBondFutureMasterData() {
        return bfmd;
    }
}
