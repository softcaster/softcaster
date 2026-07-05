package org.softcaster.core.data.account;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.softcaster.core.data.converters.AcctEventStatusConverter;
import org.softcaster.core.data.converters.AcctEventSourceConverter;
import org.softcaster.core.data.converters.AcctEventTypeConverter;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.EventSourceType;

@Entity
@Table(name = "accounting_events")
@SuppressWarnings("PersistenceUnitPresent")
@Inheritance(
        strategy = InheritanceType.JOINED
)
public class AccountingEvent implements Serializable {
    
    @Id
    @SequenceGenerator(name = "accounting_events_seq", sequenceName = "accounting_events_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounting_events_seq")
    @Column(name = "event_id")
    private Integer eventId;
    
    @Convert(converter = AcctEventTypeConverter.class)
    @Column(name = "event_type")
    private EventType eventType;
    
    @Convert(converter = AcctEventStatusConverter.class)
    @Column(name = "event_status")
    private AccountingEventStatus eventStatus;

    @Convert(converter = AcctEventSourceConverter.class)
    @Column(name = "source_type")
    private EventSourceType sourceType;

    @Column(name = "source_id")
    private Integer sourceId;
    
    @Column(name = "event_key")
    private String eventKey;

    @Column(name = "generated_by")
    private Integer generatedBy;

    @Column(name = "generated_ref")
    private String generatedRef;

    @Column(name = "created_at")
    private LocalDateTime  createdAt;

    @Column(name = "processed_at")
    private LocalDateTime  processedAt;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AccountingEvent that)) {
            return false;
        }

        return eventId != null
                && eventId.equals(that.eventId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * @return the eventId
     */
    public Integer getEventId() {
        return eventId;
    }

    /**
     * @param eventId the eventId to set
     */
    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * @param eventType the eventType to set
     */
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the eventStatus
     */
    public AccountingEventStatus getEventStatus() {
        return eventStatus;
    }

    /**
     * @param eventStatus the eventStatus to set
     */
    public void setEventStatus(AccountingEventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    /**
     * @return the sourceType
     */
    public EventSourceType getSourceType() {
        return sourceType;
    }

    /**
     * @param sourceType the sourceType to set
     */
    public void setSourceType(EventSourceType sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * @return the sourceId
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * @param sourceId the sourceId to set
     */
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * @return the eventKey
     */
    public String getEventKey() {
        return eventKey;
    }

    /**
     * @param eventKey the eventKey to set
     */
    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    /**
     * @return the generatedBy
     */
    public Integer getGeneratedBy() {
        return generatedBy;
    }

    /**
     * @param generatedBy the generatedBy to set
     */
    public void setGeneratedBy(Integer generatedBy) {
        this.generatedBy = generatedBy;
    }

    /**
     * @return the generatedRef
     */
    public String getGeneratedRef() {
        return generatedRef;
    }

    /**
     * @param generatedRef the generatedRef to set
     */
    public void setGeneratedRef(String generatedRef) {
        this.generatedRef = generatedRef;
    }

    /**
     * @return the createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the processedAt
     */
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    /**
     * @param processedAt the processedAt to set
     */
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
