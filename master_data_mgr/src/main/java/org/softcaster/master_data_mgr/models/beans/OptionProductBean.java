package org.softcaster.master_data_mgr.models.beans;

import org.hibernate.Hibernate;
import org.softcaster.commons.ui.bean.ITrendable;
import org.softcaster.commons.ui.model.IFndtModel;
import org.softcaster.core.data.FutureOptionProduct;
import org.softcaster.core.data.OptionProduct;
import org.softcaster.core.data.SpotOptionProduct;

/**
 * Riga della lista delle testate delle opzioni quotate.
 */
public class OptionProductBean implements IFndtModel, ITrendable {

    private final OptionProduct op;

    public OptionProductBean(OptionProduct op) {
        this.op = op == null ? null : (OptionProduct) Hibernate.unproxy(op);
    }

    @Override
    public Object getValueAt(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> op.getCode();
            case 1 -> op.getDescription();
            case 2 -> op instanceof FutureOptionProduct ? "Future" : "Spot";
            case 3 -> op.getAssetClass().getCode();
            case 4 -> op.getCurrency().getIsoCode();
            case 5 -> underlying(op);
            case 6 -> op.getExerciseStyle();
            case 7 -> op.getPremiumStyle();
            case 8 -> op.getSettlementType();
            case 9 -> op.getContractValue();
            case 10 -> op.getTickSize();
            case 11 -> op.getMultiplier();
            default -> null;
        };
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{"Code", "Description", "Type", "Asset Class", "Currency", "Underlying",
            "Exercise", "Premium", "Settlement", "Contract Value", "Tick Size", "Multiplier"};
    }

    @Override
    public int getTrendForColumn(int columnIndex) {
        return 0;
    }

    public OptionProduct getOptionProduct() {
        return op;
    }

    /**
     * Future sottostante (opzioni su future) o sottostante fisso (opzioni su
     * strumento secco, vuoto se ogni serie ha il suo).
     */
    private static String underlying(OptionProduct op) {
        if (op instanceof FutureOptionProduct fop) {
            return fop.getFutureProduct() == null ? "" : fop.getFutureProduct().getCode();
        }
        if (op instanceof SpotOptionProduct sop) {
            return sop.getUnderlying() == null ? "" : sop.getUnderlying().getCode();
        }
        return "";
    }
}