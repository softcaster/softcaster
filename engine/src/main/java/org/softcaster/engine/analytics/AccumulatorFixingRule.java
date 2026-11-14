package org.softcaster.engine.analytics;

import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.KnockInEffect;

/**
 * Regola di un singolo fixing di un accumulator, condivisa dal pricer
 * (AccumulatorPricer, sui sentieri simulati) e dal ciclo di vita (fixing
 * rilevati alla chiusura): stesso contratto, stesse quantita'.
 */
public final class AccumulatorFixingRule {

    private AccumulatorFixingRule() {
    }

    /**
     * Il fixing di indice index osserva il knock-out: non nei fixing garantiti;
     * se la barriera si osserva al regolamento, solo all'ultimo fixing del
     * periodo.
     */
    public static boolean koObserved(int index, int guaranteedFixings, BarrierObservation observation, boolean periodEnd) {
        return index >= guaranteedFixings && (observation != BarrierObservation.SETTLEMENT || periodEnd);
    }

    /**
     * Quantita' maturata da un fixing al prezzo spot: dailyQuantity nei fixing
     * favorevoli, dailyQuantity x leverage negli sfavorevoli (leva 1 prima del
     * knock-in LEVERAGE, nulla prima del knock-in CONTRACT); nei fixing
     * garantiti senza leva (baseOnly) sempre dailyQuantity. Positiva: il verso
     * (compra / vende) e' quello del tipo di contratto.
     */
    public static double quantity(AccumulatorType type, double strike, double dailyQuantity, double leverage,
            KnockInEffect kiEffect, boolean knockedIn, boolean baseOnly, double spot) {
        if (baseOnly) {
            return dailyQuantity;
        }
        if (kiEffect == KnockInEffect.CONTRACT && !knockedIn) {
            return 0.;
        }
        if (type.isFavourable(spot, strike)) {
            return dailyQuantity;
        }
        boolean leveraged = kiEffect != KnockInEffect.LEVERAGE || knockedIn;
        return dailyQuantity * (leveraged ? leverage : 1.);
    }
}
