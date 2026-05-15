package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.CounterpartyType;
import org.softcaster.core.data.CounterpartyTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CounterpartyTypeRestController {

    @Autowired
    private CounterpartyTypeDAO dao;

    @GetMapping("/counterparty_type/r0")
    public ResponseEntity findAll() {
        List<CounterpartyType> listaCounterpartyType = dao.findAll();
        if (listaCounterpartyType == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCounterpartyType, HttpStatus.OK);
    }

    @GetMapping("/counterparty_type/r1/{id}")
    public ResponseEntity findByIdCounterpartyType(@PathVariable("id") Integer idCounterpartyType) {
        CounterpartyType counterpartyType = dao.findByIdCounterpartyType(idCounterpartyType);
        if (counterpartyType == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(counterpartyType, HttpStatus.OK);
    }

}
