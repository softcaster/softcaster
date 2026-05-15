package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FrequencyRepository extends JpaRepository<Frequency, Integer> {

    public Frequency findByIdFrequency(Integer idFrequency);

    public Frequency findByCode(String code);
}
