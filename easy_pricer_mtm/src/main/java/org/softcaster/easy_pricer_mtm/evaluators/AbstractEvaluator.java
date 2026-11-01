/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.PnlCalc;

/**
 *
 * @author ep
 */
public class AbstractEvaluator {
    
    /**
     * P&L non realizzato sulla quantita' netta, con la stessa formula del
     * processor (PnlCalc): posizione lunga contro il prezzo medio di acquisto,
     * corta contro il prezzo medio di vendita.
     */
    protected double calcUnrealizedPL(double mktPrice, PositionDetail position) {
        return PnlCalc.unrealized(position.getBuyQty(), position.getSellQty(),
                position.getNotionalValueBuy(), position.getNotionalValueSell(), mktPrice);
    }
}
