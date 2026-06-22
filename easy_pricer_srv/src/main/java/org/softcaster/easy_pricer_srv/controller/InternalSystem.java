/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import org.softcaster.easy_pricer_srv.services.SystemDateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalSystem {
    @Autowired
    SystemDateManager systemDateManager;
    
    @PostMapping(value = "/internal/system/suspend")
    public ResponseEntity<String> suspend() {
        System.out.println("Service suspended...");
        return new ResponseEntity<>("Service suspended", HttpStatus.OK);
    }
    
    @GetMapping(value = "/internal/system/official_date")
    public ResponseEntity<String> getOfficialDate() {
        String officialDate = systemDateManager.getOfficialBusinessDate().toString();
        System.out.println(officialDate);
        return new ResponseEntity<>(officialDate, HttpStatus.OK);
    }
}
