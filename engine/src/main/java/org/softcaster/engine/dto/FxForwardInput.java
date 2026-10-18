/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.Objects;
import org.softcaster.engine.curve.DiscountCurve;

/**
 * Input per FX forward e future su cambi.
 *
 * Convenzione: il cambio e' quotato come unita' di valuta domestica (quote) per
 * 1 unita' di valuta estera (base). Es. EURUSD = 1.08 -> base EUR, domestica
 * USD.
 *
 * valuationDate = data di regolamento dello spot (oggi + business days dello
 * strumento), coerente con la convenzione usata per i bond.
 *
 * Il contratto e' opzionale: - null -> si calcola solo il cambio forward
 * (quotazione, prezzo teorico del future) - ForwardContract -> forward OTC in
 * portafoglio, valore scontato alla valuationDate - FutureContract -> future
 * quotato (es. CME 6E), valore non scontato (margini giornalieri)
 */
public record FxForwardInput(
        String instrumentId,
        LocalDate valuationDate,
        LocalDate deliveryDate, // consegna del forward / scadenza del future
        double spot,
        DiscountCurve domesticCurve,
        DiscountCurve foreignCurve,
        FxContract contract) // null se serve solo il cambio forward
        implements ForwardInput {

    public FxForwardInput {
        Objects.requireNonNull(valuationDate, "valuationDate");
        Objects.requireNonNull(deliveryDate, "deliveryDate");
        Objects.requireNonNull(domesticCurve, "domesticCurve");
        Objects.requireNonNull(foreignCurve, "foreignCurve");
        if (spot <= 0) {
            throw new IllegalArgumentException("spot deve essere > 0");
        }
        if (deliveryDate.isBefore(valuationDate)) {
            throw new IllegalArgumentException("deliveryDate precedente alla valuationDate");
        }
    }

    /**
     * true se l'input contiene un contratto da valutare.
     */
    public boolean hasContract() {
        return contract != null;
    }

    // ------------------------------------------------------------------------
    // -- Contratti
    // ------------------------------------------------------------------------
    /**
     * Posizione da valutare. Le implementazioni sono nello stesso file, quindi
     * la clausola permits non e' necessaria.
     */
    public sealed interface FxContract {

        /**
         * Quantita' in valuta estera (base), con segno: > 0 acquisto, < 0
         * vendita.
         */
        double notional();
    }

    /**
     * Forward OTC: lo strike e' il cambio fissato alla conclusione del
     * contratto e resta fermo fino alla consegna.
     *
     * @param notional importo in valuta estera, con segno
     * @param strike cambio contrattuale
     */
    public record ForwardContract(double notional, double strike) implements FxContract {

        public ForwardContract {
            if (strike <= 0) {
                throw new IllegalArgumentException("strike deve essere > 0");
            }
        }
    }

    /**
     * Future quotato: il guadagno si regola ogni giorno con il margine di
     * variazione.
     *
     * @param contracts numero di contratti, con segno (> 0 long, < 0 short)
     * @p
     * aram contractSize dimensione del contratto in valuta estera (125_000 per
     * CME 6E)
     * @param referencePrice prezzo d'ingresso (P&L dall'apertura) oppure
     * settlement del giorno precedente (variation margin di oggi)
     */
    public record FutureContract(int contracts, double contractSize, double referencePrice)
            implements FxContract {

        public FutureContract {
            if (contractSize <= 0) {
                throw new IllegalArgumentException("contractSize deve essere > 0");
            }
            if (referencePrice <= 0) {
                throw new IllegalArgumentException("referencePrice deve essere > 0");
            }
        }

        @Override
        public double notional() {
            return contracts * contractSize;
        }
    }
}
