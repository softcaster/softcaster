/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.PortfolioMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
public class PortfolioBean implements IMasterDataModel {

    private final PortfolioMasterData portfolioMasterData;

    public PortfolioBean(PortfolioMasterData portfolioMasterData) {
        this.portfolioMasterData = portfolioMasterData;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                portfolioMasterData.getCode();
            case 1 ->
                portfolioMasterData.getDescription();
            case 2 ->
                portfolioMasterData.getCurrency().getIsoCode();
             default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Code", "Description", "Currency"};
    }

    /**
     * @return the portfolioMasterData
     */
    public PortfolioMasterData getPortfolioMasterData() {
        return portfolioMasterData;
    }

}
