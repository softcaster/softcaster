/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 * Risultato del pricing di un FX forward / future.
 *
 * presentValue e' null quando l'input non contiene il contratto (calcolo del
 * solo cambio forward).
 */
public record FxForwardResult(
        String instrumentId,
        LocalDate valuationDate,
        double forwardRate,
        double forwardPoints, // forwardRate - spot
        double domesticDF, // DF domestico da spotDate a deliveryDate
        double foreignDF, // DF estero da spotDate a deliveryDate
        Double presentValue) // in valuta domestica, alla valuationDate
        implements ForwardResult {

}
