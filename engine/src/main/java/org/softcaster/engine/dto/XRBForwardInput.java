/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Record.java to edit this template
 */
package org.softcaster.engine.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.enums.DaycountBasis;

/**
 * Input per bond future (es. Mini BTP 10y) e bond forward OTC.
 *
 * Date: - valuationDate: data di regolamento a pronti dei titoli (oggi +
 * business days, T+2 per i BTP); - deliveryDate: data a cui si porta il titolo
 * a termine. Per un future regolato per cassa come il MBTP e' la data valuta
 * del prezzo del CTD rilevato a scadenza (expiry day + business days del
 * titolo); per un forward OTC e' la data di consegna.
 *
 * Repo: - repoCurve: curva di finanziamento del titolo (repo). Capitalizza il
 * prezzo a pronti e le cedole intermedie fino alla deliveryDate. Un tasso repo
 * singolo si passa come curva a un nodo (vedi BondForwardCalculator). -
 * repoDaycount: convenzione con cui esprimere i tassi in output (term repo e
 * implied repo), ACT/360 in EUR. Non entra nella capitalizzazione, che usa solo
 * i DF della curva.
 *
 * quotedFuturePrice e' facoltativo: se presente il pricer calcola anche gross
 * basis, net basis e implied repo, e sceglie il CTD come titolo con implied
 * repo massimo.
 */
public record XRBForwardInput(
        String instrumentId,
        LocalDate valuationDate,
        LocalDate deliveryDate,
        DiscountCurve repoCurve,
        DaycountBasis repoDaycount,
        List<DeliverableBond> deliverables,
        Double quotedFuturePrice)
        implements ForwardInput {

    public XRBForwardInput {
        Objects.requireNonNull(valuationDate, "valuationDate");
        Objects.requireNonNull(deliveryDate, "deliveryDate");
        Objects.requireNonNull(repoCurve, "repoCurve");
        Objects.requireNonNull(repoDaycount, "repoDaycount");
        Objects.requireNonNull(deliverables, "deliverables");
        if (!deliveryDate.isAfter(valuationDate)) {
            throw new IllegalArgumentException("deliveryDate (" + deliveryDate
                    + ") deve essere successiva alla valuationDate (" + valuationDate + ")");
        }
        if (deliverables.isEmpty()) {
            throw new IllegalArgumentException("Paniere consegnabile vuoto per " + instrumentId);
        }
        if (quotedFuturePrice != null && quotedFuturePrice <= 0) {
            throw new IllegalArgumentException("quotedFuturePrice deve essere > 0");
        }
        deliverables = List.copyOf(deliverables);
    }
}
