/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.FxFutureMasterData;

/**
 *
 * @author ep
 */
public class FxFutBean implements IFndtModel, ITrendable {

    private final FxFutureMasterData ffmd;

    public FxFutBean(FxFutureMasterData ffmd) {
        this.ffmd = ffmd;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                ffmd.getIsin();
            case 1 ->
                ffmd.getDescription();
            case 2 ->
                ffmd.getCurrency().getIsoCode();
            case 3 ->
                ffmd.getMaturityDate();
            case 4 ->
                ffmd.getContractValue();
            case 5 ->
                ffmd.getTickSize();
            case 6 ->
                ffmd.getSettlementType().getCode();
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
    
    public FxFutureMasterData getFxFutureMasterData() {
        return ffmd;
    }
}
