package org.softcaster.core.data;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "holiday")
@SuppressWarnings("PersistenceUnitPresent")

public class Holiday implements Serializable {

    @Id
    @SequenceGenerator(name = "holiday_seq", sequenceName = "holiday_s", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "holiday_seq")
    @Column(name = "id_holiday", columnDefinition = "INTEGER")
    private Integer idHoliday;

    @Column(name = "calendar", insertable = false, updatable = false)
    private Integer calendar;

    @Column(name = "holiday_day")
    private Short holidayDay;

    @Column(name = "holiday_month")
    private Short holidayMonth;

    @Column(name = "description")
    private String description;

    public Integer getIdHoliday() {
        return idHoliday;
    }

    public void setIdHoliday(Integer idHoliday) {
        this.idHoliday = idHoliday;
    }

    public Short getHolidayDay() {
        return holidayDay;
    }

    public void setHolidayDay(Short holidayDay) {
        this.holidayDay = holidayDay;
    }

    public Short getHolidayMonth() {
        return holidayMonth;
    }

    public void setHolidayMonth(Short holidayMonth) {
        this.holidayMonth = holidayMonth;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getIdHoliday() == null || obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Holiday that = (Holiday) obj;
        return getIdHoliday().equals(that.getIdHoliday());
    }

    @Override
    public int hashCode() {
        return getIdHoliday() == null ? 0 : idHoliday.hashCode();
    }

    /**
     * @return the calendar
     */
    public Integer getCalendar() {
        return calendar;
    }

    /**
     * @param calendar the idCalendar to set
     */
    public void setCalendar(Integer calendar) {
        this.calendar = calendar;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
