package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.dto.PositionMasterDataDto;
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
public class PositionMasterDataRestController {

    @Autowired
    private PositionMasterDataDAO dao;

    @GetMapping("/position_master_data/r01")
    public ResponseEntity<List<PositionMasterData>> findAll() {
        List<PositionMasterData> listaPositionMasterData = dao.findAll();
        if (listaPositionMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaPositionMasterData, HttpStatus.OK);
    }

    @GetMapping("/position_master_data/r11")
    public ResponseEntity<List<PositionMasterDataDto>> findAllDto() {
        List<PositionMasterDataDto> dtoList = dao.findAllDto();
        if (dtoList == null || dtoList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/position_master_data/r02/{id}")
    public ResponseEntity<PositionMasterData> findByIdPosition(@PathVariable("id") Integer idPosition) {
        PositionMasterData positionMasterData = dao.findByIdPosition(idPosition);
        if (positionMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(positionMasterData, HttpStatus.OK);
    }

    @GetMapping("/position_master_data/r03/{id}")
    public ResponseEntity<PositionMasterData> findByCode(@PathVariable("id") String code) {
        PositionMasterData positionMasterData = dao.findByCode(code);
        if (positionMasterData == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(positionMasterData, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/position_master_data")
    public ResponseEntity<PositionMasterData> saveOrUpdate(@RequestBody PositionMasterData positionMasterData) {
        try {
            if (positionMasterData.getIdPosition() == 0) {
                positionMasterData.setIdPosition(null);
            }

            positionMasterData = dao.saveOrUpdate(positionMasterData);
            return new ResponseEntity(positionMasterData, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/position_master_data/d01/{id}")
    public ResponseEntity<PositionMasterData> delete(@PathVariable("id") Integer idPosition) {
        PositionMasterData positionMasterData = dao.findByIdPosition(idPosition);
        if (positionMasterData != null) {
            dao.delete(positionMasterData);
            return new ResponseEntity(positionMasterData, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
