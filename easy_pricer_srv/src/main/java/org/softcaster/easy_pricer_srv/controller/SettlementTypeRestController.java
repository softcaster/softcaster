package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.SettlementType;
import org.softcaster.easy_pricer_core.data.SettlementTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettlementTypeRestController {

    @Autowired
    private SettlementTypeDAO dao;

    @GetMapping("/settlement_type/r0")
    public ResponseEntity findAll() {
        List<SettlementType> listaSettlementType = dao.findAll();
        if (listaSettlementType == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaSettlementType, HttpStatus.OK);
    }

    @GetMapping("/settlement_type/r1/{id}")
    public ResponseEntity findByIdSettlementType(@PathVariable("id") Integer idSettlementType) {
        SettlementType settlementType = dao.findByIdSettlementType(idSettlementType);
        if (settlementType == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(settlementType, HttpStatus.OK);
    }

}
