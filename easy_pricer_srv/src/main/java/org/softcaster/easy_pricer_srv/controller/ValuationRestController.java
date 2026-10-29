/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import java.util.NoSuchElementException;
import org.softcaster.easy_pricer_mds_core.calc.CashFlowInstrumentCalculator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    private CashFlowInstrumentCalculator bondCalculator;
    @Autowired
    private BondForwardCalculator bondForwardCalculator;
    @Autowired
    private FxForwardCalculator fxForwardCalculator;
    @Autowired
    private OptionCalculator optionCalculator;

    @PostMapping(value = "/pricing/bond", produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<PricingResponse> calculateBondPricing(@RequestBody PricingRequest request) {

        if (bondCalculator != null) {
            PricingResponse response = bondCalculator.bondValuation(request);
            if (response != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "/pricing/future", produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<PricingResponse> calculateFwdBondPricing(@RequestBody PricingRequest request) {

        if (bondForwardCalculator != null) {
            PricingResponse response = bondForwardCalculator.bondFwdValuation(request);
            if (response != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "/pricing/fx-future", produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<PricingResponse> calculateFxFuturePricing(@RequestBody PricingRequest request) {

        if (fxForwardCalculator != null) {
            PricingResponse response = fxForwardCalculator.fxFwdValuation(request);
            if (response != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Opzioni di tutti i rami (su future, su cambio, su titolo): il modello lo
     * sceglie la serie.
     */
    @PostMapping(value = "/pricing/option", produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<PricingResponse> calculateOptionPricing(@RequestBody PricingRequest request) {

        if (optionCalculator != null) {
            PricingResponse response = optionCalculator.optionValuation(request);
            if (response != null) {
                return new ResponseEntity(response, HttpStatus.OK);
            } else {
                return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity(CommonData.getJsonError("null value"), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // errori dei calcolatori: strumento o curva non trovati -> 404, dati non validi -> 400
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(NoSuchElementException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(message(ex)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(message(ex)), HttpStatus.BAD_REQUEST);
    }

    private static String message(Throwable e) {
        // il messaggio finisce in una stringa JSON costruita a mano
        return String.valueOf(e.getMessage()).replace('"', '\'');
    }
}
