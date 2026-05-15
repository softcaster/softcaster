package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DaycountRepository extends JpaRepository<Daycount, Integer> {

    public Daycount findByIdDaycount(Integer idDaycount);

    public Daycount findByCode(String code);
}
