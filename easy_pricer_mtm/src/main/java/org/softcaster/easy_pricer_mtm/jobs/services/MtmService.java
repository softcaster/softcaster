/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.jobs.services;

import jakarta.transaction.Transactional;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.easy_pricer_mtm.jobs.IMtmDataHelper;
import org.softcaster.easy_pricer_mtm.processors.EvaluatorDispatcher;
import org.softcaster.easy_pricer_mtm.processors.IPositionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MtmService {
    
    @Autowired
    private PositionDetailDAO positionDetailDAO;

    @Autowired
    EvaluatorDispatcher evaluatorDispatcher;

    @Autowired
    MasterDataDAO masterDataDAO;    
    
    // ogni position viene elaborata e committata singolarmente
    @Transactional 
    public void evaluatePosition(Integer positionMdId, Integer masterDataId, Integer counterpartyId, IMtmDataHelper mtmHelper) {
        
        PositionDetail position = positionDetailDAO.findByPositionMdAndMasterDataAndCounterparty(positionMdId,  masterDataId, counterpartyId).orElseGet(() -> {
          throw new MtmException("Invalid position");
        });

        MasterData masterData = masterDataDAO.findByIdMasterData(masterDataId);
        String assetClass = masterData.getAssetClass().getCode();

        IPositionEvaluator evaluator = evaluatorDispatcher.dispatch(assetClass);
        if(evaluator != null)
            evaluator.evaluate(position, masterData, mtmHelper);
        else 
            throw new MtmException("Invalid evaluator");
    }
}
