/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.MarketQuoteDefinition;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author ep
 */
// https://www.mercatoelettrico.org/it-it/Home/Esiti/Elettricita/MGP/Esiti/PUN
public class MarketQuoteDefinitionBean implements IMasterDataModel {

    private final MarketQuoteDefinition mqd;

    public MarketQuoteDefinitionBean(MarketQuoteDefinition mqd) {
        this.mqd = mqd;
    }
    
    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                mqd.getCode();
            case 1 ->
                mqd.getDescription();
            case 2 ->
                mqd.getDataSource().getCode();
            case 3 ->
                mqd.getCountry().getAlfa2Code();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[] {"Code", "Description", "Data Source", "Market"};
    }

    public MarketQuoteDefinition getMqd() {
        return mqd;
    }
}
