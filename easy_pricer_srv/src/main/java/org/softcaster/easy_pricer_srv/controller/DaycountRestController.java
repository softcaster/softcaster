package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.Daycount;
import org.softcaster.core.data.DaycountDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DaycountRestController {

    @Autowired
    private DaycountDAO dao;

    @GetMapping("/daycount/r01")
    public ResponseEntity<List<Daycount>> findAll() {
        List<Daycount> listaDaycount = dao.findAll();
        if (listaDaycount == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaDaycount, HttpStatus.OK);
    }

    @GetMapping("/daycount/r02/{id}")
    public ResponseEntity<Daycount> findByIdDaycount(@PathVariable("id") Integer idDaycount) {
        Daycount daycount = dao.findByIdDaycount(idDaycount);
        if (daycount == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(daycount, HttpStatus.OK);
    }

    @GetMapping("/daycount/r03/{id}")
    public ResponseEntity<Daycount> findByCode(@PathVariable("id") String code) {
        Daycount daycount = dao.findByCode(code);
        if (daycount == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(daycount, HttpStatus.OK);
    }
}
