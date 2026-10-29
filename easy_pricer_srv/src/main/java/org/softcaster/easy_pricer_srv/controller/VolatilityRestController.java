package org.softcaster.easy_pricer_srv.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.softcaster.easy_pricer_srv.dto.VolatilitySurfaceDto;
import org.softcaster.easy_pricer_srv.services.VolatilityService;
import org.softcaster.easy_pricer_srv.util.CommonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Superfici di volatilita': elenco, punti a una data, caricamento dei punti
 * di una data di rilevazione, volatilita' interpolata (controllo).
 */
@RestController
public class VolatilityRestController {

    @Autowired
    private VolatilityService service;

    @GetMapping("/volatility/r01")
    public ResponseEntity<List<VolatilitySurfaceDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/volatility/r02/{code}")
    public ResponseEntity<VolatilitySurfaceDto> find(@PathVariable("code") String code,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(service.find(code, date));
    }

    @PostMapping("/volatility/{code}")
    public ResponseEntity<VolatilitySurfaceDto> upload(@PathVariable("code") String code,
            @RequestBody VolatilitySurfaceDto dto) {
        return ResponseEntity.ok(service.upload(code, dto));
    }

    @GetMapping("/volatility/{code}/value")
    public ResponseEntity<Map<String, Object>> volatility(@PathVariable("code") String code,
            @RequestParam("expiry") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiry,
            @RequestParam("strike") double strike,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(Map.of("code", code, "expiry", expiry, "strike", strike,
                "volatility", service.volatility(code, date, expiry, strike)));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> notFound(NoSuchElementException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(message(ex)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<String> badRequest(RuntimeException ex) {
        return new ResponseEntity<>(CommonData.getJsonError(message(ex)), HttpStatus.BAD_REQUEST);
    }

    private static String message(Throwable e) {
        while (e.getCause() != null && e.getCause() != e) {
            e = e.getCause();
        }
        // il messaggio finisce in una stringa JSON costruita a mano
        return String.valueOf(e.getMessage()).replace('"', '\'');
    }
}
