/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.jobs;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mtm.jobs.services.EngineStateManager;
import org.softcaster.provider.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MtMPollingJob {
    
    private static final Logger log = LoggerFactory.getLogger(MtMPollingJob.class);

    @Autowired
    private EngineStateManager engineStateManager;
    
    @Autowired    
    @Qualifier("marketDataService") 
    private MarketDataService marketDataService;

    @Autowired
    private PositionDetailDAO positionDetailDAO;
    
    private void runMtm() {
        // load positions
        List<PositionDetail> positions = positionDetailDAO.findAll();
        for(PositionDetail position: positions) {
            
        }
    }
    
    @Scheduled(fixedDelay = 15000)
    public void pollPositionDetails() {
        if (engineStateManager.isSuspended()) {
            log.info("=== [PSRV] Service is suspended ===\n");
            return;
        }
        log.info("=== [MSRV] Starting mtm... ===\n");
        marketDataService.loadSpotPrice();
                
        log.info("EURUSD: " + marketDataService.getSpotPrice("EURUSD", RequestType.BID));
    }

}
