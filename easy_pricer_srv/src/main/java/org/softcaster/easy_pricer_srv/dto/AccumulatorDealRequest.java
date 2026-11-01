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
 * kiEffect LEVERAGE / CONTRACT, guaranteeType LEVERAGED / UNLEVERAGED,
 * side BUY / SELL.
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
    // quantita' per fixing oppure (in alternativa) quantita' massima totale senza
    // leva, divisa per il numero di fixing: 100.000 su 22 fixing -> 4.545,45
    public Double dailyQuantity;
    public Double maxQuantity;
    public LocalDate firstFixingDate;
    public LocalDate lastFixingDate;
    // primi periodi di regolamento garantiti (knock-out non attivo) e tipo di
    // garanzia: LEVERAGED (default se ci sono periodi garantiti, leva normale)
    // o UNLEVERAGED (quantita' base su ogni fixing); 0 periodi = nessuna garanzia
    public Integer guaranteedPeriods = 0;
    public String guaranteeType;
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
    // in alternativa: percentuale dell'importo massimo nella seconda valuta
    // (quantita' massima x strike): -0,282713 su 115.000 USD -> -325,12 USD
    public Double premiumPercent;

    // -- pricing
    public Double spot;
    public Double volatility;
    public String domesticRCurve;
    public String foreignRCurve;
    public Integer paths;
}
