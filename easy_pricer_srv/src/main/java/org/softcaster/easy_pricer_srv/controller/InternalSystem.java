/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_srv.services.EngineStateManager;
import org.softcaster.easy_pricer_srv.services.FxRollingService;
import org.softcaster.easy_pricer_srv.services.SystemDateManager;
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
    private EngineStateManager stateManager;

    @Autowired
    SystemDateManager systemDateManager;

    @Autowired
    private FxRollingService fxRollingService;

    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;
    
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
    
    @GetMapping(value = "/internal/system/alive")
    public ResponseEntity<String> alive() {
        return new ResponseEntity<>("Trade Capture Service Alive", HttpStatus.OK);
    }

    /**
     * Task FX_ROLLING della chiusura di giornata.
     */
    @PostMapping(value = "/internal/system/fx_rolling")
    public ResponseEntity<EodTaskResult> fxRolling() {
        EodTaskResult result;
        try {
            result = fxRollingService.rollPositions();
        } catch (Exception e) {
            log.error("### EOD FX rolling failed", e);
            result = EodTaskResult.failed(EodStep.FX_ROLLING, null, e.getLocalizedMessage());
        }
        log.info("=== EOD task {} ===", result);
        HttpStatus status = result.status() == EodStatus.FAILED ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }

    /**
     * Dopo il cambio data della chiusura di giornata: rilegge la data ufficiale
     * e ricostruisce le curve, ferme alla data vecchia.
     */
    @PostMapping(value = "/internal/system/refresh_date")
    public ResponseEntity<String> refreshDate() {
        marketDataService.refreshOfficialDate();
        systemDateManager.init();
        marketDataService.reloadCurves();
        return new ResponseEntity<>(systemDateManager.getOfficialBusinessDate().toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/internal/system/official_date")
    public ResponseEntity<String> getOfficialDate() {
        String officialDate = systemDateManager.getOfficialBusinessDate().toString();
        return new ResponseEntity<>(officialDate, HttpStatus.OK);
    }
}
