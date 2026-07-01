/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.math.BigDecimal;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnComponent;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnComponentType;
import org.springframework.stereotype.Component;

/**
 *
 * @author softc
 */
@Component("FFU")
public class FxFutureTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Override
    protected boolean shortSellEnabled() {
        return true;
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        FxFutureMasterData ffmd = (FxFutureMasterData) txn.getMasterData();
        if (ffmd == null) {
            throw new TxnProcessingException("Invalid processor");
        }

        ProcInputData input = new ProcInputData();
        input.setPrice(txn.getPrice() * ffmd.getMultiplier());
        input.setQuantity(txn.getQuantity() * ffmd.getContractValue());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatus());

        super.process(input, position);

        // Aggiungo component initial margin
        FinancialTxnComponent initialMargin = new FinancialTxnComponent();
        initialMargin.setCurrency(ffmd.getCurrency());
        initialMargin.setDescription("Accruals txn: " + txn.getIdFinancialTxn());
        initialMargin.setComponentType(TxnComponentType.INITIAL_MARGIN);
        initialMargin.setAmount(BigDecimal.valueOf(txn.getQuantity() * ffmd.getInitialMargin()));
        txn.addTxnComponent(initialMargin);

    }

}
