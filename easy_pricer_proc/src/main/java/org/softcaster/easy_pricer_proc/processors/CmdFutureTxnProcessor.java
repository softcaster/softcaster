/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.softcaster.core.data.CmdFutureMasterData;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.springframework.stereotype.Component;

@Component("CFU")
public class CmdFutureTxnProcessor  extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {
        CmdFutureMasterData cfmd = (CmdFutureMasterData) txn.getMasterData();
        if (cfmd == null) {
            throw new TxnProcessingException("Invalid processor");
        }

        super.process(positionInput(cfmd, txn.getQuantity(), txn.getPrice(), txn.getTxnSide()), position);

        if (!marginApplies(txn)) {
            return;
        }
        // Aggiungo component initial margin
        FinancialTxnComponent initialMargin = new FinancialTxnComponent();
        initialMargin.setCurrency(cfmd.getCurrency());
        initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
        initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
        initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * cfmd.getInitialMargin()));
        txn.addTxnComponent(initialMargin);
    }

    @Override
    protected ProcInputData positionInput(org.softcaster.core.data.MasterData masterData, double quantity, double price,
            org.softcaster.engine.enums.TxnSide side) {
        CmdFutureMasterData cfmd = (CmdFutureMasterData) masterData;
        ProcInputData input = new ProcInputData();
        input.setPrice(price * cfmd.getMultiplier());
        input.setPriceMultiplier(cfmd.getMultiplier());
        input.setQuantity(quantity * cfmd.getContractValue());
        input.setSide(side);
        return input;
    }
}
