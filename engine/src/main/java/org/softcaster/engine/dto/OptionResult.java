/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 * Risultato comune a tutti i pricer di opzioni.
 *
 * @param price          prezzo unitario (per 1 unita' di sottostante)
 * @param positionValue  price * notional del contratto
 * @param forward        prezzo a termine del sottostante alla scadenza usato dal modello
 * @param discountFactor fattore di sconto tra valuationDate e scadenza usato per il premio
 * @param timeToExpiry   tempo alla scadenza in anni (ACT/365)
 * @param greeks         sensitivita', vedi Greeks per le unita' di misura
 */
public record OptionResult(
        String instrumentId,
        LocalDate valuationDate,
        double price,
        double positionValue,
        double forward,
        double discountFactor,
        double timeToExpiry,
        Greeks greeks) implements PricingResult {
}