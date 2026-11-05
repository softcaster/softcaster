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
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.softcaster.core.data.converters.EodStatusConverter;
import org.softcaster.engine.enums.EodStatus;

/**
 * Un lancio della chiusura di giornata. Una chiusura fallita si rilancia con una
 * riga nuova, cosi' resta lo storico dei tentativi: gli step gia' riusciti per la
 * stessa data non si rieseguono.
 */
@Entity
@Table(name = "eod_run")
@SuppressWarnings("PersistenceUnitPresent")
public class EodRun implements Serializable {

    private static final int MAX_MESSAGE = 2000;

    @Id
    @SequenceGenerator(name = "eod_run_seq", sequenceName = "eod_run_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eod_run_seq")
    @Column(name = "eod_run_id")
    private Integer eodRunId;

    @Column(name = "business_date")
    private LocalDate businessDate;

    @Column(name = "next_business_date")
    private LocalDate nextBusinessDate;

    @Convert(converter = EodStatusConverter.class)
    @Column(name = "status")
    private EodStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "message")
    private String message;

    public Integer getEodRunId() {
        return eodRunId;
    }

    public void setEodRunId(Integer eodRunId) {
        this.eodRunId = eodRunId;
    }

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(LocalDate businessDate) {
        this.businessDate = businessDate;
    }

    public LocalDate getNextBusinessDate() {
        return nextBusinessDate;
    }

    public void setNextBusinessDate(LocalDate nextBusinessDate) {
        this.nextBusinessDate = nextBusinessDate;
    }

    public EodStatus getStatus() {
        return status;
    }

    public void setStatus(EodStatus status) {
        this.status = status;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null && message.length() > MAX_MESSAGE ? message.substring(0, MAX_MESSAGE) : message;
    }
}
