package org.softcaster.engine.analytics;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.CashFlowHelper;
import org.softcaster.engine.dto.CashFlowAnalytics;
import org.softcaster.engine.dto.CashFlowDetail;
import org.softcaster.engine.dto.LoanInput;
import org.softcaster.engine.dto.LoanResult;

/**
 * Mutui: fair value, debito residuo, rateo, TAEG, duration e DV01 a partire
 * dal piano delle rate (LoanScheduleBuilder).
 *
 * Fair value = valore attuale delle rate future (capitale + interessi; le
 * spese per rata sono recupero di costi e restano fuori), scontate con la
 * curva (DF(t)/DF(valutazione)) o con il tasso piatto, piu' lo spread di
 * credito continuo su ACT/365. DV01 = fair value con +1 punto base meno fair
 * value.
 *
 * TAEG (direttiva 2008/48/CE): tasso annuo i tale che il netto erogato
 * (capitale - spese all'erogazione) sia uguale alla somma delle rate e delle
 * spese per rata scontate con (1 + i)^-t, t in anni ACT/365 dall'erogazione.
 * Risolto per bisezione: la funzione e' monotona in i.
 */
public class LoanPricer extends AbstractFixedIncomePricer<LoanInput, LoanResult> {

    private static final double ONE_BP = 0.0001;

    @Override
    public LoanResult calculate(LoanInput in) {
        List<CashFlow> flows = in.schedule().flows();
        LocalDate v = in.valuationDate();
        List<CashFlow> future = CashFlowHelper.futureFlows(flows, v);

        double pv = presentValue(in, future, 0.);
        double dv01 = presentValue(in, future, ONE_BP) - pv;
        double modifiedDuration = pv > 0 ? -dv01 / pv / ONE_BP : 0.;

        double accrued = flows.stream()
                .filter(cf -> !v.isBefore(cf.accrualStart()) && v.isBefore(cf.accrualEnd()))
                .findFirst()
                .map(cf -> cf.interest() * ChronoUnit.DAYS.between(cf.accrualStart(), v)
                        / ChronoUnit.DAYS.between(cf.accrualStart(), cf.accrualEnd()))
                .orElse(0.);
        double paid = flows.stream().filter(cf -> !cf.paymentDate().isAfter(v)).mapToDouble(CashFlow::principal).sum();
        double outstanding = Math.max(in.principal() - paid, 0.);

        CashFlow next = future.isEmpty() ? null : future.get(0);
        double installment = flows.stream().filter(cf -> cf.principal() > 0)
                .findFirst().map(CashFlow::getTotalAmount).orElse(0.);
        double yield = future.isEmpty() ? 0. : internalRate(future, pv, v);

        List<CashFlowDetail> details = new ArrayList<>();
        for (CashFlow cf : future) {
            double df = discountFactor(in, cf.paymentDate(), 0.);
            details.add(new CashFlowDetail(cf.paymentDate(), cf.getTotalAmount(), df, cf.getTotalAmount() * df));
        }
        CashFlowAnalytics analytics = new CashFlowAnalytics(pv, accrued, yield, modifiedDuration, 0., dv01, details);

        double totalInterest = flows.stream().mapToDouble(CashFlow::interest).sum();
        double totalFees = in.upfrontFees() + in.installmentFees().stream().mapToDouble(Double::doubleValue).sum();

        return new LoanResult(in.instrumentId(), v, analytics, outstanding,
                next == null ? null : next.paymentDate(), next == null ? 0. : next.getTotalAmount(),
                installment, taeg(in), totalInterest, totalFees);
    }

    /**
     * TAEG: netto erogato = rate + spese per rata scontate al tasso annuo
     * composto, t ACT/365 dall'erogazione.
     */
    public double taeg(LoanInput in) {
        List<CashFlow> flows = in.schedule().flows();
        List<Double> fees = in.installmentFees();
        double net = in.principal() - in.upfrontFees();
        DoubleUnaryOperator npv = i -> {
            double sum = 0.;
            for (int k = 0; k < flows.size(); k++) {
                double t = ChronoUnit.DAYS.between(in.disbursementDate(), flows.get(k).paymentDate()) / 365.;
                sum += (flows.get(k).getTotalAmount() + fees.get(k)) * Math.pow(1 + i, -t);
            }
            return sum - net;
        };
        return bisection(npv);
    }

    private double presentValue(LoanInput in, List<CashFlow> future, double shift) {
        double pv = 0.;
        for (CashFlow cf : future) {
            pv += cf.getTotalAmount() * discountFactor(in, cf.paymentDate(), shift);
        }
        return pv;
    }

    private static double discountFactor(LoanInput in, LocalDate date, double shift) {
        double t = ChronoUnit.DAYS.between(in.valuationDate(), date) / 365.;
        double curve = in.discountCurve() != null
                ? in.discountCurve().getDiscountFactor(date) / in.discountCurve().getDiscountFactor(in.valuationDate())
                : Math.exp(-in.discountRate() * t);
        return curve * Math.exp(-(in.creditSpread() + shift) * t);
    }

    /**
     * Tasso annuo composto che rende il valore attuale dei flussi uguale a
     * price (t ACT/365 da base). Usato anche per il tasso interno al prezzo
     * di un deal (LoanCalculator.dealValuation).
     */
    public static double internalRate(List<CashFlow> flows, double price, LocalDate base) {
        DoubleUnaryOperator npv = i -> {
            double sum = 0.;
            for (CashFlow cf : flows) {
                sum += cf.getTotalAmount() * Math.pow(1 + i, -ChronoUnit.DAYS.between(base, cf.paymentDate()) / 365.);
            }
            return sum - price;
        };
        return bisection(npv);
    }

    /**
     * Zero di una funzione decrescente in [-0.99, 10].
     */
    private static double bisection(DoubleUnaryOperator f) {
        double lo = -0.99;
        double hi = 10.;
        if (f.applyAsDouble(lo) < 0 || f.applyAsDouble(hi) > 0) {
            throw new ArithmeticException("Rate outside the range [-99%, 1000%]");
        }
        for (int k = 0; k < 200 && hi - lo > 1e-12; k++) {
            double mid = 0.5 * (lo + hi);
            if (f.applyAsDouble(mid) > 0) {
                lo = mid;
            } else {
                hi = mid;
            }
        }
        return 0.5 * (lo + hi);
    }
}
