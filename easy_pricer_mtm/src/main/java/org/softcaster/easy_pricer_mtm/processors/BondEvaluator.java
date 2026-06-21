/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.processors;

import org.softcaster.core.data.InstrumentValuation;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("XRB")
public class BondEvaluator implements IPositionEvaluator {
    
    @Autowired
    MasterDataDAO masterDataDAO;
    
    @Override
    public void evaluate(PositionDetail position, MasterData masterData, IMtmDataHelper mtmHelper) {
        /*
        InstrumentValuation iv = new InstrumentValuation();
        iv.setAccruedInterest(2.);
        iv.setDuration(10.);
        iv.setMarketPrice(100.);
        iv.setModDuration(100.);
        iv.setTheoreticalPrice(100.);
        iv.setYtm(0.5);
        masterData.setInstrumentValuation(iv);
        masterDataDAO.saveOrUpdate(masterData);
*/
        System.out.println(masterData.getCode() + " : " + masterData.getDescription() + " " + mtmHelper.getOfficialDate());
    }
}
