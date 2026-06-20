/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.processors;

import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mtm.jobs.IMtmDataHelper;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondEvaluator implements IPositionEvaluator {

    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper) {
        System.out.println(masterData.getCode() + " : " + masterData.getDescription());
    }
}
