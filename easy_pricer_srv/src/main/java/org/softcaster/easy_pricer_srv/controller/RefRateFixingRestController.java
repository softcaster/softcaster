package org.softcaster.easy_pricer_srv.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.softcaster.core.data.RefRateFixing;
import org.softcaster.core.data.RefRateIndex;
import org.softcaster.core.data.RefRateIndexDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Fixing degli indici dei tassi variabili (ref_rate_fixing), letti dallo step
 * FIXING della chiusura: uno per indice e data, un nuovo caricamento della
 * stessa data sostituisce il tasso.
 *
 * POST /ref_rate_fixing/c01 con [{"index":"EURIBOR","tenor":"6M","fixingDate":"2026-11-06","rate":0.02134}]:
 * tasso in decimale (come spread, floor e cap); tenor necessario solo se il
 * codice dell'indice ha piu' durate.
 */
@RestController
public class RefRateFixingRestController {

    @Autowired
    private RefRateIndexDAO refRateIndexDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;

    public static class FixingRequest {

        public String index;
        public String tenor;
        public LocalDate fixingDate;
        public Double rate;
    }

    @PostMapping("/ref_rate_fixing/c01")
    public ResponseEntity<Map<String, Object>> save(@RequestBody List<FixingRequest> fixings) {
        int saved = transactionTemplate.execute(status -> {
            int count = 0;
            for (FixingRequest f : fixings) {
                if (f.index == null || f.fixingDate == null || f.rate == null) {
                    throw new IllegalArgumentException("index, fixingDate and rate are required");
                }
                RefRateIndex index = findIndex(f.index, f.tenor);
                RefRateFixing fixing = index.getFixings().stream()
                        .filter(x -> x.getFixingDate().toLocalDate().equals(f.fixingDate))
                        .findFirst()
                        .orElseGet(() -> {
                            RefRateFixing x = new RefRateFixing();
                            x.setFixingDate(java.sql.Date.valueOf(f.fixingDate));
                            index.getFixings().add(x);
                            return x;
                        });
                fixing.setRate(f.rate);
                refRateIndexDAO.saveOrUpdate(index);
                count++;
            }
            return count;
        });
        return new ResponseEntity<>(Map.of("saved", saved), HttpStatus.OK);
    }

    private RefRateIndex findIndex(String code, String tenor) {
        List<RefRateIndex> matches = refRateIndexDAO.findAll().stream()
                .filter(i -> i.getCode().equalsIgnoreCase(code))
                .filter(i -> tenor == null || tenor.isBlank() || i.getTenor().getCode().equalsIgnoreCase(tenor))
                .toList();
        if (matches.isEmpty()) {
            throw new NoSuchElementException("Index not found: " + code + (tenor == null ? "" : " " + tenor));
        }
        if (matches.size() > 1) {
            throw new IllegalArgumentException("Index " + code + " has several tenors: give the tenor");
        }
        return matches.get(0);
    }

    @ExceptionHandler({IllegalArgumentException.class, NoSuchElementException.class})
    public ResponseEntity<Map<String, Object>> badRequest(RuntimeException e) {
        return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
