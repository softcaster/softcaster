package org.softcaster.core.data;

import jakarta.persistence.CascadeType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "calendar")
@SuppressWarnings("PersistenceUnitPresent")
public class Calendar implements Serializable {

    @Id
    @SequenceGenerator(name = "calendar_seq", sequenceName = "calendar_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "calendar_seq")
    @Column(name = "id_calendar", columnDefinition = "INTEGER")
    private Integer idCalendar;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "calendar", nullable = false) // FK in child table holiday
    private List<Holiday> holidays = new ArrayList<>();    
    
    public Integer getIdCalendar() {
        return idCalendar;
    }

    public void setIdCalendar(Integer idCalendar) {
        this.idCalendar = idCalendar;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdCalendar() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Calendar that = (Calendar) obj;
        return getIdCalendar().equals(that.getIdCalendar());
    }

    @Override
    public int hashCode() {
        return getIdCalendar() == null ? 0 : idCalendar.hashCode();
    }

    /**
     * @return the holidays
     */
    public List<Holiday> getHolidays() {
        return holidays;
    }

    /**
     * @param holidays the holidays to set
     */
    public void setHolidays(List<Holiday> holidays) {
        this.holidays = holidays;
    }
    
    @Override
    public String toString() {
        return code;
    }   
}
