/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component("XRB")
public class XBondTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        SecurityMasterData smd = null;
        if (txn.getMasterData() instanceof SecurityMasterData bond) {
            smd = bond;
        }
        if (smd == null) {
            throw new TxnProcessingException("Invalid processor");
        }
        double accruedInterest = calculateAccruedInterest(smd);
        ProcInputData input = new ProcInputData();
        input.setPrice((txn.getPrice() + accruedInterest) * smd.getMultiplier());
        input.setQuantity(txn.getQuantity());
        input.setSide(txn.getTxnSide().getId());
        input.setStatus(txn.getTxnStatus().getCode());

        super.process(input, position);
    }

    @Override
    protected boolean shortSellEnabled() {
        return false;
    }

    protected double calculateAccruedInterest(SecurityMasterData smd) {
        return 0.;
    }
}
