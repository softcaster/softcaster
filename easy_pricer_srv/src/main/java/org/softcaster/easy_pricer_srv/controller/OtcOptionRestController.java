package org.softcaster.easy_pricer_srv.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.easy_pricer_srv.dto.OtcOptionDealRequest;
import org.softcaster.easy_pricer_srv.dto.OtcOptionDealResponse;
import org.softcaster.easy_pricer_srv.dto.OtcOptionDto;
import org.softcaster.easy_pricer_srv.services.OtcOptionDealService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Opzioni OTC (OFX su cambio, OCM su commodity): pricing dei termini,
 * registrazione del deal (anagrafica + transazione), lista, dettaglio e
 * valutazione di un contratto registrato.
 */
@RestController
public class OtcOptionRestController {

    @Autowired
    private OtcOptionDealService service;

    @PostMapping(value = "/otc/option/price", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PricingResponse> price(@RequestBody OtcOptionDealRequest request) {
        return ResponseEntity.ok(service.price(request));
    }

    @PostMapping(value = "/otc/option/deal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtcOptionDealResponse> book(@RequestBody OtcOptionDealRequest request) {
        return ResponseEntity.ok(service.book(request));
    }

    /**
     * Modifica di un deal registrato (anagrafica e transazione): PENDING
     * aggiornata, EXECUTED -> TO_AMEND.
     */
    @PutMapping(value = "/otc/option/deal/{txnId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtcOptionDealResponse> amend(@PathVariable("txnId") Integer txnId,
            @RequestBody OtcOptionDealRequest request) {
        return ResponseEntity.ok(service.amend(txnId, request));
    }

    @GetMapping("/otc/option/r01")
    public ResponseEntity<List<OtcOptionDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/otc/option/r02/{code}")
    public ResponseEntity<OtcOptionDto> findByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    /**
     * Valutazione di un contratto registrato: dalla richiesta si usano solo i
     * parametri di pricing (spot, volatility, optionPrice, curve).
     */
    @PostMapping(value = "/otc/option/value/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PricingResponse> value(@PathVariable("code") String code,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody OtcOptionDealRequest pricing) {
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
