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

        ProcInputData input = new ProcInputData();
        input.setPrice(txn.getPrice() * cfmd.getMultiplier());
        input.setQuantity(txn.getQuantity() * cfmd.getContractValue());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatusPreElab());

        super.process(input, position);

        // Aggiungo component initial margin
        FinancialTxnComponent initialMargin = new FinancialTxnComponent();
        initialMargin.setCurrency(cfmd.getCurrency());
        initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
        initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
        initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * cfmd.getInitialMargin()));
        txn.addTxnComponent(initialMargin);
    }
}
