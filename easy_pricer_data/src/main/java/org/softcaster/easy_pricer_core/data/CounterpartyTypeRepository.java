package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterpartyTypeRepository extends JpaRepository<CounterpartyType, Integer> {

    public CounterpartyType findByIdCounterpartyType(Integer idCounterpartyType);
}
