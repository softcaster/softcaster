/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.core.data.PositionDetail;
import org.softcaster.engine.enums.TxnStatus;

/**
 *
 * @author ep
 */
public abstract class AbstractTxnProcessor {

    protected abstract boolean shortSellEnabled();

    public void process(ProcInputData input, PositionDetail position) {
        double quantity = input.getQuantity();
        // Se transazione cancellata o modificata, inverto quantita, mantenendo pero il side
        if (input.getStatus() == TxnStatus.TO_CANCEL
                || input.getStatus() == TxnStatus.TO_AMEND) {
            quantity = quantity * (-1.);
        }
        double notionalValue = input.getPrice() * quantity;
        switch (input.getSide()) {
            case BUY -> {
                position.setBuyQty(position.getBuyQty() + quantity);
                position.setNotionalValueBuy(position.getNotionalValueBuy() + notionalValue);
            }
            case SELL -> {
                position.setSellQty(position.getSellQty() + quantity);
                position.setNotionalValueSell(position.getNotionalValueSell() + notionalValue);
            }
        }

        // Calcolo realized P&L
        calcRealizedPnL(position);

        // Calcolo unrealized P&L
        calcUnRealizedPnL(input, position);
    }

    protected void calcRealizedPnL(PositionDetail position) {

        // Controllo se abilitato short selling
        if (!shortSellEnabled()) {
            return;
        }

        // Se quantità sell o buy pari a zero non ho realizes
        if (Double.compare(position.getBuyQty(), 0.) == 0 || Double.compare(position.getSellQty(), 0.) == 0) {
            return;
        }

        // calcolo capacity
        double capacity = 0;
        // Se acquisti > vendite calcolo realized su tutta la quantita venduta
        if (position.getBuyQty() > position.getSellQty()) {
            capacity = position.getSellQty();
        } // se vendite > acquisti calcolo realized su parte utilizzata dalla vendita
        else {
            capacity = position.getBuyQty();
        }

        double avgBuyPrice = 0.;
        if (position.getBuyQty() > 0) {
            avgBuyPrice = position.getNotionalValueBuy() / position.getBuyQty();
        }

        double avgSellPrice = 0.;
        if (position.getSellQty() > 0) {
            avgSellPrice = position.getNotionalValueSell() / position.getSellQty();
        }

        double realizedPnL = (avgSellPrice - avgBuyPrice) * capacity;
        position.setRealizedPnl(realizedPnL);
    }

    protected void calcUnRealizedPnL(ProcInputData input, PositionDetail position) {
        // Calcolo solo su qty buy rimanente o potenziale
        double capacity = position.getBuyQty() - position.getSellQty();
        if (capacity < 0.) {
            capacity *= (-1.);
        }

        double avgBuyPrice = 0.;
        if (position.getBuyQty() > 0) {
            avgBuyPrice = position.getNotionalValueBuy() / position.getBuyQty();
        }
        double unrealizedPnL = (input.getPrice() - avgBuyPrice) * capacity;
        position.setUnrealizedPnl(unrealizedPnL);
    }

}
