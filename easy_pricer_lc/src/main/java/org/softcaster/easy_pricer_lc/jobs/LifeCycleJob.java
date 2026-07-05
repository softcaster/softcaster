/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.jobs;

import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.easy_pricer_lc.services.EngineStateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LifeCycleJob {

    private static final Logger log = LoggerFactory.getLogger(LifeCycleJob.class);

    @Autowired
    private EngineStateManager engineStateManager;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    SystemBusinessCalendarDAO sbc;
    // Iniezione del pool di thread 
    @Autowired
    @Qualifier("lifeCycleExecutor")
    private TaskExecutor taskExecutor;

    public void runLifeCycles() {
        runSettlementLyfeCycle();
    }

    private void runSettlementLyfeCycle() {
        List<PositionMasterData> positions = positionMasterDataDAO.findAll();

        if (!positions.isEmpty()) {
            for (PositionMasterData pmd : positions) {
                fetchPositionDetails(pmd,this::generateSettlementEvent);
            }
        }
    }

    void generateSettlementEvent(PositionDetail detail) {

    }

    private void fetchPositionDetails(PositionMasterData pmd, Consumer<PositionDetail> processor) {
        List<PositionDetail> details = positionDetailDAO.fetchAndClaimByPositionMasterData(pmd, 15).orElse(null);

        if (details != null && !details.isEmpty()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (PositionDetail detail : details) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        processor.accept(detail);
                    } catch (Exception e) {
                        log.error("### LIFECYCLE ERROR FOR POSITION DETAIL ID {}: {}", detail.getIdPositionDetail(), e.getMessage());
                    }
                }, taskExecutor);
                futures.add(future);
            }
            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
        }
    }
}
