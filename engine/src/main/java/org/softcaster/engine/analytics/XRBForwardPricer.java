/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.CashFlowHelper;
import org.softcaster.engine.curve.DiscountCurve;

import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.dto.XRBForwardResult;
import org.softcaster.engine.dto.CashFlowSchedule;
import org.softcaster.engine.dto.DeliverableAnalysis;
import org.softcaster.engine.dto.DeliverableBond;
import org.softcaster.engine.enums.DaycountBasis;
// + import di CashFlow e CashFlowHelper dai rispettivi package

/**
 * Pricer per bond future (es. Mini BTP 10y) e bond forward OTC (paniere di un
 * titolo con CF = 1).
 *
 * Per ogni titolo del paniere, con P(d) = DF della curva repo alla data d:
 *
 * dirty spot = clean + rateo(valuationDate) dirty forward = dirty spot *
 * P(valuation) / P(delivery) cedole cap. = somma c_i * P(pagamento_i) /
 * P(delivery) clean forward = dirty forward - cedole cap. - rateo(deliveryDate)
 * prezzo teorico = clean forward / CF
 *
 * Il rapporto P(a)/P(b) funziona sia con curve che partono da oggi sia con
 * curve che partono dalla valuationDate.
 *
 * Il prezzo teorico del future e' quello del CTD: - senza prezzo quotato: il
 * titolo con prezzo teorico minimo (il piu' conveniente da consegnare); - con
 * prezzo quotato: il titolo con implied repo massimo (convenzione di mercato).
 *
 * Implied repo: per definizione un tasso unico a interesse semplice su
 * repoDaycount, soluzione esatta di dirty * (1 + R t) = invoice + somma c_i *
 * (1 + R t_i), invoice = F * CF + rateo(delivery): R = (invoice + somma c_i -
 * dirty) / (dirty * t - somma c_i * t_i)
 *
 * Non considerati: opzioni del venditore (il MBTP e' regolato per cassa),
 * aggiustamento di convessita' dei margini giornalieri, arrotondamento
 * dell'EDSP al tick, repo special del singolo titolo (la stessa curva vale per
 * tutto il paniere).
 */
public final class XRBForwardPricer implements Pricer<XRBForwardInput, XRBForwardResult> {

    @Override
    public XRBForwardResult calculate(XRBForwardInput in) {
        List<DeliverableAnalysis> analyses = in.deliverables().stream()
                .map(d -> analyse(d, in))
                .toList();

        DeliverableAnalysis ctd = selectCtd(analyses, in.quotedFuturePrice() != null);

        return new XRBForwardResult(
                in.instrumentId(),
                in.valuationDate(),
                in.deliveryDate(),
                ctd.theoreticalPrice(),
                termRepoRate(in),
                ctd.isin(),
                analyses);
    }

    /**
     * Tasso repo semplice implicito nella curva tra valuationDate e
     * deliveryDate.
     */
    static double termRepoRate(XRBForwardInput in) {
        DiscountCurve curve = in.repoCurve();
        double growth = curve.getDiscountFactor(in.valuationDate()) / curve.getDiscountFactor(in.deliveryDate());
        double t = in.repoDaycount().calculate(in.valuationDate(), in.deliveryDate(), null);
        return (growth - 1.0) / t;
    }

    /**
     * Analisi di un singolo titolo del paniere.
     */
    DeliverableAnalysis analyse(DeliverableBond d, XRBForwardInput in) {
        LocalDate valuation = in.valuationDate();
        LocalDate delivery = in.deliveryDate();
        DiscountCurve curve = in.repoCurve();
        DaycountBasis repoDc = in.repoDaycount();
        CashFlowSchedule s = d.schedule();

        double dfDelivery = curve.getDiscountFactor(delivery);
        double growth = curve.getDiscountFactor(valuation) / dfDelivery;
        double t = repoDc.calculate(valuation, delivery, null);   // solo per l'implied repo

        // stessa funzione di rateo usata per i bond (CashFlowHelper): a una data di stacco vale 0
        double accruedSpot = CashFlowHelper.calculateAccruedInterest(s.flows(), valuation, s.daycount(), s.frequency());
        double accruedDelivery = CashFlowHelper.calculateAccruedInterest(s.flows(), delivery, s.daycount(), s.frequency());

        // cedole pagate dopo il regolamento a pronti ed entro la data a termine (inclusa)
        double couponSum = 0.0;
        double couponTimeWeighted = 0.0;
        double couponsCapitalized = 0.0;
        for (CashFlow cf : s.flows()) {
            LocalDate pay = cf.paymentDate();
            if (pay.isAfter(valuation) && !pay.isAfter(delivery)) {
                couponsCapitalized += cf.interest() * curve.getDiscountFactor(pay) / dfDelivery;
                // per l'implied repo
                couponSum += cf.interest();
                couponTimeWeighted += cf.interest() * repoDc.calculate(pay, delivery, null);
            }
        }

        double dirtySpot = d.cleanPrice() + accruedSpot;
        double dirtyForward = dirtySpot * growth;
        double forwardClean = dirtyForward - couponsCapitalized - accruedDelivery;
        double theoretical = forwardClean / d.conversionFactor();

        Double grossBasis = null;
        Double netBasis = null;
        Double impliedRepo = null;
        Double futurePrice = in.quotedFuturePrice();
        if (futurePrice != null) {
            double adjustedFuture = futurePrice * d.conversionFactor();
            double invoice = adjustedFuture + accruedDelivery;
            grossBasis = d.cleanPrice() - adjustedFuture;
            netBasis = forwardClean - adjustedFuture;
            double denominator = dirtySpot * t - couponTimeWeighted;
            if (denominator > 0) {
                impliedRepo = (invoice + couponSum - dirtySpot) / denominator;
            }
        }

        return new DeliverableAnalysis(d.isin(), d.conversionFactor(), d.cleanPrice(),
                accruedSpot, accruedDelivery, couponsCapitalized, forwardClean, theoretical,
                grossBasis, netBasis, impliedRepo);
    }

    private static DeliverableAnalysis selectCtd(List<DeliverableAnalysis> analyses, boolean hasQuote) {
        if (hasQuote) {
            // implied repo massimo; i titoli senza implied repo calcolabile vengono esclusi
            return analyses.stream()
                    .filter(a -> a.impliedRepo() != null)
                    .max(Comparator.comparingDouble(DeliverableAnalysis::impliedRepo))
                    .orElseThrow(() -> new IllegalStateException("Implied repo non calcolabile per nessun titolo"));
        }
        return analyses.stream()
                .min(Comparator.comparingDouble(DeliverableAnalysis::theoreticalPrice))
                .orElseThrow();
    }
}
