/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.BrokerInstrumentRules;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author softc
 */
public class BrokerRuleBean implements IMasterDataModel {

    private final BrokerInstrumentRules item;

    public BrokerRuleBean(BrokerInstrumentRules item) {
        this.item = item;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                item.getMasterData().getCode();
            case 1 ->
                item.getTxnSide() == 1 ? "Buy" : "Sell";
            case 2 ->
                item.getBrokerFee();
            case 3 ->
                item.getExchangeFee();
            case 4 ->
                item.getInitialMargin();
            case 5 ->
                item.getMaintenanceMargin();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Instrument", "Side", "Broker Fee", "Exchange Fee", "Initial Margin", "Maintenance Margin"};
    }
}
