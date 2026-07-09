package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.CmdFutureMasterData;
import org.softcaster.core.data.CmdFutureMasterDataDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CmdFutureMasterDataRestController {

    @Autowired
    private CmdFutureMasterDataDAO dao;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping("/cmd_future_master_data/r01")
    public ResponseEntity findAll() {
        List<CmdFutureMasterData> listaCmdFutureMasterData = dao.findAll();
        if (listaCmdFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCmdFutureMasterData, HttpStatus.OK);
    }

    @GetMapping("/cmd_future_master_data/r02/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        CmdFutureMasterData cmdFutureMasterData = dao.findByIdMasterData(idMasterData);
        if (cmdFutureMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(cmdFutureMasterData, HttpStatus.OK);
    }

}
