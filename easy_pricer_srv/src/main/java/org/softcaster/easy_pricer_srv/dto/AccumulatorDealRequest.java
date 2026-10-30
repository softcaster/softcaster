package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Deal capture di un accumulator / decumulator: termini del contratto,
 * dati dell'operazione e parametri di pricing. La stessa richiesta serve a
 * prezzare i termini (/otc/accumulator/price) e a registrare il deal
 * (/otc/accumulator/deal).
 *
 * Codici degli enum come nel database: accumulatorType ACCUMULATOR /
 * DECUMULATOR, settlementType PHYSICAL / CASH, settlementFrequency DAILY /
 * WEEKLY / MONTHLY / NONE, koObservation FIXING / CONTINUOUS / SETTLEMENT,
 * kiEffect LEVERAGE / CONTRACT, side BUY / SELL.
 */
public class AccumulatorDealRequest {

    // -- contratto
    // coppia di cambi (FXA) o scadenza di un future su commodity (CMA)
    public String underlyingCode;
    public String accumulatorType = "ACCUMULATOR";
    public String settlementType = "PHYSICAL";
    public String settlementFrequency = "WEEKLY";
    public Double strike;
    public Double koBarrier;
    public String koObservation = "FIXING";
    public Double kiBarrier;
    public String kiEffect;
    public Double leverage = 1.;
    public Double dailyQuantity;
    public LocalDate firstFixingDate;
    public LocalDate lastFixingDate;
    public Integer guaranteedFixings = 0;
    public Boolean koAccrual = false;
    // spot lag del regolamento; null = giorni di regolamento del sottostante
    public Integer spotLag;
    public LocalTime fixingTime;
    public String fixingTimeZone;
    public String description;

    // -- operazione (solo per la registrazione)
    public LocalDate tradeDate;
    public String side = "BUY";
    public Integer counterpartyId;
    public Integer positionMdId;
    // premio iniziale per contratto, valuta di regolamento, pagato da chi compra (0 = zero cost)
    public Double premium = 0.;

    // -- pricing
    public Double spot;
    public Double volatility;
    public String domesticRCurve;
    public String foreignRCurve;
    public Integer paths;
}
