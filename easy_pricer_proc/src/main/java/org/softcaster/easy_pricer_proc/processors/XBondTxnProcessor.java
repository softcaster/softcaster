/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.util.ArrayList;
import java.util.List;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.analytics.BondPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.dto.BondInputData;
import org.softcaster.engine.dto.BondOutputData;
import org.softcaster.engine.enums.Compounding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component("XRB")
public class XBondTxnProcessor extends AbstractTxnProcessor implements ITxnProcessor {

    @Autowired
    @Qualifier("bondPricer") 
    private BondPricer bondPricer;

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {

        SecurityMasterData smd = null;
        if (txn.getMasterData() instanceof SecurityMasterData bond) {
            smd = bond;
        }
        if (smd == null) {
            throw new TxnProcessingException("Invalid processor");
        }

        
        BondInputData bondInputData = new BondInputData();
        bondInputData.setSpotPrice(txn.getPrice());
        bondInputData.setValuationDate(txn.getSettlement().toLocalDate());
        bondInputData.setCompounding(Compounding.COMPOUNDED);
        bondInputData.setDaycount(smd.getAccrualDaycount());
        bondInputData.setFrequency(smd.getFrequency());
        bondInputData.setFlows(getFlows(smd.getCashFlows()));
        BondOutputData bondOutputData = bondPricer.calculate(bondInputData);
        
        ProcInputData input = new ProcInputData();
        input.setPrice((txn.getPrice() + bondOutputData.getAccruedInterest()) * smd.getMultiplier());
        input.setQuantity(txn.getQuantity());
        input.setSide(txn.getTxnSide());
        input.setStatus(txn.getTxnStatus());

        super.process(input, position);
    }

    @Override
    protected boolean shortSellEnabled() {
        return false;
    }

    private List<CashFlow> getFlows(List<CashFlowItem> cashFlows) {
        List<CashFlow> flows = null;
        
        if(!cashFlows.isEmpty()) {
            flows = new ArrayList<>();
            for(CashFlowItem item: cashFlows) {
                
            }
        }
        
        return flows;
    }
}
