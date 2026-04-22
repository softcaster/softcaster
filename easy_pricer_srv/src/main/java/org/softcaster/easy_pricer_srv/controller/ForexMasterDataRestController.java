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

    @GetMapping("/forex_master_data/r0")
    public ResponseEntity findAll() {
        List<ForexMasterData> listaForexMasterData = dao.findAll();
        if (listaForexMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaForexMasterData, HttpStatus.OK);
    }

    @GetMapping("/forex_master_data/r1/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        ForexMasterData forexMasterData = dao.findByIdMasterData(idMasterData);
        if (forexMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(forexMasterData, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/forex_master_data")
    public ResponseEntity saveOrUpdate(@RequestBody ForexMasterData forexMasterData) {
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
    @DeleteMapping("/forex_master_data/d1/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idMasterData) {
        ForexMasterData forexMasterData = dao.findByIdMasterData(idMasterData);
        if (forexMasterData != null) {
            dao.delete(forexMasterData);
            return new ResponseEntity(forexMasterData, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }

}
