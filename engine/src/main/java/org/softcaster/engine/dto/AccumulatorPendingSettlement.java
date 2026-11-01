package org.softcaster.engine.dto;

/**
 * Quantita' gia' maturata da fixing avvenuti e non ancora regolata: vale come
 * un forward allo strike con scadenza il regolamento del periodo.
 *
 * @param quantity          quantita' maturata (leva inclusa), sempre positiva
 * @param settlementForward forward del sottostante alla data di regolamento
 * @param discountFactor    fattore di sconto dalla valutazione al regolamento
 */
public record AccumulatorPendingSettlement(
        double quantity,
        double settlementForward,
        double discountFactor) {

    public AccumulatorPendingSettlement {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must be >= 0");
        }
        if (settlementForward <= 0 || discountFactor <= 0) {
            throw new IllegalArgumentException("settlementForward and discountFactor must be > 0");
        }
    }
}
