package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.MasterData;
import org.softcaster.easy_pricer_core.data.MasterDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MasterDataRestController {

    @Autowired
    private MasterDataDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/master_data/r0")
    public ResponseEntity findAll() {
        List<MasterData> listaMasterData = dao.findAll();
        if (listaMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaMasterData, HttpStatus.OK);
    }

    @GetMapping("/master_data/r1/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        MasterData masterData = dao.findByIdMasterData(idMasterData);
        if (masterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(masterData, HttpStatus.OK);
    }

}
