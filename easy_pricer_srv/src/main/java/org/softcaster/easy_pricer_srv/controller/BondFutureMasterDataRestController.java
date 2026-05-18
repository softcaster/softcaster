package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.BondFutureMasterData;
import org.softcaster.core.data.BondFutureMasterDataDAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BondFutureMasterDataRestController {

    @Autowired
    private BondFutureMasterDataDAO dao;

    @GetMapping("/bond_future_master_data/r01")
    public ResponseEntity findAll() {
        List<BondFutureMasterData> listaBondFutureMasterData = dao.findAll();
        if (listaBondFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaBondFutureMasterData, HttpStatus.OK);
    }

    @GetMapping("/bond_future_master_data/r02/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        BondFutureMasterData bondFutureMasterData = dao.findByIdMasterData(idMasterData);
        if (bondFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(bondFutureMasterData, HttpStatus.OK);
    }

}
