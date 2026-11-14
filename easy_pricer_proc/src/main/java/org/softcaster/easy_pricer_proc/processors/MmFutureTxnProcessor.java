package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.hibernate.Hibernate;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.MmFutureMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.stereotype.Component;

/**
 * Deal su un future money market (prezzo 100 - tasso). Posizione come per gli
 * altri future: quantita' = contratti x contract value, prezzo = prezzo x
 * multiplier (il multiplier della testata porta la frazione d'anno del
 * deposito sottostante); vendita allo scoperto ammessa.
 *
 * Margine iniziale (componente INITIAL_MARGIN) solo all'inserimento del deal
 * e solo se la testata ne indica uno.
 */
@Component("MFU")
public class MmFutureTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        if (!(Hibernate.unproxy(txn.getMasterData()) instanceof MmFutureMasterData mfmd)) {
            throw new TxnProcessingException("Invalid processor: " + txn.getMasterData().getCode() + " is not a money market future");
        }
        if (mfmd.getContractValue() == null) {
            throw new TxnProcessingException("Missing contract value for " + mfmd.getCode());
        }

        super.process(positionInput(mfmd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide()), position);

        if (!marginApplies(txn)) {
            return;
        }
        if (mfmd.getInitialMargin() != null && mfmd.getInitialMargin() > 0) {
            FinancialTxnComponent initialMargin = new FinancialTxnComponent();
            initialMargin.setCurrency(mfmd.getCurrency());
            initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
            initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
            initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * mfmd.getInitialMargin()));
            txn.addTxnComponent(initialMargin);
        }
    }

    @Override
    protected ProcInputData positionInput(org.softcaster.core.data.MasterData masterData, double quantity, double price, TxnSide side) {
        MmFutureMasterData mfmd = (MmFutureMasterData) Hibernate.unproxy(masterData);
        ProcInputData input = new ProcInputData();
        input.setPrice(price * mfmd.getMultiplier());
        input.setPriceMultiplier(mfmd.getMultiplier());
        input.setQuantity(quantity * mfmd.getContractValue());
        input.setSide(side);
        return input;
    }
}
