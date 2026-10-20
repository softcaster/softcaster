/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 * Sensitivita' del prezzo unitario dell'opzione. Unita' di misura:
 *
 * @param delta variazione del prezzo per 1 unita' di sottostante (spot; forward
 * per Black-76)
 * @param gamma variazione del delta per 1 unita' di sottostante
 * @param vega variazione del prezzo per +1 punto di volatilita' (0.20 -> 0.21)
 * @param theta variazione del prezzo per 1 giorno di calendario che passa (di
 * solito negativa)
 * @param rho variazione del prezzo per +1 punto percentuale del tasso
 * domestico/di sconto
 */
public record Greeks(
        double delta,
        double gamma,
        double vega,
        double theta,
        double rho) {

    /**
     * Greche di un'opzione a scadenza: solo il delta (0 o +/-1) e'
     * significativo.
     */
    public static Greeks atExpiry(double delta) {
        return new Greeks(delta, 0.0, 0.0, 0.0, 0.0);
    }
}
