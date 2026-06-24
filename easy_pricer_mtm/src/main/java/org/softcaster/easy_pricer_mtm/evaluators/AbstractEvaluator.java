/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.evaluators;

import org.softcaster.core.data.PositionDetail;

/**
 *
 * @author ep
 */
public class AbstractEvaluator {
    
    protected double calcUnrealizedPL(double mktPrice, PositionDetail position) {
       
        // Calcolo solo su qty buy rimanente o potenziale
        double capacity = position.getBuyQty() - position.getSellQty();
        if (capacity < 0.) {
            capacity *= (-1.);
        }

        double avgBuyPrice = 0.;
        if (position.getBuyQty() > 0) {
            avgBuyPrice = position.getNotionalValueBuy() / position.getBuyQty();
        }
        double unrealizedPnL = (mktPrice - avgBuyPrice) * capacity;
        
        return unrealizedPnL;
    }
}
