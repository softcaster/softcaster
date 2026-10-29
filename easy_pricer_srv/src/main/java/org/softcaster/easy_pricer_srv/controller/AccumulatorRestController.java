package org.softcaster.easy_pricer_srv.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.softcaster.easy_pricer_mds_core.dto.AccumulatorPricingResponse;
import org.softcaster.easy_pricer_srv.dto.AccumulatorDealRequest;
import org.softcaster.easy_pricer_srv.dto.AccumulatorDealResponse;
import org.softcaster.easy_pricer_srv.dto.AccumulatorDto;
import org.softcaster.easy_pricer_srv.services.AccumulatorDealService;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Accumulator / decumulator OTC: pricing dei termini, registrazione del deal
 * (anagrafica + calendario + transazione), lista, dettaglio e valutazione di
 * un contratto registrato.
 */
@RestController
public class AccumulatorRestController {

    @Autowired
    private AccumulatorDealService service;

    @PostMapping(value = "/otc/accumulator/price", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccumulatorPricingResponse> price(@RequestBody AccumulatorDealRequest request) {
        return ResponseEntity.ok(service.price(request));
    }

    @PostMapping(value = "/otc/accumulator/deal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccumulatorDealResponse> book(@RequestBody AccumulatorDealRequest request) {
        return ResponseEntity.ok(service.book(request));
    }

    @GetMapping("/otc/accumulator/r01")
    public ResponseEntity<List<AccumulatorDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/otc/accumulator/r02/{code}")
    public ResponseEntity<AccumulatorDto> findByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    /**
     * Valutazione di un contratto registrato: dalla richiesta si usano solo i
     * parametri di pricing (volatility, spot, curve, paths).
     */
    @PostMapping(value = "/otc/accumulator/value/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccumulatorPricingResponse> value(@PathVariable("code") String code,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody AccumulatorDealRequest pricing) {
        return ResponseEntity.ok(service.value(code, date, pricing));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(NoSuchElementException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(rootMessage(ex)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> badRequest(RuntimeException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(rootMessage(ex)), HttpStatus.BAD_REQUEST);
    }

    private static String rootMessage(Throwable e) {
        while (e.getCause() != null && e.getCause() != e) {
            e = e.getCause();
        }
        // il messaggio finisce in una stringa JSON costruita a mano
        return String.valueOf(e.getMessage()).replace('"', '\'');
    }
}
