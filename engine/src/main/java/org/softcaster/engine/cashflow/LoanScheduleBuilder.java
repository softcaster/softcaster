package org.softcaster.engine.cashflow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

/**
 * Piano delle rate di un mutuo.
 *
 * Periodi: dal giorno di erogazione alla prima rata, poi a passo di frequenza
 * fino alla scadenza (ForwardScheduleGenerator). Se la prima rata non cade a
 * un periodo pieno dall'erogazione il primo periodo e' spezzato e diventa
 * preammortamento tecnico (soli interessi).
 *
 * Rate: i periodi di preammortamento (tecnico + preAmortizationPeriods) pagano
 * solo interessi sul capitale; sui periodi restanti si applica la strategia
 * del tipo di piano: SAS -> FrenchAmortizationStrategy (rata costante), SLP ->
 * StraightLineStrategy (quota capitale costante), IOL ->
 * BulletAmortizationStrategy (capitale alla scadenza).
 */
public class LoanScheduleBuilder {

    private final ForwardScheduleGenerator generator = new ForwardScheduleGenerator();

    /**
     * Periodi di maturazione e pagamento.
     *
     * @param firstPayment prima rata; null = erogazione + un periodo
     */
    public List<PaymentPeriod> periods(LocalDate disbursement, LocalDate firstPayment, LocalDate maturity,
            Frequency frequency, BusinessDayConvention bdc, DaycountBasis daycount, HolidayCalendar calendar) {
        Objects.requireNonNull(disbursement, "disbursement");
        Objects.requireNonNull(maturity, "maturity");
        LocalDate first = firstPayment != null ? firstPayment : frequency.offset(disbursement);
        if (!first.isAfter(disbursement) || first.isAfter(maturity)) {
            throw new IllegalArgumentException("Prima rata " + first + " fuori dal periodo " + disbursement + " - " + maturity);
        }
        // prima rata a un periodo pieno: date ancorate all'erogazione (31/10 -> 30/11, 31/12, ... 28/2, 31/3)
        if (first.equals(frequency.offset(disbursement))) {
            return generator.generate(disbursement, maturity, frequency, bdc, daycount, calendar);
        }
        List<PaymentPeriod> periods = new ArrayList<>();
        periods.add(new PaymentPeriod(disbursement, first, bdc.adjust(first, calendar),
                daycount.calculate(disbursement, first, frequency)));
        if (first.isBefore(maturity)) {
            periods.addAll(generator.generate(first, maturity, frequency, bdc, daycount, calendar));
        }
        return periods;
    }

    /**
     * Primo periodo spezzato: la prima rata non cade a un periodo pieno
     * dall'erogazione.
     */
    public static boolean hasStub(List<PaymentPeriod> periods, Frequency frequency) {
        PaymentPeriod p = periods.get(0);
        return !frequency.offset(p.accrualStart()).equals(p.accrualEnd());
    }

    /**
     * Rate del mutuo.
     *
     * @param annualRate            tasso annuo in decimale (0.032 = 3,2%)
     * @param preAmortizationPeriods rate contrattuali di soli interessi (oltre
     *                              all'eventuale primo periodo spezzato)
     */
    public List<CashFlow> cashFlows(double principal, double annualRate, AmortizationSchedule type,
            List<PaymentPeriod> periods, Frequency frequency, int preAmortizationPeriods, DaycountBasis daycount) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Capitale deve essere > 0");
        }
        int interestOnly = preAmortizationPeriods + (hasStub(periods, frequency) ? 1 : 0);
        if (interestOnly >= periods.size()) {
            throw new IllegalArgumentException("Preammortamento (" + interestOnly + " rate) non lascia rate di ammortamento su "
                    + periods.size());
        }
        List<CashFlow> flows = new ArrayList<>();
        for (PaymentPeriod p : periods.subList(0, interestOnly)) {
            flows.add(new CashFlow(p.accrualStart(), p.accrualEnd(), p.paymentDate(), 0.,
                    principal * annualRate * p.yearFraction(), principal, CashFlowStatus.RECORDED));
        }
        AmortizationStrategy strategy = switch (type) {
            case SAS -> new FrenchAmortizationStrategy();
            case SLP -> new StraightLineStrategy();
            case IOL -> new BulletAmortizationStrategy();
            case NONE -> throw new IllegalArgumentException("Tipo di piano mancante");
        };
        flows.addAll(strategy.generateCashFlows(principal, annualRate, periods.subList(interestOnly, periods.size()), daycount));
        return flows;
    }
}
