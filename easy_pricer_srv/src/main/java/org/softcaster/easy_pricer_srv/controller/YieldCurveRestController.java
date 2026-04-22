package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.YieldCurve;
import org.softcaster.easy_pricer_core.data.YieldCurveDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YieldCurveRestController {

    @Autowired
    private YieldCurveDAO dao;

    @GetMapping("/yield_curve/r0")
    public ResponseEntity findAll() {
        List<YieldCurve> listaYieldCurve = dao.findAll();
        if (listaYieldCurve == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaYieldCurve, HttpStatus.OK);
    }

    @GetMapping("/yield_curve/r1/{id}")
    public ResponseEntity findByIdYieldCurve(@PathVariable("id") Integer idYieldCurve) {
        YieldCurve yieldCurve = dao.findByIdYieldCurve(idYieldCurve);
        if (yieldCurve == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(yieldCurve, HttpStatus.OK);
    }

    @GetMapping("/yield_curve/r2/{id}")
    public ResponseEntity findByCode(@PathVariable("id") String code) {
        YieldCurve yieldCurve = dao.findByCode(code);
        if (yieldCurve == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(yieldCurve, HttpStatus.OK);
    }
}
