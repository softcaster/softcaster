package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.RefRateIndex;
import org.softcaster.core.data.RefRateIndexDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RefRateIndexRestController {

    @Autowired
    private RefRateIndexDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/ref_rate_index/r01")
    public ResponseEntity findAll() {
        List<RefRateIndex> listaRefRateIndex = dao.findAll();
        if (listaRefRateIndex == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaRefRateIndex, HttpStatus.OK);
    }

    @GetMapping("/ref_rate_index/r02/{id}")
    public ResponseEntity<RefRateIndex> findByRefRateIndexId(@PathVariable("id") Integer refRateIndexId) {
        RefRateIndex refRateIndex = dao.findByRefRateIndexId(refRateIndexId);
        if (refRateIndex == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(refRateIndex, HttpStatus.OK);
    }

}
