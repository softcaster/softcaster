package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PositionDetailRestController {

    @Autowired
    private PositionDetailDAO dao;

    @GetMapping("/position_detail/r0")
    public ResponseEntity findAll() {
        List<PositionDetail> listaPositionDetail = dao.findAll();
        if (listaPositionDetail == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaPositionDetail, HttpStatus.OK);
    }

    @GetMapping("/position_detail/r1/{id}")
    public ResponseEntity findByIdPositionDetail(@PathVariable("id") Integer idPositionDetail) {
        PositionDetail positionDetail = dao.findByIdPositionDetail(idPositionDetail);
        if (positionDetail == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(positionDetail, HttpStatus.OK);
    }

}
