package org.softcaster.easy_pricer_srv.dto;

/**
 * Curva utilizzabile per lo sconto: yield_curve (kind YIELD) o spread_curve
 * (kind SPREAD, valuta della curva base). currency = codice ISO, null se la
 * curva non ne ha una.
 */
public record DiscountCurveDto(String code, String description, String kind, String currency) {
}
