package org.softcaster.master_data_mgr.models.beans;

import org.hibernate.Hibernate;
import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.FutureProduct;

/**
 * Riga della lista delle testate dei future.
 */
public class FutureProductBean implements IFndtModel, ITrendable {

    private final FutureProduct fp;

    public FutureProductBean(FutureProduct fp) {
        this.fp = fp;
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 ->
                fp.getCode();
            case 1 ->
                fp.getDescription();
            case 2 ->
                productType(fp);
            case 3 ->
                fp.getExchangeContractCode();
            case 4 ->
                fp.getAssetClass().getCode();
            case 5 ->
                fp.getCurrency().getIsoCode();
            case 6 ->
                fp.getContractValue();
            case 7 ->
                fp.getTickSize();
            case 8 ->
                fp.getMultiplier();
            case 9 ->
                fp.getSettlementType();
            default ->
                null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Type", "Exchange Code", "Asset Class", "Currency",
            "Contract Value", "Tick Size", "Multiplier", "Settlement"};
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        return 0;
    }

    public FutureProduct getFutureProduct() {
        return fp;
    }

    /**
     * Tipo di future della testata (Bond, Fx, Mm, Cmd, Power).
     *
     * @param fp
     * @return
     */
    public static String productType(FutureProduct fp) {
        return Hibernate.unproxy(fp).getClass().getSimpleName().replace("FutureProduct", "");
    }
}
