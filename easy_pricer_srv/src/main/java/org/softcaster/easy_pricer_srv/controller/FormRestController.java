package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.Form;
import org.softcaster.easy_pricer_core.data.FormDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FormRestController {

    @Autowired
    private FormDAO dao;

    @GetMapping("/form/r0")
    public ResponseEntity findAll() {
        List<Form> listaForm = dao.findAll();
        if (listaForm == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaForm, HttpStatus.OK);
    }

    @GetMapping("/form/r1/{id}")
    public ResponseEntity findByIdForm(@PathVariable("id") Integer idForm) {
        Form form = dao.findByIdForm(idForm);
        if (form == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(form, HttpStatus.OK);
    }

    @GetMapping("/form/r2/{id}")
    public ResponseEntity findByCode(@PathVariable("id") String code) {
        Form form = dao.findByCode(code);
        if (form == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(form, HttpStatus.OK);
    }
}
