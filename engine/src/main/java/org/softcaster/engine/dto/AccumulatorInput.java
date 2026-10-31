package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.GuaranteeType;
import org.softcaster.engine.enums.KnockInEffect;

/**
 * Accumulator / decumulator per AccumulatorPricer, indipendente dal
 * sottostante: il chiamante traduce il calendario in fixing futuri (forward e
 * fattori di sconto) e fixing passati non regolati.
 *
 * Valori dal punto di vista di chi detiene il contratto; per un contratto
 * venduto il chiamante cambia segno.
 *
 * @param spot              prezzo corrente del sottostante: le greche sono
 *                          rispetto a questo, con i forward che si muovono in
 *                          proporzione
 * @param guaranteedFixings fixing garantiti dall'inizio del contratto (il
 *                          chiamante traduce i periodi garantiti in fixing):
 *                          knock-out non attivo
 * @param guaranteeType     NONE (con 0 fixing garantiti), LEVERAGED (leva
 *                          normale), UNLEVERAGED (quantita' base su ogni
 *                          fixing garantito)
 * @param knockedIn         knock-in gia' avvenuto
 * @param fixings           fixing futuri in ordine di data, ciascuno con la
 *                          sua volatilita' (vuoto se il contratto e' finito o
 *                          uscito per knock-out)
 * @param pending           quantita' maturate e non ancora regolate
 * @param paths             numero di sentieri Monte Carlo (con antitetiche)
 * @param seed              seme del generatore: stesso seme, stesso risultato
 */
public record AccumulatorInput(
        String instrumentId,
        LocalDate valuationDate,
        AccumulatorType type,
        double strike,
        double koBarrier,
        BarrierObservation koObservation,
        Double kiBarrier,
        KnockInEffect kiEffect,
        double leverage,
        double dailyQuantity,
        int guaranteedFixings,
        GuaranteeType guaranteeType,
        boolean koAccrual,
        boolean knockedIn,
        double spot,
        List<AccumulatorFixingInput> fixings,
        List<AccumulatorPendingSettlement> pending,
        int paths,
        long seed) implements PricingInput {

    public static final int DEFAULT_PATHS = 50_000;
    public static final long DEFAULT_SEED = 20_261_029L;

    public AccumulatorInput {
        Objects.requireNonNull(valuationDate, "valuationDate");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(koObservation, "koObservation");
        fixings = List.copyOf(Objects.requireNonNull(fixings, "fixings"));
        pending = List.copyOf(Objects.requireNonNull(pending, "pending"));
        if (strike <= 0 || koBarrier <= 0 || spot <= 0) {
            throw new IllegalArgumentException("strike, koBarrier e spot devono essere > 0");
        }
        if (!type.isFavourable(koBarrier, strike)) {
            throw new IllegalArgumentException("barriera di knock-out dalla parte sbagliata dello strike");
        }
        if ((kiBarrier == null) != (kiEffect == null)) {
            throw new IllegalArgumentException("kiBarrier e kiEffect vanno indicati insieme");
        }
        if (kiBarrier != null && !type.isFavourable(strike, kiBarrier)) {
            throw new IllegalArgumentException("barriera di knock-in dalla parte sbagliata dello strike");
        }
        if (leverage < 1 || dailyQuantity <= 0 || guaranteedFixings < 0) {
            throw new IllegalArgumentException("leverage >= 1, dailyQuantity > 0, guaranteedFixings >= 0");
        }
        Objects.requireNonNull(guaranteeType, "guaranteeType");
        if ((guaranteeType == GuaranteeType.NONE) != (guaranteedFixings == 0)) {
            throw new IllegalArgumentException("guaranteeType NONE se e solo se non ci sono fixing garantiti");
        }
        if (paths < 2) {
            throw new IllegalArgumentException("paths deve essere >= 2");
        }
        for (int i = 1; i < fixings.size(); i++) {
            if (fixings.get(i).time() <= fixings.get(i - 1).time()) {
                throw new IllegalArgumentException("fixing non in ordine di data");
            }
        }
    }

    /**
     * Volatilita' media dei fixing futuri (0 se non ce ne sono).
     */
    public double averageVolatility() {
        return fixings.stream().mapToDouble(AccumulatorFixingInput::volatility).average().orElse(0.);
    }
}
