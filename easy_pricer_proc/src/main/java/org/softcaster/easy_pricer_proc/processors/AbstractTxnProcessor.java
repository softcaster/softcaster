/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.PnlCalc;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.TxnStatus;

/**
 *
 * @author ep
 */
public abstract class AbstractTxnProcessor {

    protected abstract boolean shortSellEnabled();

    public void process(ProcInputData input, PositionDetail p) {
        double quantity = input.getQuantity();
        // Cancel/amend: inverto la quantita', mantengo il side
        if (input.getStatus() == TxnStatus.TO_CANCEL || input.getStatus() == TxnStatus.TO_AMEND) {
            quantity = -quantity;
        }
        double notional = input.getPrice() * quantity;

        // f(prima)
        double realizedBefore = realizedOf(p);

        switch (input.getSide()) {
            case BUY -> {
                p.setBuyQty(p.getBuyQty() + quantity);
                p.setNotionalValueBuy(p.getNotionalValueBuy() + notional);
            }
            case SELL -> {
                p.setSellQty(p.getSellQty() + quantity);
                p.setNotionalValueSell(p.getNotionalValueSell() + notional);
            }
        }

        // Guard: un cancel/amend su un ciclo gia' azzerato porterebbe quantita' negative
        if (p.getBuyQty() < -PnlCalc.QTY_EPS || p.getSellQty() < -PnlCalc.QTY_EPS) {
            throw new TxnProcessingException("Quantita' negative: cancel/amend su ciclo gia' chiuso, gestire come rettifica");
        }

        if (shortSellEnabled()) {
            // f(dopo) - f(prima): realizedPnl e' un cumulato, mai sovrascritto
            double realizedAfter = realizedOf(p);
            double current = p.getRealizedPnl() == null ? 0. : p.getRealizedPnl();
            p.setRealizedPnl(current + realizedAfter - realizedBefore);
        }

        // Mark: market price se disponibile, altrimenti prezzo del deal
        double mark = (p.getMarketPrice() != null && p.getMarketPrice() > 0.) ? p.getMarketPrice() : input.getPrice();
        p.setUnrealizedPnl(PnlCalc.unrealized(p.getBuyQty(), p.getSellQty(),
                p.getNotionalValueBuy(), p.getNotionalValueSell(), mark));

        // Flat: il realized e' gia' fissato nel cumulato, azzero i totali del ciclo
        if (shortSellEnabled() && PnlCalc.isFlat(p.getBuyQty(), p.getSellQty())) {
            p.resetCycleTotals();
        }
    }

    private static double realizedOf(PositionDetail p) {
        return PnlCalc.realized(p.getBuyQty(), p.getSellQty(),
                p.getNotionalValueBuy(), p.getNotionalValueSell());
    }
}
