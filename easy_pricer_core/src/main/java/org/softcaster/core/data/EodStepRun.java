package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.softcaster.core.data.converters.EodStatusConverter;
import org.softcaster.core.data.converters.EodStepConverter;
import org.softcaster.engine.enums.EodStatus;
import org.softcaster.engine.enums.EodStep;

/**
 * Esecuzione di uno step in un lancio della chiusura di giornata.
 */
@Entity
@Table(name = "eod_step_run")
@SuppressWarnings("PersistenceUnitPresent")
public class EodStepRun implements Serializable {

    private static final int MAX_MESSAGE = 2000;

    @Id
    @SequenceGenerator(name = "eod_step_run_seq", sequenceName = "eod_step_run_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eod_step_run_seq")
    @Column(name = "eod_step_run_id")
    private Integer eodStepRunId;

    @Column(name = "eod_run")
    private Integer eodRun;

    @Convert(converter = EodStepConverter.class)
    @Column(name = "step")
    private EodStep step;

    @Convert(converter = EodStatusConverter.class)
    @Column(name = "status")
    private EodStatus status;

    @Column(name = "processed")
    private int processed;

    @Column(name = "created")
    private int created;

    @Column(name = "skipped")
    private int skipped;

    @Column(name = "errors")
    private int errors;

    @Column(name = "message")
    private String message;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    public Integer getEodStepRunId() {
        return eodStepRunId;
    }

    public void setEodStepRunId(Integer eodStepRunId) {
        this.eodStepRunId = eodStepRunId;
    }

    public Integer getEodRun() {
        return eodRun;
    }

    public void setEodRun(Integer eodRun) {
        this.eodRun = eodRun;
    }

    public EodStep getStep() {
        return step;
    }

    public void setStep(EodStep step) {
        this.step = step;
    }

    public EodStatus getStatus() {
        return status;
    }

    public void setStatus(EodStatus status) {
        this.status = status;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null && message.length() > MAX_MESSAGE ? message.substring(0, MAX_MESSAGE) : message;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }
}
