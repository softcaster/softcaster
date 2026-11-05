package org.softcaster.core.dto;

import java.time.LocalDate;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;

/**
 * Esito di un task della chiusura di giornata, uguale per tutti i servizi. HTTP
 * 200 con OK e WARNING, 500 con FAILED, sempre con questo body.
 *
 * @param task         step della chiusura eseguito
 * @param businessDate data ufficiale della chiusura, null se il task e' fallito prima di leggerla
 * @param status       OK; WARNING: completato, da controllare; FAILED: la chiusura non puo' proseguire
 *                     (mai RUNNING: questo e' l'esito finale)
 * @param processed    elementi esaminati
 * @param created      elementi creati o aggiornati
 * @param skipped      elementi senza nulla da fare (es. evento gia' generato da un rilancio)
 * @param errors       elementi non elaborati
 * @param message      dettaglio per l'operatore
 */
public record EodTaskResult(EodStep task, LocalDate businessDate, EodStatus status, int processed, int created,
        int skipped, int errors, String message) {

    /**
     * Task arrivato in fondo: FAILED se almeno un elemento e' in errore.
     */
    public static EodTaskResult of(EodStep task, LocalDate businessDate, int processed, int created, int skipped,
            int errors, String message) {
        return new EodTaskResult(task, businessDate, errors > 0 ? EodStatus.FAILED : EodStatus.OK, processed, created,
                skipped, errors, message);
    }

    /**
     * Task che non e' riuscito a partire o si e' interrotto.
     */
    public static EodTaskResult failed(EodStep task, LocalDate businessDate, String message) {
        return new EodTaskResult(task, businessDate, EodStatus.FAILED, 0, 0, 0, 0, message);
    }

    public EodTaskResult asWarning(String warning) {
        return new EodTaskResult(task, businessDate, EodStatus.WARNING, processed, created, skipped, errors, warning);
    }
}
