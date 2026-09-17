/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.CmdFutureMasterData;
import org.softcaster.core.data.PowerFutureMasterData;

/**
 *
 * @author ep
 */
public class PowerFutBean implements IFndtModel, ITrendable {

    private final PowerFutureMasterData edfmd;

    public PowerFutBean(PowerFutureMasterData cfmd) {
        this.edfmd = cfmd;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                edfmd.getIsin();
            case 1 ->
                edfmd.getDescription();
            case 2 ->
                edfmd.getCurrency().getIsoCode();
            case 3 ->
                edfmd.getMaturityDate();
            case 4 ->
                edfmd.getContractValue();
            case 5 ->
                edfmd.getTickSize();
            case 6 ->
                edfmd.getSettlementType().getCode();
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
    
    public PowerFutureMasterData getPowerFutureMasterData() {
        return edfmd;
    }
}
