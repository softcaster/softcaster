package org.softcaster.market_data.services.Eurext;

/**
 * Serie di opzione Eurex. settlementPrice e' null se la serie non ha ancora
 * un prezzo di settlement.
 */
public record OptionSeries(String isin,
        int contractId,
        String expirationDate,
        double strike,
        String callPut,
        int versionNr,
        String settlementDate,
        Double settlementPrice,
        long openInterest) {

}
