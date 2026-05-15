package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Integer> {

    public Holiday findByIdHoliday(Integer idHoliday);
}
