package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.SuperClass;
import org.softcaster.easy_pricer_core.data.SuperClassDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SuperClassRestController {

    @Autowired
    private SuperClassDAO dao;

    @Autowired

    @GetMapping("/super_class/r0")
    public ResponseEntity findAll() {
        List<SuperClass> listaSuperClass = dao.findAll();
        if (listaSuperClass == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaSuperClass, HttpStatus.OK);
    }

    @GetMapping("/super_class/r1/{id}")
    public ResponseEntity findByIdSuperClass(@PathVariable("id") Integer idSuperClass) {
        SuperClass superClass = dao.findByIdSuperClass(idSuperClass);
        if (superClass == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(superClass, HttpStatus.OK);
    }

}
