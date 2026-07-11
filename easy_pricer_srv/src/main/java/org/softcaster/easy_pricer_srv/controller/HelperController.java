/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingResponse;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.softcaster.engine.dto.XRBOutputData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ep
 */
@RestController
public class HelperController {

    @Autowired
    private BondCalculator bondCalculator;
    
    //
    // Bond
    //
    @GetMapping("/helper/bond/quote/{isin}")
    public ResponseEntity getBondIrr(@PathVariable("isin") String isin) {
        if (bondCalculator != null) {
            BondPricingResponse output = null; //bondCalculator.bondValuation(isin);
            if (output != null) {
                return new ResponseEntity(output, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "helper/bond/request")
    @SuppressWarnings("unchecked")
    public ResponseEntity getBondData(@RequestBody BondPricingRequest request) {
        if (bondCalculator != null) {
            BondPricingResponse output = bondCalculator.bondValuation(request);
            if (output != null) {
                return new ResponseEntity(output, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping(value = "helper/bond/requests")
    @SuppressWarnings("unchecked")
    public ResponseEntity getBondsData() {
        if (bondCalculator != null) {
            List<XRBOutputData> output = null;//bondCalculator.bondsValuation();
            if (output != null) {
                return new ResponseEntity(output, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
