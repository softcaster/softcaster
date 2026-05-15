package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.Counterparty;
import org.softcaster.core.data.CounterpartyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounterpartyRestController {

    @Autowired
    private CounterpartyDAO dao;

    @GetMapping("/counterparty/r01")
    public ResponseEntity findAll() {
        List<Counterparty> listaCounterparty = dao.findAll();
        if (listaCounterparty == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCounterparty, HttpStatus.OK);
    }

    @GetMapping("/counterparty/r02/{id}")
    public ResponseEntity findByIdCounterparty(@PathVariable("id") Integer idCounterparty) {
        Counterparty counterparty = dao.findByIdCounterparty(idCounterparty);
        if (counterparty == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(counterparty, HttpStatus.OK);
    }

}
