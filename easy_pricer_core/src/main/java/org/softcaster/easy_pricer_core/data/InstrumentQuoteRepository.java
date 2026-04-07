package org.softcaster.easy_pricer_core.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstrumentQuoteRepository extends JpaRepository<InstrumentQuote, Integer> {

    public InstrumentQuote findByIdInstrumentQuote(Integer idInstrumentQuote);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM instrument_quote WHERE master_data = :id", nativeQuery = true)
    void deleteInstrumentQuotes(@Param("id") Integer masterData);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM instrument_quote_hist WHERE master_data = :id", nativeQuery = true)
    void deleteInstrumentQuoteHist(@Param("id") Integer masterData);
}
