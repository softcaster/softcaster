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
import org.softcaster.easy_pricer_core.data.TxnStatusDAO;
import org.softcaster.easy_pricer_proc.processors.ITxnProcessor;
import org.softcaster.easy_pricer_proc.processors.ProcessorDispatcher;
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
    @Autowired
    private ProcessorDispatcher processorDispatcher;
    @Autowired
    private TxnStatusDAO txnStatusDAO;

    /*
    @Autowired
    private EasyPricerEngine engine;
     */
    protected boolean elabFinancialTxn(FinancialTxn txn, PositionDetail position) {
        ITxnProcessor processor = processorDispatcher.dispatch(txn.getMasterData().getAssetClass().getCode());
        if (processor != null) {
            processor.process(txn, position);
            return true;
        } else {
            return false;
        }
    }

    protected boolean elabFinancialTxnList(List<FinancialTxn> financialTxnList) {
        for (FinancialTxn txn : financialTxnList) {
            try {
                // 1. Ricerca 
                PositionDetail position = positionRepository.findByPositionMdAndMasterDataAndCounterparty(
                        txn.getPositionMd().getIdPosition(), txn.getMasterData().getIdMasterData(),
                        txn.getCounterparty().getIdCounterparty()).orElseGet(() -> {
                    PositionDetail newPosition = new PositionDetail();
                    newPosition.setPositionMd(txn.getPositionMd().getIdPosition());
                    newPosition.setMasterData(txn.getMasterData().getIdMasterData());
                    newPosition.setCounterparty(txn.getCounterparty().getIdCounterparty());
                    newPosition.initialize();
                    return newPosition;
                });

                // 2. Elaborazione
                log.info("### Processing ID: {}", txn.getIdFinancialTxn());
                if (elabFinancialTxn(txn, position)) {
                    // 3. Salvataggio position e aggiornamento status transazione
                    positionRepository.saveOrUpdate(position);
                    if (txn.getTxnStatus().getCode().equalsIgnoreCase("PENDING")) {
                        txn.setTxnStatus(txnStatusDAO.findByCode("EXECUTED"));
                    } else if(txn.getTxnStatus().getCode().equalsIgnoreCase("CANCELLED")) {
                        txn.setTxnStatus(txnStatusDAO.findByCode("CANCELLED_EXECUTED"));
                    }
                    financialTxnDAO.saveOrUpdate(txn);
                }

            } catch (Exception e) {
                log.error("### ERROR ID {}: {}", txn.getIdFinancialTxn(), e.getMessage());
                return false;
            }
        }

        return true;
    }

    protected void pollPendingTrades() {
        // 1. Cerca le transazioni PENDING
        List<FinancialTxn> pendingTxn = financialTxnDAO.findByTxnStatusCode("PENDING");

        if (!pendingTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} PENDING transaction ===", pendingTxn.size());
            elabFinancialTxnList(pendingTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    protected void pollCancelledTrades() {
        // 1. Cerca le transazioni CANCELLED
        List<FinancialTxn> cancelledTxn = financialTxnDAO.findByTxnStatusCode("CANCELLED");

        if (!cancelledTxn.isEmpty()) {
            log.info("=== [BATCH START] find {} CANCELLED transaction ===", cancelledTxn.size());
            elabFinancialTxnList(cancelledTxn);
            log.info("=== [BATCH END] Processing completed ===\n");
        }
    }

    // Esegue il polling ogni 15 secondi (15000 millisecondi)
    @Scheduled(fixedDelay = 15000)
    @Transactional
    public void pollTrades() {
        // 1. Elabora le transazioni PENDING
        pollPendingTrades();

        // 2. Elabora le transazioni CANCELLED
        pollCancelledTrades();
    }
}
