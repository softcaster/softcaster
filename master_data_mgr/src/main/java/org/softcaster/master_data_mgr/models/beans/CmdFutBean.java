/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.CmdFutureMasterData;

/**
 *
 * @author ep
 */
public class CmdFutBean implements IFndtModel, ITrendable {

    private final CmdFutureMasterData cfmd;

    public CmdFutBean(CmdFutureMasterData cfmd) {
        this.cfmd = cfmd;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                cfmd.getIsin();
            case 1 ->
                cfmd.getDescription();
            case 2 ->
                cfmd.getCurrency().getIsoCode();
            case 3 ->
                cfmd.getMaturityDate();
            case 4 ->
                cfmd.getContractValue();
            case 5 ->
                cfmd.getTickSize();
            case 6 ->
                cfmd.getSettlementType().getCode();
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
    
    public CmdFutureMasterData getCmdFutureMasterData() {
        return cfmd;
    }
}
