/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Risultato del pricing di un bond future / bond forward.
 *
 * @param theoreticalPrice prezzo teorico del future (per 100 di nominale),
 * senza arrotondamento al tick
 * @param termRepoRate tasso repo implicito nella curva tra valuationDate e
 * deliveryDate, interesse semplice su repoDaycount (da confrontare con
 * l'implied repo del CTD)
 * @param ctdIsin titolo cheapest-to-deliver
 * @param deliverables analisi di tutti i titoli del paniere, nello stesso
 * ordine dell'input
 */
public record XRBForwardResult(
        String instrumentId,
        LocalDate valuationDate,
        LocalDate deliveryDate,
        double theoreticalPrice,
        double termRepoRate,
        String ctdIsin,
        List<DeliverableAnalysis> deliverables)
        implements ForwardResult {

    public XRBForwardResult {
        deliverables = List.copyOf(deliverables);
    }

    /**
     * Richiesto da ForwardResult: per un bond future il "forward" e' il prezzo
     * teorico.
     */
    @Override
    public double forwardRate() {
        return theoreticalPrice;
    }

    /**
     * Analisi del CTD.
     */
    public DeliverableAnalysis ctd() {
        return deliverables.stream()
                .filter(d -> d.isin().equals(ctdIsin))
                .findFirst()
                .orElseThrow();
    }

    /**
     * DV01 del future per 100 di nominale: il future segue il CTD diviso il
     * fattore di conversione, quindi DV01 del CTD / CF.
     *
     * @return
     */
    public double futureDv01() {
        DeliverableAnalysis ctd = ctd();
        return ctd.dv01() / ctd.conversionFactor();
    }
}
