package org.softcaster.easy_pricer_mtm.services;

import java.time.LocalDate;

/**
 * Esito del MtM di fine giornata (POST /internal/mtm/eod).
 *
 * @param positions        posizioni valutate
 * @param valuationErrors  posizioni non valutate (nessuna fotografia)
 * @param created          fotografie nuove
 * @param updated          fotografie della stessa data aggiornate (non ancora contabilizzate)
 * @param alreadyPosted    fotografie della stessa data gia' contabilizzate, lasciate com'erano
 * @param unchanged        posizioni chiuse senza variazioni dall'ultima fotografia
 * @param notAccounted     posizioni di asset class non contabilizzate a MTM
 * @param snapshotErrors   errori nella scrittura delle fotografie
 */
public record EodMtmResult(LocalDate valuationDate, int positions, int valuationErrors, int created, int updated,
        int alreadyPosted, int unchanged, int notAccounted, int snapshotErrors) {
}
