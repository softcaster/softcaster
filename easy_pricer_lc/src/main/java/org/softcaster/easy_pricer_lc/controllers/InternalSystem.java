/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.controllers;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_lc.jobs.LifeCycleJob;
import org.softcaster.easy_pricer_lc.services.EngineStateManager;
import org.softcaster.easy_pricer_lc.services.FxRollingService;
import org.softcaster.easy_pricer_lc.services.MaturityService;
import org.softcaster.easy_pricer_lc.services.RateFixingService;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.springframework.beans.factory.annotation.Autowired;
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
    private LifeCycleJob lcJob;

    @Autowired
    private FxRollingService fxRollingService;

    @Autowired
    private RateFixingService rateFixingService;

    @Autowired
    private MaturityService maturityService;

    @Autowired
    private org.softcaster.easy_pricer_lc.services.AccumulatorLifeCycleService accumulatorService;

    @Autowired
    @org.springframework.beans.factory.annotation.Qualifier("marketDataService")
    private org.softcaster.easy_pricer_mds_core.MarketDataService marketDataService;

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
        return new ResponseEntity<>("LifeCycle Service Alive", HttpStatus.OK);
    }

    /**
     * Inizio della chiusura di giornata (EodBatchRunner, a ogni lancio): data
     * ufficiale, prezzi e curve di chiusura in memoria, una volta sola. Gli step
     * li usano senza ricaricarli. 500 con il motivo se una curva non si costruisce.
     */
    @PostMapping("/internal/system/market_data")
    public ResponseEntity<String> marketData() {
        try {
            marketDataService.refreshOfficialDate();
            marketDataService.loadSpotPrice();
            marketDataService.reloadCurves();
            log.info("Market data loaded");
            return new ResponseEntity<>("Market data loaded", HttpStatus.OK);
        } catch (Exception e) {
            log.error("### Market data load failed", e);
            return new ResponseEntity<>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/internal/system/settlement")
    public ResponseEntity<EodTaskResult> settlement() {
        return runTask(EodStep.SETTLEMENT, lcJob::runSettlementLyfeCycle);
    }

    @PostMapping("/internal/system/coupon")
    public ResponseEntity<EodTaskResult> coupon() {
        return runTask(EodStep.COUPON, lcJob::runCouponLyfeCycle);
    }

    @PostMapping("/internal/system/accrual")
    public ResponseEntity<EodTaskResult> accrual() {
        return runTask(EodStep.ACCRUAL, lcJob::runAccrualLyfeCycle);
    }

    @PostMapping("/internal/system/fixing")
    public ResponseEntity<EodTaskResult> fixing() {
        // tassi variabili e fixing degli accumulator
        return runTask(EodStep.FIXING, () -> {
            EodTaskResult rates = rateFixingService.fixRates();
            if (rates.status() == EodStatus.FAILED && rates.errors() == 0) {
                return rates; // fuori dalla chiusura
            }
            EodTaskResult accumulators = accumulatorService.fixAccumulators();
            return EodTaskResult.of(EodStep.FIXING, rates.businessDate(),
                    rates.processed() + accumulators.processed(), rates.created() + accumulators.created(),
                    rates.skipped() + accumulators.skipped(), rates.errors() + accumulators.errors(),
                    String.join(" ", rates.message() == null ? "" : rates.message(),
                            accumulators.message() == null ? "" : accumulators.message()).trim());
        });
    }

    @PostMapping("/internal/system/maturity")
    public ResponseEntity<EodTaskResult> maturity() {
        return runTask(EodStep.MATURITY, maturityService::processMaturities);
    }

    @PostMapping("/internal/system/fx_rolling")
    public ResponseEntity<EodTaskResult> fxRolling() {
        return runTask(EodStep.FX_ROLLING, fxRollingService::rollPositions);
    }

    private ResponseEntity<EodTaskResult> runTask(EodStep step, Supplier<EodTaskResult> task) {
        EodTaskResult result;
        try {
            result = task.get();
        } catch (Exception e) {
            log.error("### EOD task {} failed", step, e);
            result = EodTaskResult.failed(step, null, e.getLocalizedMessage());
        }
        log.info("=== EOD task {} ===", result);
        HttpStatus status = result.status() == EodStatus.FAILED ? HttpStatus.INTERNAL_SERVER_ERROR : HttpStatus.OK;
        return new ResponseEntity<>(result, status);
    }
}
