package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterpartyRoleMappingRepository extends JpaRepository<CounterpartyRoleMapping, Integer> {

    public CounterpartyRoleMapping findByCounterpartyRoleMappingId(Integer counterpartyRoleMappingId);
}
