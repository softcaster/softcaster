/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.services;

import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.easy_pricer_mds_core.IMtmDataHelper;
import org.softcaster.easy_pricer_mtm.context.ValuationContext;
import org.softcaster.easy_pricer_mtm.exceptions.MtmException;
import org.softcaster.easy_pricer_mtm.evaluators.EvaluatorDispatcher;
import org.softcaster.easy_pricer_mtm.evaluators.IPositionEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MtmService {

    private static final Logger log = LoggerFactory.getLogger(MtmService.class);

    @Autowired
    private PositionDetailDAO positionDetailDAO;

    @Autowired
    EvaluatorDispatcher evaluatorDispatcher;

    @Autowired
    MasterDataDAO masterDataDAO;

    // ogni position viene elaborata e committata singolarmente
    @Transactional
    public void evaluatePosition(Integer positionMdId, Integer masterDataId, Integer counterpartyId, IMtmDataHelper mtmHelper, ValuationContext context) {

        PositionDetail position = positionDetailDAO.findByPositionMdAndMasterDataAndCounterparty(positionMdId, masterDataId, counterpartyId).orElseGet(() -> {
            throw new MtmException("Invalid position");
        });

        // classe reale: gli evaluator distinguono il tipo di anagrafica
        MasterData masterData = (MasterData) Hibernate.unproxy(masterDataDAO.findByIdMasterData(masterDataId));
        String assetClass = masterData.getAssetClass().getCode();

        // posizione chiusa su uno strumento scaduto (step MATURITY): niente da
        // valutare, e la valutazione fallirebbe (nessun flusso, nessuna quotazione)
        double buyQty = position.getBuyQty() == null ? 0. : position.getBuyQty();
        double sellQty = position.getSellQty() == null ? 0. : position.getSellQty();
        java.time.LocalDate officialDate = mtmHelper.getOfficialDate();
        if (masterData.getMaturityDate() != null && officialDate != null
                && !masterData.getMaturityDate().toLocalDate().isAfter(officialDate)
                && org.softcaster.easy_pricer_mds_core.PnlCalc.isFlat(buyQty, sellQty)) {
            position.setUnrealizedPnl(0.);
            positionDetailDAO.saveOrUpdate(position);
            log.info("Mtm instrument {}: matured, position closed, not valued", masterData.getCode());
            return;
        }

        IPositionEvaluator evaluator = evaluatorDispatcher.dispatch(assetClass);
        if (evaluator != null) {
            evaluator.evaluate(position, masterData, mtmHelper, context);
            positionDetailDAO.saveOrUpdate(position);
            log.info("Mtm intrument: " + masterData.getCode());
        } else {
            throw new MtmException("Invalid evaluator");
        }
    }
}
