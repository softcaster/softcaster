package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.Frequency;
import org.softcaster.easy_pricer_core.data.FrequencyDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FrequencyRestController {

    @Autowired
    private FrequencyDAO dao;

    @GetMapping("/frequency/r0")
    public ResponseEntity findAll() {
        List<Frequency> listaFrequency = dao.findAll();
        if (listaFrequency == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFrequency, HttpStatus.OK);
    }

    @GetMapping("/frequency/r1/{id}")
    public ResponseEntity findByIdFrequency(@PathVariable("id") Integer idFrequency) {
        Frequency frequency = dao.findByIdFrequency(idFrequency);
        if (frequency == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(frequency, HttpStatus.OK);
    }

    @GetMapping("/frequency/r2/{id}")
    public ResponseEntity findByCode(@PathVariable("id") String code) {
        Frequency frequency = dao.findByCode(code);
        if (frequency == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(frequency, HttpStatus.OK);
    }
}
