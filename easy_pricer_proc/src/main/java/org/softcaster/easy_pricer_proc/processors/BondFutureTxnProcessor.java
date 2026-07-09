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

        ProcInputData input = new ProcInputData();
        input.setPrice(txn.getPrice() * bfmd.getMultiplier());
        input.setQuantity(txn.getQuantity() * bfmd.getContractValue());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatus());

        super.process(input, position);

        // Aggiungo component initial margin
        FinancialTxnComponent initialMargin = new FinancialTxnComponent();
        initialMargin.setCurrency(bfmd.getCurrency());
        initialMargin.setDescription("Initial Margin txn: " + txn.getIdFinancialTxn());
        initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
        initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * bfmd.getInitialMargin()));
        txn.addTxnComponent(initialMargin);

    }

}
