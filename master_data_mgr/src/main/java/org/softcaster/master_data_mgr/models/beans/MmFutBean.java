/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.MmFutureMasterData;

/**
 *
 * @author ep
 */
public class MmFutBean implements IFndtModel, ITrendable {

    private final MmFutureMasterData mfmd;

    public MmFutBean(MmFutureMasterData mfmd) {
        this.mfmd = mfmd;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                mfmd.getIsin();
            case 1 ->
                mfmd.getDescription();
            case 2 ->
                mfmd.getCurrency().getIsoCode();
            case 3 ->
                mfmd.getMaturityDate();
            case 4 ->
                mfmd.getContractValue();
            case 5 ->
                mfmd.getTickSize();
            case 6 ->
                mfmd.getSettlementType().getCode();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Currency", "Maturity", "Contract Value", "Tick Size", "Settlement"};
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        return 0;
    }
    
    public MmFutureMasterData getMmFutureMasterData() {
        return mfmd;
    }
}
