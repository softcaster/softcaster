/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_srv.calc.BondCalculator;
import org.softcaster.easy_pricer_srv.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_srv.calc.FxForwardCalculator;
import org.softcaster.easy_pricer_srv.dto.BondPricingRequest;
import org.softcaster.easy_pricer_srv.dto.BondPricingResponse;
import org.softcaster.easy_pricer_srv.dto.PricingRequest;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.softcaster.engine.dto.BondOutputData;
import org.softcaster.engine.dto.MarketOutputData;
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
    @Autowired
    private BondForwardCalculator bondForwardCalculator;
    @Autowired
    private FxForwardCalculator fxForwardCalculator;
    
    //
    // Bond
    //
    @GetMapping("/helper/bond/quote/{isin}")
    public ResponseEntity getBondIrr(@PathVariable("isin") String isin) {
        if (bondCalculator != null) {
            BondPricingResponse output = bondCalculator.bondValuation(isin);
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
            List<BondOutputData> output = bondCalculator.bondsValuation();
            if (output != null) {
                return new ResponseEntity(output, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    //
    // Forward
    //
    @PostMapping("/helper/bondfwd/request")
    @SuppressWarnings("unchecked")
    public ResponseEntity bondFwdValuation(@RequestBody PricingRequest request) {
        if (bondForwardCalculator != null) {
            MarketOutputData output = bondForwardCalculator.bondFwdValuation(request);
            return new ResponseEntity(output, HttpStatus.OK);
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/helper/fxfwd/request")
    @SuppressWarnings("unchecked")
    public ResponseEntity forexFwdValuation(@RequestBody PricingRequest request) {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
/*
        if (fxForwardCalculator != null) {
            ForexFwdOutputData output = fxForwardCalculator.forexFwdValuation(request);
            return new ResponseEntity(output, HttpStatus.OK);
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
*/
    }

}
