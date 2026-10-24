package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.OptionMasterData;

/**
 * Riga della lista delle serie di opzioni.
 */
public class OptionBean implements IFndtModel, ITrendable {

    private final OptionMasterData omd;

    public OptionBean(OptionMasterData omd) {
        this.omd = omd;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                omd.getCode();
            case 1 ->
                omd.getDescription();
            case 2 ->
                omd.getProduct().getCode();
            case 3 ->
                omd.getOptionType().getCode();
            case 4 ->
                omd.getStrike();
            case 5 ->
                omd.getMaturityDate();
            case 6 ->
                omd.getUnderlying().getCode();
            case 7 ->
                omd.getCurrency().getIsoCode();
            case 8 ->
                omd.getExerciseStyle().getCode();
            case 9 ->
                omd.getContractValue();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Product", "Type", "Strike", "Expiry", "Underlying", "Currency", "Exercise", "Contract Value"};
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        return 0;
    }

    public OptionMasterData getOptionMasterData() {
        return omd;
    }
}