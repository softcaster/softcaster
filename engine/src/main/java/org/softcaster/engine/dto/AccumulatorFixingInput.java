package org.softcaster.engine.dto;

/**
 * Fixing futuro di un accumulator, gia' tradotto in dati di mercato dal
 * chiamante (il pricer non conosce curve ne' sottostante).
 *
 * @param index             posizione nel calendario completo (0 = primo
 *                          fixing del contratto): serve per i fixing garantiti
 * @param time              anni dalla data di valutazione al fixing (ACT/365);
 *                          0 = fixing del giorno non ancora rilevato, al forward
 * @param forward           forward del sottostante alla data di fixing
 * @param settlementForward forward del sottostante alla data di regolamento
 *                          del periodo
 * @param discountFactor    fattore di sconto dalla valutazione al
 *                          regolamento, nella valuta di regolamento
 * @param periodEnd         ultimo fixing del periodo di regolamento (per il
 *                          knock-out osservato alle date di regolamento)
 * @param volatility        volatilita' implicita alla scadenza del fixing
 *                          (annua, decimale): la varianza totale
 *                          volatility^2 time fa la struttura a termine
 */
public record AccumulatorFixingInput(
        int index,
        double time,
        double forward,
        double settlementForward,
        double discountFactor,
        boolean periodEnd,
        double volatility) {

    public AccumulatorFixingInput {
        if (time < 0) {
            throw new IllegalArgumentException("fixing " + index + ": time must be >= 0 (future or same-day fixing)");
        }
        if (forward <= 0 || settlementForward <= 0) {
            throw new IllegalArgumentException("fixing " + index + ": forward must be > 0");
        }
        if (discountFactor <= 0) {
            throw new IllegalArgumentException("fixing " + index + ": discountFactor must be > 0");
        }
        if (volatility < 0) {
            throw new IllegalArgumentException("fixing " + index + ": volatility must be >= 0");
        }
    }
}
