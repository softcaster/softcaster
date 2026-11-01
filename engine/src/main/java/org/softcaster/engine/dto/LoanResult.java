package org.softcaster.engine.dto;

import java.time.LocalDate;

/**
 * Risultato di LoanPricer, nella valuta del mutuo, dal punto di vista di chi
 * presta.
 *
 * analytics: dirtyPrice = valore attuale delle rate future (fair value),
 * accruedInterest = interessi maturati nel periodo in corso, yield = tasso
 * interno delle rate future al fair value (composto annuo, ACT/365),
 * modifiedDuration, dv01 (variazione del fair value per +1 punto base della
 * curva, con segno), flussi con fattori di sconto.
 *
 * @param outstandingPrincipal debito residuo dopo l'ultima rata pagata
 * @param nextPaymentDate      prossima rata (null se il mutuo e' estinto)
 * @param nextPaymentAmount    importo della prossima rata (capitale + interessi)
 * @param installment          rata di ammortamento (prima rata dopo il
 *                             preammortamento; costante nel piano francese)
 * @param taeg                 tasso annuo effettivo globale: rende il netto
 *                             erogato uguale alle rate piu' le spese per rata,
 *                             composto annuo su ACT/365 dall'erogazione
 * @param totalInterest        interessi di tutto il piano
 * @param totalFees            spese di tutto il piano (erogazione + rate)
 */
public record LoanResult(
        String instrumentId,
        LocalDate valuationDate,
        CashFlowAnalytics analytics,
        double outstandingPrincipal,
        LocalDate nextPaymentDate,
        double nextPaymentAmount,
        double installment,
        double taeg,
        double totalInterest,
        double totalFees) implements CashFlowResult {
}
