package org.softcaster.easy_pricer_srv.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FutureMasterDataRestController {

    @GetMapping("/future_master_data/r01")
    public ResponseEntity findAll() {
        return new ResponseEntity(null, HttpStatus.NOT_FOUND);
    }

    @GetMapping("/future_master_data/r02/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        return new ResponseEntity(null, HttpStatus.NOT_FOUND);
    }
}
