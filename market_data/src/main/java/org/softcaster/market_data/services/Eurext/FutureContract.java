package org.softcaster.market_data.services.Eurext;

/**
 * Scadenza di un future Eurex. settlementPrice e' null se la scadenza non ha
 * ancora un prezzo di settlement.
 */
public record FutureContract(String isin,
        int contractId,
        String contract,
        String expirationDate,
        String lastTradingDate,
        String settlementDate,
        Double settlementPrice,
        long openInterest) {

}
