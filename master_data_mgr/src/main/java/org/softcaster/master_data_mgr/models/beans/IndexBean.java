package org.softcaster.master_data_mgr.models.beans;

import org.softcaster.core.data.IndexMasterData;
import org.softcaster.master_data_mgr.models.IMasterDataModel;

/**
 * Riga della tabella degli indici azionari.
 */
public class IndexBean implements IMasterDataModel {

    private final IndexMasterData imd;

    public IndexBean(IndexMasterData imd) {
        this.imd = imd;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                imd.getCode();
            case 1 ->
                imd.getDescription();
            case 2 ->
                imd.getCurrency().getIsoCode();
            case 3 ->
                imd.getIrc();
            case 4 ->
                imd.getDividendYield();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Currency", "Discount Curve", "Dividend Yield"};
    }

    public IndexMasterData getIndexMasterData() {
        return imd;
    }
}
