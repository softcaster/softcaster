/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.processors;

import java.util.List;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.dto.PositionDealRow;
import org.softcaster.easy_pricer_mds_core.PnlCalc;
import org.softcaster.engine.enums.TxnSide;

/**
 *
 * @author ep
 */
public abstract class AbstractTxnProcessor {

    protected abstract boolean shortSellEnabled();

    /**
     * Prezzo e quantita' del deal in base unitaria per i conti di posizione.
     */
    protected abstract ProcInputData positionInput(MasterData masterData, double quantity, double price, TxnSide side);

    /**
     * Ricostruisce quantita', controvalori, realized e ratei dai deal rimasti,
     * dopo un cancel o un amend: la posizione e' quella che sarebbe senza il
     * deal, anche se questo cambia cicli gia' chiusi. Un deal opposto non lo
     * sarebbe: inciderebbe sui prezzi di carico. I dati di mercato restano, li
     * aggiorna il MtM.
     */
    public void rebuildPosition(PositionDetail p, MasterData masterData, List<PositionDealRow> deals) {
        p.resetCycleTotals();
        p.setRealizedPnl(0.);
        p.setBuyAccrual(0.);
        p.setSellAccrual(0.);
        for (PositionDealRow deal : deals) {
            TxnSide side = deal.quantity() < 0 ? TxnSide.SELL : TxnSide.BUY;
            process(positionInput(masterData, Math.abs(deal.quantity()), deal.price(), side), p);
            if (deal.accrual() != null) {
                double accrual = deal.accrual().doubleValue();
                if (side == TxnSide.BUY) {
                    p.setBuyAccrual(p.getBuyAccrual() + accrual);
                } else {
                    p.setSellAccrual(p.getSellAccrual() + accrual);
                }
            }
        }
    }

    public void process(ProcInputData input, PositionDetail p) {
        double quantity = input.getQuantity();
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

        if (shortSellEnabled()) {
            // f(dopo) - f(prima): realizedPnl e' un cumulato, mai sovrascritto
            double realizedAfter = realizedOf(p);
            double current = p.getRealizedPnl() == null ? 0. : p.getRealizedPnl();
            p.setRealizedPnl(current + realizedAfter - realizedBefore);
        }

        // Mark: market price se disponibile (quotato, portato a unitario come il
        // prezzo del deal), altrimenti prezzo del deal
        double mark = (p.getMarketPrice() != null && p.getMarketPrice() > 0.)
                ? p.getMarketPrice() * input.getPriceMultiplier() : input.getPrice();
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
