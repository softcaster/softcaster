package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.hibernate.Hibernate;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.IndexFutureMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnSide;
import org.springframework.stereotype.Component;

/**
 * Deal su future su indice (FESX): come i future su commodity. Posizione in
 * punti indice x contract value (10 EUR per punto); margine iniziale per
 * contratto solo se la testata ne ha uno (> 0).
 */
@Component("IXF")
public class IndexFutureTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {
        if (!(Hibernate.unproxy(txn.getMasterData()) instanceof IndexFutureMasterData ifmd)) {
            throw new TxnProcessingException("Invalid processor: " + txn.getMasterData().getCode() + " is not an index future");
        }

        super.process(positionInput(ifmd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide()), position);

        if (!marginApplies(txn) || ifmd.getInitialMargin() == null || ifmd.getInitialMargin() <= 0) {
            return;
        }
        FinancialTxnComponent initialMargin = new FinancialTxnComponent();
        initialMargin.setCurrency(ifmd.getCurrency());
        initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
        initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
        initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * ifmd.getInitialMargin()));
        txn.addTxnComponent(initialMargin);
    }

    @Override
    protected ProcInputData positionInput(MasterData masterData, double quantity, double price, TxnSide side) {
        IndexFutureMasterData ifmd = (IndexFutureMasterData) Hibernate.unproxy(masterData);
        ProcInputData input = new ProcInputData();
        input.setPrice(price * ifmd.getMultiplier());
        input.setPriceMultiplier(ifmd.getMultiplier());
        input.setQuantity(quantity * ifmd.getContractValue());
        input.setSide(side);
        return input;
    }
}
