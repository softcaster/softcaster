package org.softcaster.market_data.core;

import java.time.LocalDate;
import java.util.List;
import org.softcaster.market_data.core.enums.Market;

public interface MarketDataManager {

    Node getQuote(String ticker, Market market);

    /**
     * Tassi di una curva, alla data piu' recente disponibile.
     *
     * @param curveId chiave della curva per il provider (yield_curve.provider_code,
     *                o il codice della curva)
     * @return 
     */
    List<Node> getRates(String curveId);

    /**
     * Tassi riferiti a una data (es. la data ufficiale). I provider che non
     * gestiscono la data restituiscono quelli di getRates(curveId).
     * @param curveId
     * @param referenceDate
     * @return 
     */
    default List<Node> getRates(String curveId, LocalDate referenceDate) {
        return getRates(curveId);
    }

    String getMarketDataServiceId();

    List<Market> getMarkets();
}
