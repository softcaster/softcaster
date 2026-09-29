package org.softcaster.core.data;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketQuoteRepository extends JpaRepository<MarketQuote, Integer> {

    public MarketQuote findByMarketQuoteIdAndBusinessDate(Integer marketQuoteId, LocalDate businessDate);
}
