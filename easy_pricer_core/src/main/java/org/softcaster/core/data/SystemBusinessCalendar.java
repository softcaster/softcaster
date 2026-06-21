package org.softcaster.core.data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.softcaster.core.data.converters.SbcStatusConverter;
import org.softcaster.engine.enums.SbcStatus;

@Entity
@Table(name = "system_business_calendar")
@SuppressWarnings("PersistenceUnitPresent")

public class SystemBusinessCalendar implements Serializable {

    @Id
    @Column(name = "sbc_id")
    private Integer sbcId;

    @Column(name = "description")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "calendar", nullable = true)
    private Calendar calendar;

    @Convert(converter = SbcStatusConverter.class)
    @Column(name = "status")
    private SbcStatus status;

    @Column(name = "official_date")
    private LocalDate officialDate;

    @Column(name = "next_business_date")
    private LocalDate nextBusinessDate;

    @Version
    @Column(name = "version")
    private Integer version;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Integer getSbcId() {
        return sbcId;
    }

    public void setSbcId(Integer sbcId) {
        this.sbcId = sbcId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (sbcId == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SystemBusinessCalendar that = (SystemBusinessCalendar) obj;
        return sbcId.equals(that.sbcId);
    }

    @Override
    public int hashCode() {
        return sbcId == null ? 0 : sbcId.hashCode();
    }

    /**
     * @return the calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * @param calendar the calendar to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the status
     */
    public SbcStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(SbcStatus status) {
        this.status = status;
    }

    /**
     * @return the officialDate
     */
    public LocalDate getOfficialDate() {
        return officialDate;
    }

    /**
     * @param officialDate the officialDate to set
     */
    public void setOfficialDate(LocalDate officialDate) {
        this.officialDate = officialDate;
    }

    /**
     * @return the nextBusinessDate
     */
    public LocalDate getNextBusinessDate() {
        return nextBusinessDate;
    }

    /**
     * @param nextBusinessDate the nextBusinessDate to set
     */
    public void setNextBusinessDate(LocalDate nextBusinessDate) {
        this.nextBusinessDate = nextBusinessDate;
    }

    /**
     * @return the updatedAt
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
