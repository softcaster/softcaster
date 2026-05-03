/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_srv.calc.BondCalculator;
import org.softcaster.easy_pricer_srv.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_srv.calc.FxForwardCalculator;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ph.alephzero.finance.BondFwdPriceRequest;
import ph.alephzero.finance.BondPriceRequest;
import ph.alephzero.finance.ForexFwdPriceRequest;
import ph.alephzero.finance.products.fixedincome.BondCalcOutputData;
import ph.alephzero.finance.products.forward.BondForwardOutputData;
import ph.alephzero.finance.products.forward.ForexFwdOutputData;

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
            BondCalcOutputData output = bondCalculator.bondValuation(isin);
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
    public ResponseEntity getBondData(@RequestBody BondPriceRequest request) {
        if (bondCalculator != null) {
            BondCalcOutputData output = bondCalculator.bondValuation(request);
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
            List<BondCalcOutputData> output = bondCalculator.bondsValuation();
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
    public ResponseEntity bondFwdValuation(@RequestBody BondFwdPriceRequest request) {
        if (bondForwardCalculator != null) {
            BondForwardOutputData output = bondForwardCalculator.bondFwdValuation(request);
            return new ResponseEntity(output, HttpStatus.OK);
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/helper/fxfwd/request")
    @SuppressWarnings("unchecked")
    public ResponseEntity forexFwdValuation(@RequestBody ForexFwdPriceRequest request) {
        if (fxForwardCalculator != null) {
            ForexFwdOutputData output = fxForwardCalculator.forexFwdValuation(request);
            return new ResponseEntity(output, HttpStatus.OK);
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
