/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;

/**
 * Termini contrattuali di un'opzione: non contiene dati di mercato.
 *
 * @param type          CALL o PUT
 * @param style         EUROPEAN, AMERICAN o BERMUDAN
 * @param strike        prezzo di esercizio, nella stessa unita' del sottostante
 * @param expiryDate    data di scadenza (ultimo giorno di esercizio)
 * @param notional      quantita' di sottostante coperta dal contratto (es. 1 per un prezzo unitario,
 *                      100 azioni, 25.000 EUR di nozionale): il pricer restituisce il prezzo unitario,
 *                      il valore della posizione e' prezzo * notional
 * @param exerciseDates date di esercizio anticipato, solo per le BERMUDAN (la scadenza e' sempre
 *                      esercitabile e non va ripetuta); lista vuota per gli altri stili
 */
public record OptionContract(
        OptionType type,
        ExerciseStyle style,
        double strike,
        LocalDate expiryDate,
        double notional,
        List<LocalDate> exerciseDates) {

    public OptionContract {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(style, "style");
        Objects.requireNonNull(expiryDate, "expiryDate");
        if (strike <= 0) {
            throw new IllegalArgumentException("strike deve essere > 0");
        }
        exerciseDates = exerciseDates == null ? List.of() : List.copyOf(exerciseDates);
        if (style == ExerciseStyle.BERMUDAN) {
            if (exerciseDates.isEmpty()) {
                throw new IllegalArgumentException("Un'opzione BERMUDAN richiede almeno una data di esercizio");
            }
            for (LocalDate d : exerciseDates) {
                if (d.isAfter(expiryDate)) {
                    throw new IllegalArgumentException("Data di esercizio " + d + " successiva alla scadenza " + expiryDate);
                }
            }
        } else if (!exerciseDates.isEmpty()) {
            throw new IllegalArgumentException("Le date di esercizio sono previste solo per le opzioni BERMUDAN");
        }
    }

    /** Europea o americana, contratto unitario (notional = 1). */
    public static OptionContract of(OptionType type, ExerciseStyle style, double strike, LocalDate expiryDate) {
        return new OptionContract(type, style, strike, expiryDate, 1.0, List.of());
    }

    /** Bermudan, contratto unitario (notional = 1). */
    public static OptionContract bermudan(OptionType type, double strike, LocalDate expiryDate,
                                          List<LocalDate> exerciseDates) {
        return new OptionContract(type, ExerciseStyle.BERMUDAN, strike, expiryDate, 1.0, exerciseDates);
    }

    public boolean isCall() {
        return type == OptionType.CALL;
    }
}