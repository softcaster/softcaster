/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.easy_pricer_core.data.PortfolioMasterData;
import org.softcaster.easy_pricer_core.data.PositionMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class PositionBean implements IMasterDataModel {

    private final PositionMasterData positionMasterData;

    public PositionBean(PositionMasterData positionMasterData) {
        this.positionMasterData = positionMasterData;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                positionMasterData.getCode();
            case 1 ->
                positionMasterData.getDescription();
            case 2 ->
                positionMasterData.getCurrency().getIsoCode();
            case 3 ->
                positionMasterData.getPortfolio().getCode();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Currency", "Portfolio"};
    }

    /**
     * @return the positionMasterData
     */
    public PositionMasterData getPositionMasterData() {
        return positionMasterData;
    }

}
