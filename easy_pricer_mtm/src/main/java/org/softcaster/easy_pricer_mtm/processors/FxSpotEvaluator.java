/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.processors;

import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mtm.jobs.IMtmDataHelper;
import org.softcaster.provider.enums.RequestType;
import org.springframework.stereotype.Component;

@Component("FSP")
public class FxSpotEvaluator implements IPositionEvaluator {

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper) {
        
        double avgBuyPrice = position.getNotionalValueBuy()/position.getBuyQty();
        double mktPrice = mtmHelper.getSpotPrice(masterData.getCode(), RequestType.ASK);
        double unrealized = (mktPrice - avgBuyPrice)*(position.getBuyQty() - position.getSellQty());
        System.out.println(unrealized);
    }
    
}
