/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.PositionDetail;

/**
 *
 * @author ep
 */
public abstract class AbstractTxnProcessor {

    protected abstract boolean shortSellEnabled();

    public void process(ProcInputData input, PositionDetail position) {
        double quantity = input.getQuantity();
        // Se transazione cancellata, inverto quantita, mantenendo pero il side
        if (input.getStatus().equalsIgnoreCase("CANCELLED")) {
            quantity = quantity * (-1.);
        }
        double notionalValue = input.getPrice() * quantity;
        switch (input.getSide()) {
            case ITxnProcessor.BUY -> {
                position.setBuyQty(position.getBuyQty() + quantity);
                position.setNotionalValueBuy(position.getNotionalValueBuy() + notionalValue);
            }
            case ITxnProcessor.SELL -> {
                position.setSellQty(position.getSellQty() + quantity);
                position.setNotionalValueSell(position.getNotionalValueSell() + notionalValue);
            }
        }
        
        // Calcolo realized P&L
        calcRealizedPnL(position);

        // Calcolo unrealized P&L
        calcUnRealizedPnL(position);
    }

    protected void calcRealizedPnL(PositionDetail position) {

        // Controllo se abilitato short selling
        if (!shortSellEnabled() && Double.compare(position.getBuyQty(), 0.) == 0) 
            return;
        
        // Calcolo solo su qty sell
        {
            if (position.getSellQty() > 0.) {
                // Calcolo prezzo medio buy
                if (position.getBuyQty() > 0) {
                    double avgBuyPrice = position.getNotionalValueBuy() / position.getBuyQty();
                    double avgSellPrice = position.getNotionalValueSell() / position.getSellQty();
                    double realizedPnL = (avgSellPrice - avgBuyPrice) * position.getSellQty();
                    position.setRealizedPnl(realizedPnL);
                }
            }
        }
    }

    protected void calcUnRealizedPnL(PositionDetail position) {
        // Calcolo solo su qty buy rimanente
        double deltaQty = position.getBuyQty() - position.getSellQty();
        if (deltaQty > 0.) {
            // Calcolo prezzo medio buy
            if (position.getBuyQty() > 0) {
                double avgBuyPrice = position.getNotionalValueBuy() / position.getBuyQty();
                double unrealizedPnL = (position.getMarketPrice() - avgBuyPrice) * deltaQty;
                position.setUnrealizedPnl(unrealizedPnL);
            }
        } else {
            // Nessun unrealized, tutto realized
            position.setUnrealizedPnl(0.);
        }
    }

}
