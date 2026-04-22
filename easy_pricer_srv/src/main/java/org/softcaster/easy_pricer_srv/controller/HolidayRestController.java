package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.Holiday;
import org.softcaster.easy_pricer_core.data.HolidayDAO;
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
public class HolidayRestController {

    @Autowired
    private HolidayDAO dao;

    @GetMapping("/holiday/r0")
    public ResponseEntity findAll() {
        List<Holiday> listaHoliday = dao.findAll();
        if (listaHoliday == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaHoliday, HttpStatus.OK);
    }

    @GetMapping("/holiday/r1/{id}")
    public ResponseEntity findByIdHoliday(@PathVariable("id") Integer idHoliday) {
        Holiday holiday = dao.findByIdHoliday(idHoliday);
        if (holiday == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(holiday, HttpStatus.OK);
    }

    // save/update record
    @PostMapping(value = "/holiday")
    public ResponseEntity saveOrUpdate(@RequestBody Holiday holiday) {
        try {
            if (holiday.getIdHoliday() == 0) {
                holiday.setIdHoliday(null);
            }

            holiday = dao.saveOrUpdate(holiday);
            return new ResponseEntity(holiday, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/holiday/d1/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idHoliday) {
        Holiday holiday = dao.findByIdHoliday(idHoliday);
        if (holiday != null) {
            dao.delete(holiday);
            return new ResponseEntity(holiday, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
