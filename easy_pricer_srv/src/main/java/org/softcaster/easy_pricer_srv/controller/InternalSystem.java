/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.easy_pricer_srv.services.EngineStateManager;
import org.softcaster.easy_pricer_srv.services.SystemDateManager;
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
    SystemDateManager systemDateManager;
    
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
    
    @GetMapping(value = "/internal/system/official_date")
    public ResponseEntity<String> getOfficialDate() {
        String officialDate = systemDateManager.getOfficialBusinessDate().toString();
        return new ResponseEntity<>(officialDate, HttpStatus.OK);
    }
}
