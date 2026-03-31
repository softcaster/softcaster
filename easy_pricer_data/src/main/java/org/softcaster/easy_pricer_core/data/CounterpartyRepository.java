package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterpartyRepository extends JpaRepository<Counterparty, Integer> {

    public Counterparty findByIdCounterparty(Integer idCounterparty);
}
