/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_mtm.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mtm.jobs.MtMPollingJob;
import org.softcaster.easy_pricer_mtm.services.EngineStateManager;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalSystem {

    private static final Logger log = LoggerFactory.getLogger(InternalSystem.class);

    @Autowired
    private MtMPollingJob mtMPollingJob;
    @Autowired
    private EngineStateManager stateManager;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Dopo il cambio data della chiusura di giornata: rilegge la data ufficiale.
     * Le curve le ricostruisce il prossimo giro di polling.
     */
    @PostMapping(value = "/internal/system/refresh_date")
    public ResponseEntity<String> refreshDate() {
        marketDataService.refreshOfficialDate();
        return new ResponseEntity<>("Official date refreshed", HttpStatus.OK);
    }

    @PostMapping(value = "/internal/system/suspend")
    public ResponseEntity<String> suspend() {
        // Cambia lo stato centralizzato
        stateManager.suspend();
        log.info("Service suspended...");
        return new ResponseEntity<>("Service suspended", HttpStatus.OK);
    }

    @PostMapping(value = "/internal/system/resume")
    public ResponseEntity<String> resume() {
        // Cambia lo stato centralizzato
        stateManager.resume();
        log.info("Service resumed...");
        return new ResponseEntity<>("Service resumed", HttpStatus.OK);
    }
    
    @PostMapping(value = "/internal/system/execute")
    public ResponseEntity<String> executeJob() {
        boolean result = mtMPollingJob.executeJob();
        String response = "OK";
        HttpStatus status = HttpStatus.OK;
        if(!result) {
            status = HttpStatus.NOT_FOUND;
            response = "Mtm job failed!";
        }
            
        return new ResponseEntity<>(response, status);
    }
    
    /**
     * MtM di fine giornata alla data ufficiale: valutazione di tutte le
     * posizioni e fotografie per la contabilita' (eventi MTM). Da lanciare
     * dopo il caricamento dei prezzi di chiusura.
     */
    @PostMapping(value = "/internal/mtm/eod")
    public ResponseEntity<EodTaskResult> executeEod() {
        EodTaskResult result;
        try {
            result = mtMPollingJob.executeEod();
        } catch (Exception e) {
            log.error("### EOD MTM failed", e);
            result = EodTaskResult.failed(EodStep.MTM, null, e.getLocalizedMessage());
        }
        HttpStatus status = result.status() == EodStatus.FAILED ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }

    @GetMapping(value = "/internal/system/alive")
    public ResponseEntity<String> getOfficialDate() {
        return new ResponseEntity<>("Mtm Service Alive", HttpStatus.OK);
    }
}
