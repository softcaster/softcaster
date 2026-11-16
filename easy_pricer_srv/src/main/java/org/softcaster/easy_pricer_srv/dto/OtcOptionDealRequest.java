package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Deal capture di un'opzione OTC: termini del contratto, dati
 * dell'operazione e parametri di pricing. La stessa richiesta serve a
 * prezzare i termini (/otc/option/price) e a registrare il deal
 * (/otc/option/deal).
 *
 * Codici degli enum come nel database: optionType CALL / PUT, exerciseStyle
 * EUROPEAN / AMERICAN, settlementType PHYSICAL / CASH, side BUY / SELL.
 */
public class OtcOptionDealRequest {

    // -- contratto
    // coppia di cambi (OFX) o scadenza di un future su commodity (OCM)
    public String underlyingCode;
    public String optionType = "CALL";
    public String exerciseStyle = "EUROPEAN";
    public String settlementType = "PHYSICAL";
    public Double strike;
    // quantita' del sottostante: valuta base (OFX) o unita' della commodity (OCM)
    public Double notional;
    public LocalDate expiryDate;
    // consegna all'esercizio; null = scadenza + spot lag (OFX), giorno della scadenza (OCM)
    public LocalDate deliveryDate;
    // spot lag del premio e della consegna; null = giorni di regolamento del sottostante
    public Integer spotLag;
    // cut della scadenza (es. 10:00 America/New_York)
    public LocalTime expiryTime;
    public String expiryTimeZone;
    public String description;

    // -- operazione (solo per la registrazione)
    public LocalDate tradeDate;
    public String side = "BUY";
    public Integer counterpartyId;
    public Integer positionMdId;
    // premio totale nella valuta di regolamento, pagato da chi compra; in
    // alternativa (uno solo dei tre):
    public Double premium;
    // premio per unita' di sottostante (OFX: pips della valuta quotata, 0,0125 USD per EUR)
    public Double premiumUnit;
    // percentuale del controvalore allo strike (nozionale x strike): 1,05 su 1.170.000 USD -> 12.285 USD
    public Double premiumPercent;

    // -- pricing
    public Double spot;
    public Double volatility;
    // premio di mercato per unita' di sottostante: valutazione alla volatilita' implicita
    public Double optionPrice;
    public String domesticRCurve;
    public String foreignRCurve;
}
