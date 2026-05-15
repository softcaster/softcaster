package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.YieldCurveItem;
import org.softcaster.core.data.YieldCurveItemDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class YieldCurveItemRestController {

    @Autowired
    private YieldCurveItemDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/yield_curve_item/r0")
    public ResponseEntity findAll() {
        List<YieldCurveItem> listaYieldCurveItem = dao.findAll();
        if (listaYieldCurveItem == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaYieldCurveItem, HttpStatus.OK);
    }

    @GetMapping("/yield_curve_item/r1/{id}")
    public ResponseEntity findByIdYieldCurveItem(@PathVariable("id") Integer idYieldCurveItem) {
        YieldCurveItem yieldCurveItem = dao.findByIdYieldCurveItem(idYieldCurveItem);
        if (yieldCurveItem == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(yieldCurveItem, HttpStatus.OK);
    }

}
