package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemBusinessCalendarRepository extends JpaRepository<SystemBusinessCalendar, Integer> {

    public SystemBusinessCalendar findBySbcId(Integer sbcId);
}
