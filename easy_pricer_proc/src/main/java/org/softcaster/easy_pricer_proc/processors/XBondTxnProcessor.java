/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.PositionDetail;
import org.softcaster.easy_pricer_core.data.SecurityMasterData;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component("XRB")
public class XBondTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        double accruedInterest = 0.;
        if (txn.getMasterData() instanceof SecurityMasterData bond) {
            accruedInterest = calculateAccruedInterest(bond);
        }

        ProcInputData input = new ProcInputData();
        input.setPrice(txn.getPrice() + accruedInterest);
        input.setQuantity(txn.getQuantity());
        input.setSide(txn.getTxnSide());
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
