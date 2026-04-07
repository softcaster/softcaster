/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.easy_pricer_core.data.Currency;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 *
 * @author softc
 */
public class CurrencyBean implements IMasterDataModel {

    private final Currency currency;

    public CurrencyBean(Currency currency) {
        this.currency = currency;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                currency.getIsoCode();
            case 1 ->
                currency.getDescription();
            case 2 ->
                currency.getCalendar().getCode();
            case 3 ->
                currency.getDaycount().getCode();
            case 4 ->
                currency.getBusinessDays();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"ISO Code", "Description", "Calendar", "Daycount", "Business Days"};
    }
}
