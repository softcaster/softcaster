/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.dto;

/**
 * Analisi di un titolo del paniere. Valori per 100 di nominale; tassi in
 * decimale.
 *
 * I campi basati sul prezzo quotato del future sono null se il prezzo non e'
 * stato fornito.
 *
 * @param isin codice del titolo
 * @param conversionFactor fattore di conversione
 * @param cleanPrice prezzo secco a pronti
 * @param accruedSpot rateo alla valuationDate
 * @param accruedDelivery rateo alla deliveryDate
 * @param couponsCapitalized cedole pagate tra valuationDate e deliveryDate,
 * capitalizzate al repo
 * @param forwardCleanPrice prezzo secco a termine del titolo
 * @param theoreticalPrice prezzo teorico del future se questo fosse il CTD:
 * forwardCleanPrice / CF
 * @param grossBasis cleanPrice - futurePrice * CF
 * @param netBasis forwardCleanPrice - futurePrice * CF (gross basis al netto
 * del carry)
 * @param impliedRepo tasso repo implicito della strategia cash-and-carry
 */
public record DeliverableAnalysis(
        String isin,
        double conversionFactor,
        double cleanPrice,
        double accruedSpot,
        double accruedDelivery,
        double couponsCapitalized,
        double forwardCleanPrice,
        double theoreticalPrice,
        Double grossBasis,
        Double netBasis,
        Double impliedRepo) {

}
