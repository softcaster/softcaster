/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.temporal.ChronoUnit;

/**
 * Input comune a tutte le opzioni.
 *
 * Ogni sottotipo descrive un sottostante diverso, e quindi un modello diverso:
 * - EquityOptionInput: azioni e indici -> Black-Scholes(-Merton), CRR -
 * FxOptionInput: cambi -> Garman-Kohlhagen - ForwardOptionInput: future e
 * forward (anche bond) -> Black-76
 *
 * Convenzioni: - valuationDate e' la data di valutazione (trade date), non la
 * data di regolamento; - volatility in DECIMALE (0.20 = 20%), annua, sulla base
 * ACT/365; - la volatilita' e' un dato di mercato e sta fuori dal contratto,
 * cosi' il calcolo della volatilita' implicita riusa lo stesso input con
 * withVolatility().
 */
public sealed interface OptionInput extends PricingInput
        permits EquityOptionInput, FxOptionInput, ForwardOptionInput {

    /**
     * instrumentId e valuationDate sono dichiarati qui perche' PricingInput non
     * li espone: i record li implementano automaticamente con i componenti
     * omonimi.
     * @return 
     */
    String instrumentId();

    java.time.LocalDate valuationDate();

    OptionContract contract();

    double volatility();

    /**
     * Copia dell'input con un'altra volatilita' (stesso tipo concreto).
     * @param volatility
     * @return 
     */
    OptionInput withVolatility(double volatility);

    /**
     * Tempo alla scadenza in anni ACT/365 fixed, la base usuale delle
     * volatilita'.
     * @return 
     */
    default double timeToExpiry() {
        return ChronoUnit.DAYS.between(valuationDate(), contract().expiryDate()) / 365.0;
    }

    /**
     * Controlli comuni, da chiamare nel compact constructor dei record passando
     * i PARAMETRI: dentro il compact constructor i campi non sono ancora
     * assegnati, quindi non si puo' usare this.
     * @param valuationDate
     * @param contract
     * @param volatility
     */
    static void validate(java.time.LocalDate valuationDate, OptionContract contract, double volatility) {
        java.util.Objects.requireNonNull(valuationDate, "valuationDate");
        java.util.Objects.requireNonNull(contract, "contract");
        if (contract.expiryDate().isBefore(valuationDate)) {
            throw new IllegalArgumentException("Opzione scaduta: expiry " + contract.expiryDate()
                    + " precedente alla valuationDate " + valuationDate);
        }
        if (volatility < 0 || Double.isNaN(volatility)) {
            throw new IllegalArgumentException("volatility deve essere >= 0");
        }
    }
}
