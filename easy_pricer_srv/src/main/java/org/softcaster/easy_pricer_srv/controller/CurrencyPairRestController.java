package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.CurrencyPair;
import org.softcaster.easy_pricer_core.data.CurrencyPairDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyPairRestController {

    @Autowired
    private CurrencyPairDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/currency_pair/r0")
    public ResponseEntity findAll() {
        List<CurrencyPair> listaCurrencyPair = dao.findAll();
        if (listaCurrencyPair == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCurrencyPair, HttpStatus.OK);
    }

    @GetMapping("/currency_pair/r1/{id}")
    public ResponseEntity findByIdCurrencyPair(@PathVariable("id") Integer idCurrencyPair) {
        CurrencyPair currencyPair = dao.findByIdCurrencyPair(idCurrencyPair);
        if (currencyPair == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(currencyPair, HttpStatus.OK);
    }

}
