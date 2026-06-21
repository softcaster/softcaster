package org.softcaster.core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentValuationRepository extends JpaRepository<InstrumentValuation, Integer> {

    public InstrumentValuation findByInstrumentValuationId(Integer instrumentValuationId);
}
