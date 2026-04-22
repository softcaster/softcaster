package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.FutureMasterData;
import org.softcaster.easy_pricer_core.data.FutureMasterDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FutureMasterDataRestController {

    @Autowired
    private FutureMasterDataDAO dao;

    @GetMapping("/future_master_data/r0")
    public ResponseEntity findAll() {
        List<FutureMasterData> listaFutureMasterData = dao.findAll();
        if (listaFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFutureMasterData, HttpStatus.OK);
    }

    @GetMapping("/future_master_data/r1/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        FutureMasterData futureMasterData = dao.findByIdMasterData(idMasterData);
        if (futureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(futureMasterData, HttpStatus.OK);
    }

}
