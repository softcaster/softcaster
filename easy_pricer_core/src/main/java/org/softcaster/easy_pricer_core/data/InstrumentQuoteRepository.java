package org.softcaster.easy_pricer_core.data;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InstrumentQuoteRepository extends JpaRepository<InstrumentQuote, Integer> {

    public InstrumentQuote findByIdInstrumentQuote(Integer idInstrumentQuote);

    @Query("SELECT i FROM InstrumentQuote i WHERE i.masterData.assetClass.code = :assetClass")
    public List<InstrumentQuote> findByAssetClass(@Param("assetClass") String assetClass);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM instrument_quote WHERE master_data = :id", nativeQuery = true)
    public void deleteInstrumentQuotes(@Param("id") Integer masterData);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM instrument_quote_hist WHERE master_data = :id", nativeQuery = true)
    public void deleteInstrumentQuoteHist(@Param("id") Integer masterData);
}
