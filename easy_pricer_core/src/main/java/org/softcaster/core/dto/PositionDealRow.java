package org.softcaster.core.dto;

import java.math.BigDecimal;

/**
 * Deal di una posizione, per ricostruirla senza caricare le transazioni.
 *
 * @param quantity quantita' con segno del deal (negativa per una vendita)
 * @param price    prezzo del deal
 * @param accrual  rateo del deal (componente BOND_ACCRUAL), null se non ne ha
 */
public record PositionDealRow(Double quantity, Double price, BigDecimal accrual) {
}
