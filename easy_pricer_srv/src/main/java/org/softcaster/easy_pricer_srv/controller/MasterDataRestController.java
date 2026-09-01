package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.dto.MasterDataDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MasterDataRestController {

    @Autowired
    private MasterDataDAO dao;

    @GetMapping("/master_data/r01")
    public ResponseEntity findAll() {
        List<MasterData> listaMasterData = dao.findAll();
        if (listaMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaMasterData, HttpStatus.OK);
    }

    @GetMapping("/master_data/r01/{assetClass}")
    public ResponseEntity<List<MasterDataDto>> findAllByAssetClass(@PathVariable String assetClass) {
        List<MasterDataDto> dtoList = dao.findAllDtoByAssetClass(assetClass);
        if (dtoList == null || dtoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/master_data/r02/{id}")
    public ResponseEntity findByIdMasterData(@PathVariable("id") Integer idMasterData) {
        MasterData masterData = dao.findByIdMasterData(idMasterData);
        if (masterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(masterData, HttpStatus.OK);
    }

}
