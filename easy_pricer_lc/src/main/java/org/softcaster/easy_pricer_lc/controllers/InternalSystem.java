/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_lc.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.easy_pricer_lc.jobs.LifeCycleJob;
import org.softcaster.easy_pricer_lc.services.EngineStateManager;
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

    @GetMapping("/internal/system/settlement")
    public ResponseEntity<String> settlement() {
        try {
            String result = lcJob.runSettlementLyfeCycleOnLine();
            if(result.isBlank()) {
                result = "Process SettlementLyfeCycle ends successfully!";
            }
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/internal/system/accrual")
    public ResponseEntity<String> accrual() {
        try {
            String result = lcJob.runAccrualLyfeCycleOnLine();
            if(result.isBlank()) {
                result = "Process AccrualLyfeCycle ends successfully!";
            }
            return new ResponseEntity(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(e.getLocalizedMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
