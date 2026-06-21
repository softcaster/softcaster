/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component("FSP")
public class ForexTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    public ForexTxnProcessor() {
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {
        ForexMasterData fmd = (ForexMasterData) txn.getMasterData();
        if (fmd == null) {
            throw new TxnProcessingException("Invalid processor");
        }

        ProcInputData input = new ProcInputData();
        input.setPrice(txn.getPrice());
        input.setQuantity(txn.getQuantity());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatusPreElab());

        super.process(input, position);
    }

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }
}
