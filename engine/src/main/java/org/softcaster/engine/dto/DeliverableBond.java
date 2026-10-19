/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

import java.util.Objects;

/**
 * Titolo del paniere consegnabile di un bond future, con i dati gia' risolti da
 * mds_core.
 *
 * Prezzi e flussi sono espressi per 100 di nominale.
 *
 * Per un bond forward OTC su un singolo titolo si usa un solo DeliverableBond
 * con conversionFactor = 1.
 *
 * @param isin codice del titolo
 * @param schedule flussi e convenzioni del titolo (rateo ACT/ACT ICMA per i
 * BTP)
 * @param cleanPrice prezzo secco a pronti, per regolamento alla valuationDate
 * @param conversionFactor fattore di conversione rispetto al titolo nozionale
 * (6% per il MBTP)
 */
public record DeliverableBond(
        String isin,
        CashFlowSchedule schedule,
        double cleanPrice,
        double conversionFactor) {

    public DeliverableBond {
        Objects.requireNonNull(isin, "isin");
        Objects.requireNonNull(schedule, "schedule");
        if (cleanPrice <= 0) {
            throw new IllegalArgumentException("cleanPrice deve essere > 0 per " + isin);
        }
        if (conversionFactor <= 0) {
            throw new IllegalArgumentException("conversionFactor deve essere > 0 per " + isin);
        }
    }
}
