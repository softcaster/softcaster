package org.softcaster.easy_pricer_srv.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Deal capture di un mutuo: termini, spese, condizioni del variabile, dati
 * dell'operazione e parametri di valutazione. La stessa richiesta serve a
 * simulare il piano (/otc/loan/price) e a registrare il mutuo (/otc/loan/deal).
 *
 * Codici come nel database: rateType FIXED / FLOATING, frequency MONTHLY /
 * QUARTERLY / SEMI-ANNUAL / ANNUAL, amortization SAS (francese) / SLP
 * (italiano) / IOL (bullet), daycount EUR_30_360 / ACT_360 / ACT_365...,
 * rollConvention UNADJUSTED / FORWARD / FORWARD-MODIFIED...
 */
public class LoanDealRequest {

    // -- termini
    public String rateType = "FIXED";
    public String currency = "EUR";
    public Double principalAmount;
    // TAN in punti percentuali (3.2 = 3,2%); per il variabile: tasso corrente (indice + spread)
    public Double annualRate;
    public LocalDate disbursementDate;
    // vuota = erogazione + un periodo; se non cade a un periodo pieno il primo periodo e' preammortamento tecnico
    public LocalDate firstPaymentDate;
    public LocalDate maturityDate;
    public String frequency = "MONTHLY";
    public String amortization = "SAS";
    public String daycount = "EUR_30_360";
    public String rollConvention = "UNADJUSTED";
    public Integer preAmortizationPeriods = 0;
    public String description;

    // -- spese: tipo PROCESSING / UNDERWRITING / TAX / INCIDENTAL / MANAGEMENT / INSURANCE,
    //    basis AMOUNT / PERCENT (punti: 0.25 = 0,25%), timing UPFRONT / PER_INSTALLMENT
    public List<Fee> fees = new ArrayList<>();
    // condizioni di servizio, fuori dal prezzo
    public Double defaultInterest = 0.;
    public Double latePaymentFee = 0.;

    // -- variabile (rateType FLOATING): spread, floor, cap in decimale (0.012 = 120 bp)
    public Double spread;
    public Double floor;
    public Double cap;
    public String indexCode;
    public Integer indexMonths = 6;
    public Integer fixingLagDays = 2;

    // -- operazione (solo per la registrazione): BUY = la banca presta
    public LocalDate tradeDate;
    public String side = "BUY";
    public Integer counterpartyId;
    public Integer positionMdId;

    // -- valutazione
    public LocalDate valuationDate;
    public String discountCurve;
    // tassi in decimale, continui
    public Double discountRate;
    public Double creditSpread;

    public static class Fee {

        public String type;
        public String basis = "AMOUNT";
        public String timing = "UPFRONT";
        public Double value;
    }
}
