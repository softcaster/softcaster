package org.softcaster.easy_pricer_srv.dto;

import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;

/**
 * Esito della registrazione o modifica di un'opzione OTC: contratto, lato,
 * quantita' (1), premio totale e valutazione alla data dell'operazione
 * (null se mancano volatilita' o curve).
 */
public record OtcOptionDealResponse(
        OtcOptionDto contract,
        String side,
        double quantity,
        double premium,
        PricingResponse pricing) {
}
