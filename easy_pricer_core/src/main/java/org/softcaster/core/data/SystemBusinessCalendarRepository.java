package org.softcaster.core.data;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemBusinessCalendarRepository extends JpaRepository<SystemBusinessCalendar, Integer> {

    @EntityGraph(attributePaths = {"calendar", "currency"})
    public SystemBusinessCalendar findBySbcId(Integer sbcId);
}
