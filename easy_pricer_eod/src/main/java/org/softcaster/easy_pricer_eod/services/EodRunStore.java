package org.softcaster.easy_pricer_eod.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.softcaster.core.data.EodRun;
import org.softcaster.core.data.EodRunDAO;
import org.softcaster.core.data.EodStepRun;
import org.softcaster.core.data.EodStepRunDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.SbcStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scritture della chiusura di giornata su calendario, eod_run ed eod_step_run.
 */
@Service
public class EodRunStore {

    private static final int SBC_ID = 1;

    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private EodRunDAO eodRunDAO;
    @Autowired
    private EodStepRunDAO eodStepRunDAO;

    /**
     * Nuovo lancio per la data ufficiale. Con il calendario gia' in CLOSING e'
     * il rilancio di una chiusura fallita: tiene la data successiva gia' fissata.
     */
    @Transactional
    public EodRun beginRun() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate next;
        if (sbc.getStatus() == SbcStatus.OPEN) {
            next = new Calendar(sbc.getCalendar()).getNextBusinessDate(sbc.getOfficialDate(), 1);
        } else if (sbc.getStatus() == SbcStatus.CLOSING && sbc.getNextBusinessDate() != null) {
            next = sbc.getNextBusinessDate();
        } else {
            throw new IllegalStateException("The business calendar is " + sbc.getStatus().getDescription());
        }
        EodRun run = new EodRun();
        run.setBusinessDate(sbc.getOfficialDate());
        run.setNextBusinessDate(next);
        run.setStatus(EodStatus.RUNNING);
        run.setStartedAt(LocalDateTime.now());
        return eodRunDAO.saveOrUpdate(run);
    }

    /**
     * Lancio rimasto RUNNING senza nessuno che lo esegua (console caduta).
     */
    public Optional<EodRun> findRunning() {
        return eodRunDAO.findRunning();
    }

    @Transactional
    public void failInterruptedRun(EodRun run) {
        for (EodStepRun stepRun : eodStepRunDAO.findByEodRun(run.getEodRunId())) {
            if (stepRun.getStatus() == EodStatus.RUNNING) {
                stepRun.setStatus(EodStatus.FAILED);
                stepRun.setMessage("Interrupted");
                stepRun.setEndedAt(LocalDateTime.now());
                eodStepRunDAO.saveOrUpdate(stepRun);
            }
        }
        endRun(run, EodStatus.FAILED, "Interrupted");
    }

    /**
     * Blocca i deal: da qui srv li rifiuta.
     */
    @Transactional
    public void openClosing(LocalDate next) {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        sbc.setStatus(SbcStatus.CLOSING);
        sbc.setNextBusinessDate(next);
        sbc.setUpdatedAt(LocalDateTime.now());
        sbcDAO.saveOrUpdate(sbc);
    }

    public EodStepRun beginStep(EodRun run, EodStep step) {
        EodStepRun stepRun = new EodStepRun();
        stepRun.setEodRun(run.getEodRunId());
        stepRun.setStep(step);
        stepRun.setStatus(EodStatus.RUNNING);
        stepRun.setStartedAt(LocalDateTime.now());
        return eodStepRunDAO.saveOrUpdate(stepRun);
    }

    public EodStepRun endStep(EodStepRun stepRun, EodTaskResult result) {
        stepRun.setStatus(result.status());
        stepRun.setProcessed(result.processed());
        stepRun.setCreated(result.created());
        stepRun.setSkipped(result.skipped());
        stepRun.setErrors(result.errors());
        stepRun.setMessage(result.message());
        stepRun.setEndedAt(LocalDateTime.now());
        return eodStepRunDAO.saveOrUpdate(stepRun);
    }

    public void endRun(EodRun run, EodStatus status, String message) {
        run.setStatus(status);
        run.setMessage(message);
        run.setEndedAt(LocalDateTime.now());
        eodRunDAO.saveOrUpdate(run);
    }

    /**
     * Cambio data e chiusura del lancio nella stessa transazione: non puo'
     * restare un lancio aperto su una data gia' cambiata.
     */
    @Transactional
    public void rollDate(EodRun run, EodStepRun stepRun, EodStatus runStatus) {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate closed = sbc.getOfficialDate();
        LocalDate next = sbc.getNextBusinessDate();
        sbc.setLastOfficialDate(closed);
        sbc.setOfficialDate(next);
        sbc.setNextBusinessDate(null);
        sbc.setStatus(SbcStatus.OPEN);
        sbc.setUpdatedAt(LocalDateTime.now());
        sbcDAO.saveOrUpdate(sbc);

        String message = "Official date rolled from " + closed + " to " + next;
        endStep(stepRun, EodTaskResult.of(EodStep.DATE_ROLL, closed, 1, 1, 0, 0, message));
        endRun(run, runStatus, message);
    }
}
