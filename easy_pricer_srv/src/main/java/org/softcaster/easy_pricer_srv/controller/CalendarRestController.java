package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.Calendar;
import org.softcaster.core.data.CalendarDAO;
import org.softcaster.core.data.Holiday;
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
public class CalendarRestController {

    @Autowired
    private CalendarDAO dao;

    @GetMapping("/calendar/r0")
    public ResponseEntity findAll() {
        List<org.softcaster.core.data.Calendar> listaCalendar = dao.findAll();
        if (listaCalendar == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCalendar, HttpStatus.OK);
    }

    @GetMapping("/calendar/r1/{id}")
    public ResponseEntity findByIdCalendar(@PathVariable("id") Integer idCalendar) {
        org.softcaster.core.data.Calendar calendar = dao.findByIdCalendar(idCalendar);
        if (calendar == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(calendar, HttpStatus.OK);
    }

    @GetMapping("/calendar/r11/{id}")
    public ResponseEntity findHolidays(@PathVariable("id") Integer idCalendar) {
        List<Holiday> holidays = dao.findHolidays(idCalendar);
        if (holidays == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(holidays, HttpStatus.OK);
    }

    @GetMapping("/calendar/r2/{id}")
    public ResponseEntity findByCode(@PathVariable("id") String code) {
        org.softcaster.core.data.Calendar calendar = dao.findByCode(code);
        if (calendar == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(calendar, HttpStatus.OK);
    }
    
    // save/update record
    @PostMapping(value = "/calendar")
    public ResponseEntity saveOrUpdate(@RequestBody Calendar calendar) {
        try {
            if (calendar.getIdCalendar() == 0) {
                calendar.setIdCalendar(null);
            }

            calendar = dao.saveOrUpdate(calendar);
            return new ResponseEntity(calendar, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // delete record
    @DeleteMapping("/calendar/d1/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idCalendar) {
        Calendar calendar = dao.findByIdCalendar(idCalendar);
        if (calendar != null) {
            dao.delete(calendar);
            return new ResponseEntity(calendar, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
