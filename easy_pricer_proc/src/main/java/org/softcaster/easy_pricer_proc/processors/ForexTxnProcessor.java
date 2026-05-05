/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.PositionDetail;
import org.springframework.stereotype.Component;

/**
 *
 * @author ep
 */
@Component("FSP")
public class ForexTxnProcessor implements ITxnProcessor {

    public ForexTxnProcessor() {
    }

    @Override
    public void process(FinancialTxn txn, PositionDetail position) {
        double quantity = txn.getQuantity();
        double notionalValue = txn.getPrice() * quantity;
        switch (txn.getTxnSide()) {
            case 1 -> {
                position.setBuyQty(position.getBuyQty() + quantity);
                position.setNotionalValueBuy(position.getNotionalValueBuy() + notionalValue);
            }
            case -1 -> {
                position.setSellQty(position.getSellQty() + quantity);
                position.setNotionalValueSell(position.getNotionalValueSell() + notionalValue);
            }
        }
    }
}
