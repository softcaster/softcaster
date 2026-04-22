package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.RollConvention;
import org.softcaster.easy_pricer_core.data.RollConventionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RollConventionRestController {

    @Autowired
    private RollConventionDAO dao;

    @GetMapping("/roll_convention/r0")
    public ResponseEntity findAll() {
        List<RollConvention> listaRollConvention = dao.findAll();
        if (listaRollConvention == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaRollConvention, HttpStatus.OK);
    }

    @GetMapping("/roll_convention/r1/{id}")
    public ResponseEntity findByIdRollConvention(@PathVariable("id") Integer idRollConvention) {
        RollConvention rollConvention = dao.findByIdRollConvention(idRollConvention);
        if (rollConvention == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(rollConvention, HttpStatus.OK);
    }

    @GetMapping("/roll_convention/r2/{id}")
    public ResponseEntity findByCode(@PathVariable("id") String code) {
        RollConvention rollConvention = dao.findByCode(code);
        if (rollConvention == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(rollConvention, HttpStatus.OK);
    }
}
