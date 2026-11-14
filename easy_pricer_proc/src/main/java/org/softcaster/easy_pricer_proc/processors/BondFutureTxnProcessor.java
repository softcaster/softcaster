/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.softcaster.core.data.BondFutureMasterData;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.springframework.stereotype.Component;

@Component("BFU")
public class BondFutureTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        BondFutureMasterData bfmd = (BondFutureMasterData) txn.getMasterData();
        if (bfmd == null) {
            throw new TxnProcessingException("Invalid processor");
        }

        super.process(positionInput(bfmd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide()), position);

        if (!marginApplies(txn)) {
            return;
        }
        // Aggiungo component initial margin
        FinancialTxnComponent initialMargin = new FinancialTxnComponent();
        initialMargin.setCurrency(bfmd.getCurrency());
        initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
        initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
        initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * bfmd.getInitialMargin()));
        txn.addTxnComponent(initialMargin);

    }

    @Override
    protected ProcInputData positionInput(org.softcaster.core.data.MasterData masterData, double quantity, double price,
            org.softcaster.engine.enums.TxnSide side) {
        BondFutureMasterData bfmd = (BondFutureMasterData) masterData;
        ProcInputData input = new ProcInputData();
        input.setPrice(price * bfmd.getMultiplier());
        input.setPriceMultiplier(bfmd.getMultiplier());
        input.setQuantity(quantity * bfmd.getContractValue());
        input.setSide(side);
        return input;
    }

}
