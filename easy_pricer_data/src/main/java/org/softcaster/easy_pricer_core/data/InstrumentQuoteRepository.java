package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InstrumentQuoteRepository extends JpaRepository<InstrumentQuote, Integer> {

    public InstrumentQuote findByIdInstrumentQuote(Integer idInstrumentQuote);
}
