/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_proc.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InternalSystem {
    
    @PostMapping(value = "/internal/system/suspend")
    public ResponseEntity<String> suspend() {
        System.out.println("Service suspended...");
        return new ResponseEntity<>("Service suspended", HttpStatus.OK);
    }
}
