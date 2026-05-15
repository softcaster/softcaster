package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterpartyTypeRepository extends JpaRepository<CounterpartyType, Integer> {

    public CounterpartyType findByIdCounterpartyType(Integer idCounterpartyType);

    public CounterpartyType findByCode(String code);
}
