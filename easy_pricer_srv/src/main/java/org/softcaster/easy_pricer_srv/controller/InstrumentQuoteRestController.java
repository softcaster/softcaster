package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.InstrumentQuote;
import org.softcaster.core.data.InstrumentQuoteDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstrumentQuoteRestController {

    @Autowired
    private InstrumentQuoteDAO dao;

    @GetMapping("/instrument_quote/r0")
    public ResponseEntity findAll() {
        List<InstrumentQuote> listaInstrumentQuote = dao.findAll();
        if (listaInstrumentQuote == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaInstrumentQuote, HttpStatus.OK);
    }

    @GetMapping("/instrument_quote/r1/{id}")
    public ResponseEntity findByIdInstrumentQuote(@PathVariable("id") Integer idInstrumentQuote) {
        InstrumentQuote instrumentQuote = dao.findByIdInstrumentQuote(idInstrumentQuote);
        if (instrumentQuote == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(instrumentQuote, HttpStatus.OK);
    }

}
