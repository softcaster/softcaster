/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.jobs;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.easy_pricer_core.data.FinancialTxn;
import org.softcaster.easy_pricer_core.data.FinancialTxnDAO;
import org.softcaster.easy_pricer_core.data.PositionDetail;
import org.softcaster.easy_pricer_core.data.PositionDetailDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author ep
 */
@Component
public class FinTxnPollingJob {

    private static final Logger log = LoggerFactory.getLogger(FinTxnPollingJob.class);

    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private PositionDetailDAO positionRepository;

    /*
    @Autowired
    private EasyPricerEngine engine;
     */
    protected void elabFinancialTxn(FinancialTxn txn, PositionDetail position) {

    }

    // Esegue il polling ogni 5 secondi (5000 millisecondi)
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollPendingTrades() {
        // 1. Cerca le transazioni PENDING
        List<FinancialTxn> pendingTxn = financialTxnDAO.findByTxnStatusCode("PENDING");

        if (!pendingTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} PENDING transaction ===", pendingTxn.size());

            for (FinancialTxn txn : pendingTxn) {
                try {
                    // 1. Ricerca 
                    PositionDetail position = positionRepository.findByPositionMdAndMasterDataAndCounterparty(
                            txn.getPositionMd().getIdPosition(), txn.getMasterData().getIdMasterData(),
                            txn.getCounterparty().getIdCounterparty()).orElseGet(() -> {
                        PositionDetail newPosition = new PositionDetail();
                        newPosition.setPositionMd(txn.getPositionMd().getIdPosition());
                        newPosition.setMasterData(txn.getMasterData().getIdMasterData());
                        newPosition.setCounterparty(txn.getCounterparty().getIdCounterparty());
                        return newPosition;
                    });

                    // 2. Elaborazione
                    log.info("### Processing ID: {}", txn.getIdFinancialTxn());
                    elabFinancialTxn(txn, position);

                    // 3. Salvataggio stato
                    //tradeRepository.save(trade);
                } catch (Exception e) {
                    log.error("### ERROR ID {}: {}", txn.getIdFinancialTxn(), e.getMessage());
                }
            }
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }
}
