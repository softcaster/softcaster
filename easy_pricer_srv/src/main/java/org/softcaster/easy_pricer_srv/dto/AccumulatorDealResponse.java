package org.softcaster.easy_pricer_srv.dto;

import org.softcaster.easy_pricer_mds_core.dto.AccumulatorPricingResponse;

/**
 * Esito della registrazione: contratto creato (con il calendario), dati
 * dell'operazione e, se e' stata indicata la volatilita', la valutazione
 * alla data dell'operazione.
 */
public record AccumulatorDealResponse(
        AccumulatorDto contract,
        String side,
        double quantity,
        double premium,
        AccumulatorPricingResponse pricing) {
}
