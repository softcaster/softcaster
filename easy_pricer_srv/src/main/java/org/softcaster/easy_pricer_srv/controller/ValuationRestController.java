/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import org.softcaster.easy_pricer_srv.dto.BondPricingRequest;
import org.softcaster.easy_pricer_srv.dto.BondPricingResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author softc
 */
@RestController
public class ValuationRestController {
    
    @PostMapping(value = "/pricing/bond" , produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<BondPricingResponse> calculateBondPricing(@RequestBody BondPricingRequest request) {
        
        BondPricingResponse response = new BondPricingResponse();
        response.accruedInterest = 0.5;
        response.modifiedDuration = 1.5;
        response.yieldToMaturity = 0.04;
        
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
