package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.TypeOfInterest;
import org.softcaster.easy_pricer_core.data.TypeOfInterestDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TypeOfInterestRestController {

    @Autowired
    private TypeOfInterestDAO dao;

    @GetMapping("/type_of_interest/r0")
    public ResponseEntity findAll() {
        List<TypeOfInterest> listaTypeOfInterest = dao.findAll();
        if (listaTypeOfInterest == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaTypeOfInterest, HttpStatus.OK);
    }

    @GetMapping("/type_of_interest/r1/{id}")
    public ResponseEntity findByIdTypeOfInterest(@PathVariable("id") Integer idTypeOfInterest) {
        TypeOfInterest typeOfInterest = dao.findByIdTypeOfInterest(idTypeOfInterest);
        if (typeOfInterest == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(typeOfInterest, HttpStatus.OK);
    }

    @GetMapping("/type_of_interest/r2/{id}")
    public ResponseEntity findByCode(@PathVariable("id") String code) {
        TypeOfInterest typeOfInterest = dao.findByCode(code);
        if (typeOfInterest == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(typeOfInterest, HttpStatus.OK);
    }
}
