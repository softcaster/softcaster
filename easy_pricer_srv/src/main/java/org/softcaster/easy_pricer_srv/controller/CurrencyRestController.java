package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.Currency;
import org.softcaster.easy_pricer_core.data.CurrencyDAO;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyRestController {

    @Autowired
    private CurrencyDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/currency/r0")
    public ResponseEntity findAll() {
        List<Currency> listaCurrency = dao.findAll();
        if (listaCurrency == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCurrency, HttpStatus.OK);
    }

    @GetMapping("/currency/r1/{id}")
    public ResponseEntity findByIdCurrency(@PathVariable("id") Integer idCurrency) {
        Currency currency = dao.findByIdCurrency(idCurrency);
        if (currency == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(currency, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/currency")
    public ResponseEntity saveOrUpdate(@RequestBody Currency currency) {
        try {
            if (currency.getIdCurrency() == 0) {
                currency.setIdCurrency(null);
            }

            currency = dao.saveOrUpdate(currency);
            return new ResponseEntity(currency, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    // delete record
    @DeleteMapping("/currency/d1/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idCurrency) {
        Currency currency = dao.findByIdCurrency(idCurrency);
        if (currency != null) {
            dao.delete(currency);
            return new ResponseEntity(currency, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
