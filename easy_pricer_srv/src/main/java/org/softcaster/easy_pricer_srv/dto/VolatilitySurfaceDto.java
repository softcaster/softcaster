package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Superficie di volatilita' per il client e per il caricamento dei punti.
 *
 * In caricamento (POST /volatility/{code}): underlyingCode e description
 * servono solo se la superficie non esiste ancora; i punti sostituiscono
 * quelli della stessa referenceDate. strike null = at the money, volatility
 * annua in decimale (0.08 = 8%).
 */
public record VolatilitySurfaceDto(
        String code,
        String description,
        String underlyingCode,
        LocalDate referenceDate,
        List<Point> points) {

    public record Point(LocalDate expiryDate, Double strike, Double volatility, String source) {
    }
}
