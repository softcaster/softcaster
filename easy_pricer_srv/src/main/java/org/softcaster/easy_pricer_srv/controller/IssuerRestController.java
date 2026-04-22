package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.Issuer;
import org.softcaster.easy_pricer_core.data.IssuerDAO;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IssuerRestController {

    @Autowired
    private IssuerDAO dao;


    @GetMapping("/issuer/r0")
    public ResponseEntity findAll() {
        List<Issuer> listaIssuer = dao.findAll();
        if (listaIssuer == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaIssuer, HttpStatus.OK);
    }

    @GetMapping("/issuer/r1/{id}")
    public ResponseEntity findByIdIssuer(@PathVariable("id") Integer idIssuer) {
        Issuer issuer = dao.findByIdIssuer(idIssuer);
        if (issuer == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(issuer, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/issuer")
    public ResponseEntity saveOrUpdate(@RequestBody Issuer issuer) {
        try {
            if (issuer.getIdIssuer() == 0) {
                issuer.setIdIssuer(null);
            }

            issuer = dao.saveOrUpdate(issuer);
            return new ResponseEntity(issuer, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    // delete record
    @DeleteMapping("/issuer/d1/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idIssuer) {
        Issuer issuer = dao.findByIdIssuer(idIssuer);
        if (issuer != null) {
            dao.delete(issuer);
            return new ResponseEntity(issuer, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
