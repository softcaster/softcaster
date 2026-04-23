package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.ForexMasterData;
import org.softcaster.easy_pricer_core.data.ForexMasterDataDAO;
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
public class ForexMasterDataRestController {

    @Autowired
    private ForexMasterDataDAO dao;

    @GetMapping("/forex_master_data/r01")
    public ResponseEntity<List<ForexMasterData>> findAll() {
        List<ForexMasterData> listaForexMasterData = dao.findAll();
        if (listaForexMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaForexMasterData, HttpStatus.OK);
    }

    @GetMapping("/forex_master_data/r02/{id}")
    public ResponseEntity<ForexMasterData> findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        ForexMasterData forexMasterData = dao.findByIdMasterData(idMasterData);
        if (forexMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(forexMasterData, HttpStatus.OK);
    }

    @GetMapping("/forex_master_data/r03/{id}")
    public ResponseEntity<ForexMasterData> findByCode(@PathVariable("id") String code) {
        ForexMasterData forexMasterData = dao.findByCode(code);
        if (forexMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(forexMasterData, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/forex_master_data")
    public ResponseEntity<ForexMasterData> saveOrUpdate(@RequestBody ForexMasterData forexMasterData) {
        try {
            if (forexMasterData.getIdMasterData() == 0) {
                forexMasterData.setIdMasterData(null);
            }

            forexMasterData = dao.saveOrUpdate(forexMasterData);
            return new ResponseEntity(forexMasterData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/forex_master_data/d01/{id}")
    public ResponseEntity<ForexMasterData> delete(@PathVariable("id") Integer idMasterData) {
        ForexMasterData forexMasterData = dao.findByIdMasterData(idMasterData);
        if (forexMasterData != null) {
            dao.delete(forexMasterData);
            return new ResponseEntity(forexMasterData, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
