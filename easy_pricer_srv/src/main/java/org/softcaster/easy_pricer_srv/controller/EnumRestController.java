package org.softcaster.easy_pricer_srv.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.softcaster.easy_pricer_srv.dto.EnumDto;
import org.softcaster.easy_pricer_srv.services.EnumService;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Voci degli enum per i client (codice ed etichetta): /enums?names=a,b per
 * piu' enum in una chiamata, /enums/{name} per uno solo, /enums/names per
 * l'elenco dei nomi disponibili.
 */
@RestController
public class EnumRestController {

    @Autowired
    private EnumService service;

    @GetMapping("/enums")
    public ResponseEntity<Map<String, List<EnumDto>>> find(@RequestParam("names") List<String> names) {
        return ResponseEntity.ok(service.find(names));
    }

    @GetMapping("/enums/names")
    public ResponseEntity<List<String>> names() {
        return ResponseEntity.ok(service.names());
    }

    @GetMapping("/enums/{name}")
    public ResponseEntity<List<EnumDto>> find(@PathVariable("name") String name) {
        return ResponseEntity.ok(service.find(name));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(NoSuchElementException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(String.valueOf(ex.getMessage()).replace('"', '\'')), HttpStatus.NOT_FOUND);
    }
}
