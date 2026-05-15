package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FxFutureMasterDataRestController {

    @Autowired
    private FxFutureMasterDataDAO dao;

    @GetMapping("/fx_future_master_data/r01")
    public ResponseEntity findAll() {
        List<FxFutureMasterData> listaFxFutureMasterData = dao.findAll();
        if (listaFxFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaFxFutureMasterData, HttpStatus.OK);
    }

    @GetMapping("/fx_future_master_data/r02/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        FxFutureMasterData fxFutureMasterData = dao.findByIdMasterData(idMasterData);
        if (fxFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(fxFutureMasterData, HttpStatus.OK);
    }

}
