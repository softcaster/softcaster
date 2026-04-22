package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.easy_pricer_core.data.AmortizationSchedule;
import org.softcaster.easy_pricer_core.data.AmortizationScheduleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AmortizationScheduleRestController {

    @Autowired
    private AmortizationScheduleDAO dao;

    @GetMapping("/amortization_schedule/r0")
    public ResponseEntity<List<AmortizationSchedule>> findAll() {
        List<AmortizationSchedule> listaAmortizationSchedule = dao.findAll();
        if (listaAmortizationSchedule == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaAmortizationSchedule, HttpStatus.OK);
    }

    @GetMapping("/amortization_schedule/r1/{id}")
    public ResponseEntity<AmortizationSchedule> findByIdAmortizationSchedule(@PathVariable("id") Integer idAmortizationSchedule) {
        AmortizationSchedule amortizationSchedule = dao.findByIdAmortizationSchedule(idAmortizationSchedule);
        if (amortizationSchedule == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(amortizationSchedule, HttpStatus.OK);
    }

}
