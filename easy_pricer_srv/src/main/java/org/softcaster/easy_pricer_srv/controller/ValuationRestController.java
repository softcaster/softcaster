/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingResponse;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingResponse;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private BondCalculator bondCalculator;
    @Autowired
    private BondForwardCalculator bondForwardCalculator;
    
    @PostMapping(value = "/pricing/bond" , produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<BondPricingResponse> calculateBondPricing(@RequestBody BondPricingRequest request) {
       
        if (bondCalculator != null) {
            BondPricingResponse response = bondCalculator.bondValuation(request);
            if (response != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    @PostMapping(value = "/pricing/future" , produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<ForwardPricingResponse> calculateFwdBondPricing(@RequestBody ForwardPricingRequest request) {
       
        if (bondForwardCalculator != null) {
            ForwardPricingResponse response = bondForwardCalculator.bondFwdValuation(request);
            if (response != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }    
    
    @PostMapping(value = "/pricing/fx-future" , produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<ForwardPricingResponse> calculateFwdFxPricing(@RequestBody ForwardPricingRequest request) {
       
        ForwardPricingResponse response = new ForwardPricingResponse();
        response.theoreticalPrice = request.referencePrice;
        return new ResponseEntity(response, HttpStatus.OK);
    
    }
}
