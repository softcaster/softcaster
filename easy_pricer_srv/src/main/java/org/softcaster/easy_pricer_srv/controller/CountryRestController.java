package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import org.softcaster.core.data.Country;
import org.softcaster.core.data.CountryDAO;
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
public class CountryRestController {

    @Autowired
    private CountryDAO dao;

    @GetMapping("/country/r0")
    public ResponseEntity findAll() {
        List<Country> listaCountry = dao.findAll();
        if (listaCountry == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(listaCountry, HttpStatus.OK);
    }

    @GetMapping("/country/r1/{id}")
    public ResponseEntity findByIdCountry(@PathVariable("id") Integer idCountry) {
        Country country = dao.findByIdCountry(idCountry);
        if (country == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(country, HttpStatus.OK);
    }

    @GetMapping("/country/r2/{id}")
    public ResponseEntity findByAlfa3Code(@PathVariable("id") String alfa3Code) {
        Country country = dao.findByAlfa3Code(alfa3Code );
        if (country == null) {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(country, HttpStatus.OK);
    }
    
    // save/update record
    @PostMapping(value = "/country")
    public ResponseEntity saveOrUpdate(@RequestBody Country country) {
        try {
            if (country.getIdCountry()== 0) {
                country.setIdCountry(null);
            }

            country = dao.saveOrUpdate(country);
            return new ResponseEntity(country, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(CommonData.getJsonError(e.getLocalizedMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }
    
    // delete record
    @DeleteMapping("/country/d1/{id}")
    public ResponseEntity delete(@PathVariable("id") Integer idCountry) {
        Country country = dao.findByIdCountry(idCountry);
        if (country != null) {
            dao.delete(country);
            return new ResponseEntity(country, HttpStatus.OK);
        } else {
            return new ResponseEntity(null, HttpStatus.NOT_FOUND);
        }
    }
}
