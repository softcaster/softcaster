package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import java.util.NoSuchElementException;
import org.softcaster.easy_pricer_mds_core.dto.LoanPricingResponse;
import org.softcaster.easy_pricer_srv.dto.LoanCashFlowDto;
import org.softcaster.easy_pricer_srv.dto.LoanDealRequest;
import org.softcaster.easy_pricer_srv.dto.LoanDto;
import org.softcaster.easy_pricer_srv.services.LoanDealService;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mutui OTC: simulazione del piano e valutazione dei termini, registrazione
 * (anagrafica + spese + piano + transazione), lista, dettaglio e valutazione di
 * un mutuo registrato.
 */
@RestController
public class LoanRestController {

    @Autowired
    private LoanDealService service;

    @PostMapping(value = "/otc/loan/price", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoanPricingResponse> price(@RequestBody LoanDealRequest request) {
        return ResponseEntity.ok(service.price(request));
    }

    @PostMapping(value = "/otc/loan/deal", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoanPricingResponse> book(@RequestBody LoanDealRequest request) {
        return ResponseEntity.ok(service.book(request));
    }

    /**
     * Modifica di un deal registrato (anagrafica e transazione): PENDING
     * aggiornata, EXECUTED -> TO_AMEND.
     *
     * @param txnId
     * @param request
     * @return
     */
    @PutMapping(value = "/otc/loan/deal/{txnId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoanDto> amend(@PathVariable("txnId") Integer txnId, @RequestBody LoanDealRequest request) {
        return ResponseEntity.ok(service.amend(txnId, request));
    }

    @GetMapping("/otc/loan/r01")
    public ResponseEntity<List<LoanDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/otc/loan/r02/{code}")
    public ResponseEntity<LoanDto> findByCode(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.findByCode(code));
    }

    /**
     * Piano salvato (cash_flow_item) di un mutuo registrato.
     *
     * @param code
     * @return
     */
    @GetMapping("/otc/loan/cashflows/{code}")
    public ResponseEntity<List<LoanCashFlowDto>> cashFlows(@PathVariable("code") String code) {
        return ResponseEntity.ok(service.cashFlows(code));
    }

    /**
     * Valutazione di un mutuo registrato: dalla richiesta si usano solo
     * valuationDate, discountCurve, discountRate e creditSpread.
     *
     * @param code
     * @param pricing
     * @return
     */
    @PostMapping(value = "/otc/loan/value/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoanPricingResponse> value(@PathVariable("code") String code, @RequestBody LoanDealRequest pricing) {
        return ResponseEntity.ok(service.value(code, pricing));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(NoSuchElementException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(message(ex)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, ArithmeticException.class})
    public ResponseEntity<String> badRequest(RuntimeException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(message(ex)), HttpStatus.BAD_REQUEST);
    }

    private static String message(Throwable e) {
        while (e.getCause() != null && e.getCause() != e) {
            e = e.getCause();
        }
        // il messaggio finisce in una stringa JSON costruita a mano
        return String.valueOf(e.getMessage()).replace('"', '\'');
    }
}
