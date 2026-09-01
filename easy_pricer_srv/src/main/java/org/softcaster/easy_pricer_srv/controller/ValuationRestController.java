/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.easy_pricer_srv.controller;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.YieldCurveDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.BondCalculator;
import org.softcaster.easy_pricer_mds_core.calc.BondForwardCalculator;
import org.softcaster.easy_pricer_mds_core.calc.FxFutureCalculator;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.BondPricingResponse;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.ForwardPricingResponse;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.dto.ForwardBaseInputData;
import org.softcaster.engine.dto.ForwardBaseOutputData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    private FxFutureCalculator fxFutureCalculator;
    @Autowired
    private FxFutureMasterDataDAO fxFutureMasterDataDAO;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    @PostMapping(value = "/pricing/bond", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/pricing/future", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/pricing/fx-future", produces = MediaType.APPLICATION_JSON_VALUE)
    @SuppressWarnings("unchecked")
    public ResponseEntity<ForwardPricingResponse> calculateFxFwdPricing(@RequestBody ForwardPricingRequest request) {

        ForwardPricingResponse response = new ForwardPricingResponse();
        if (request.foreignRCurve.isBlank() || request.domesticRCurve.isBlank()) {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        FxFutureMasterData fmd = fxFutureMasterDataDAO.findByIsin(request.isin);
        if (fmd == null) {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        YieldCurve yieldCurve = marketDataService.getYieldCurve(request.domesticRCurve);
        if (yieldCurve == null) {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        double domesticDF = yieldCurve.getDiscountFactor(fmd.getMaturityDate().toLocalDate());

        yieldCurve = marketDataService.getYieldCurve(request.foreignRCurve);
        if (yieldCurve == null) {
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        double foreignDF = yieldCurve.getDiscountFactor(fmd.getMaturityDate().toLocalDate());

        ForwardBaseInputData input = new ForwardBaseInputData();
        input.setCode(fmd.getCode());
        input.setDomesticDF(domesticDF);
        input.setForeignDF(foreignDF);
        input.setUnderlyingReferencePrice(request.referencePrice);
        input.setUseRates(false);

        ForwardBaseOutputData output = fxFutureCalculator.fxFwdValuation(input);
        response.theoreticalPrice = output.getPrice();

        return new ResponseEntity(response, HttpStatus.OK);

    }
}
