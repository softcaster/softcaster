/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardInput.ForwardContract;
import org.softcaster.engine.dto.FxForwardInput.FutureContract;
import org.softcaster.engine.dto.FxForwardResult;

/**
 * Pricer per FX forward e future su cambi (covered interest parity).
 *
 * Il cambio forward e' il prezzo relativo futuro delle due valute dopo aver
 * tenuto conto dei rendimenti nelle due valute:
 *
 * F = S * DF_foreign / DF_domestic
 *
 * equivalente alla forma classica F = S * (1 + r_d * t) / (1 + r_f * t),
 * perche' DF = 1 / (1 + r * t): il rapporto dei DF e' l'inverso di quello dei
 * tassi.
 *
 * I DF sono calcolati tra valuationDate (regolamento dello spot) e
 * deliveryDate, come rapporto DF(delivery) / DF(valuation): funziona sia con
 * curve che partono da oggi sia con curve che partono dalla data di regolamento
 * (DF(valuation) = 1).
 *
 * Con tassi deterministici il prezzo del future coincide con il forward;
 * l'aggiustamento di convessita' dovuto ai margini giornalieri non e'
 * considerato.
 */
public final class FxForwardPricer implements Pricer<FxForwardInput, FxForwardResult> {

    @Override
    public FxForwardResult calculate(FxForwardInput in) {
        double domesticDF = periodDiscountFactor(in.domesticCurve(), in);
        double foreignDF = periodDiscountFactor(in.foreignCurve(), in);

        double forward = in.spot() * foreignDF / domesticDF;

        return new FxForwardResult(
                in.instrumentId(),
                in.valuationDate(),
                forward,
                forward - in.spot(),
                domesticDF,
                foreignDF,
                presentValue(in, forward, domesticDF));
    }

    /**
     * Solo il cambio forward, per chi non ha bisogno del risultato completo
     * (es. pricer di opzioni su cambi che partono dal forward).
     * @param in
     * @return 
     */
    public double forwardRate(FxForwardInput in) {
        return in.spot() * periodDiscountFactor(in.foreignCurve(), in)
                / periodDiscountFactor(in.domesticCurve(), in);
    }

    /**
     * DF della curva tra valuationDate e deliveryDate.
     */
    private static double periodDiscountFactor(DiscountCurve curve, FxForwardInput in) {
        return curve.getDiscountFactor(in.deliveryDate()) / curve.getDiscountFactor(in.valuationDate());
    }

    /**
     * Valore della posizione in valuta domestica, o null se non c'e' un
     * contratto.
     *
     * Forward OTC: PV = notional * (F - strike) * DF_domestic (incasso alla
     * consegna) Future: PV = notional * (F - referencePrice) (regolato ogni
     * giorno, nessuno sconto)
     *
     * Scritto con instanceof per compilare anche con Java 17; con Java 21 si
     * puo' sostituire con uno switch sul tipo sealed.
     */
    private static Double presentValue(FxForwardInput in, double forward, double domesticDF) {
        var contract = in.contract();
        if (contract == null) {
            return null;
        }
        if (contract instanceof ForwardContract fwd) {
            return fwd.notional() * (forward - fwd.strike()) * domesticDF;
        }
        if (contract instanceof FutureContract fut) {
            return fut.notional() * (forward - fut.referencePrice());
        }
        throw new IllegalStateException("Tipo di contratto non gestito: " + contract.getClass().getSimpleName());
    }
}
