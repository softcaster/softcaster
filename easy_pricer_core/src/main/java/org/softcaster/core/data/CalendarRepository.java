package org.softcaster.core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalendarRepository extends JpaRepository<Calendar, Integer> {

    public Calendar findByIdCalendar(Integer idCalendar);

    @Query(value = "SELECT h FROM Holiday h WHERE h.calendar=:id")
    public List<Holiday> findHolidaysByIdCalendar(@Param("id") Integer idCalendar);

    public Calendar findByCode(String code);
}
