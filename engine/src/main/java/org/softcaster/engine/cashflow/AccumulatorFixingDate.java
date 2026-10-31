package org.softcaster.engine.cashflow;

import java.time.LocalDate;

/**
 * Data del calendario di un accumulator.
 *
 * @param fixingDate     data di osservazione
 * @param exerciseDate   ultimo fixing del periodo di regolamento a cui
 *                       appartiene il fixing
 * @param settlementDate regolamento del periodo: exerciseDate + spot lag
 *                       giorni lavorativi
 */
public record AccumulatorFixingDate(
        LocalDate fixingDate,
        LocalDate exerciseDate,
        LocalDate settlementDate) {
}
