package org.softcaster.engine.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.HolidayCalendar;
import org.softcaster.engine.cashflow.LoanScheduleBuilder;
import org.softcaster.engine.cashflow.PaymentPeriod;
import org.softcaster.engine.dto.CashFlowSchedule;
import org.softcaster.engine.dto.LoanInput;
import org.softcaster.engine.dto.LoanResult;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.Frequency;

/**
 * Mutuo di 200.000 al 3,2% in 20 anni, rate mensili, 30/360: piano francese
 * confrontato con la formula della rata costante, piano italiano, bullet,
 * preammortamento, primo periodo spezzato e TAEG.
 */
class LoanPricerTest {

    private static final LocalDate DISBURSEMENT = LocalDate.of(2026, 11, 2);
    private static final LocalDate MATURITY = LocalDate.of(2046, 11, 2);
    private static final double P = 200_000.;
    private static final double RATE = 0.032;
    private static final double I = RATE / 12;
    private static final HolidayCalendar NO_HOLIDAYS = d -> false;

    private final LoanScheduleBuilder builder = new LoanScheduleBuilder();
    private final LoanPricer pricer = new LoanPricer();

    private List<CashFlow> flows(AmortizationSchedule type, LocalDate firstPayment, int preAmortization) {
        List<PaymentPeriod> periods = builder.periods(DISBURSEMENT, firstPayment, MATURITY, Frequency.MONTHLY,
                BusinessDayConvention.UNADJUSTED, DaycountBasis.EUR_30_360, NO_HOLIDAYS);
        return builder.cashFlows(P, RATE, type, periods, Frequency.MONTHLY, preAmortization, DaycountBasis.EUR_30_360);
    }

    private LoanInput input(List<CashFlow> flows, double upfrontFees, double feePerInstallment, LocalDate valuation, double discountRate) {
        return new LoanInput("TEST", valuation, P, DISBURSEMENT, RATE,
                new CashFlowSchedule(flows, DaycountBasis.EUR_30_360, Frequency.MONTHLY, Compounding.COMPOUNDED),
                upfrontFees, Collections.nCopies(flows.size(), feePerInstallment), null, discountRate, 0.);
    }

    @Test
    void frenchInstallmentMatchesAnnuityFormula() {
        List<CashFlow> f = flows(AmortizationSchedule.SAS, null, 0);
        double expected = P * I / (1 - Math.pow(1 + I, -240));      // 1.129,62
        assertEquals(240, f.size());
        assertEquals(P * I, f.get(0).interest(), 1e-6);              // 533,33
        for (CashFlow cf : f.subList(0, 239)) {
            assertEquals(expected, cf.getTotalAmount(), 1e-6);
        }
        assertEquals(P, f.stream().mapToDouble(CashFlow::principal).sum(), 1e-6);
        assertEquals(0., f.get(239).outstandingBalance(), 1e-6);
    }

    @Test
    void straightLineHasConstantPrincipal() {
        List<CashFlow> f = flows(AmortizationSchedule.SLP, null, 0);
        assertEquals(P / 240, f.get(0).principal(), 1e-9);
        assertEquals(P * I, f.get(0).interest(), 1e-6);
        assertEquals(P / 240 * I, f.get(239).interest(), 1e-6);      // ultima: interessi sull'ultima quota
    }

    @Test
    void preAmortizationPaysInterestOnly() {
        List<CashFlow> f = flows(AmortizationSchedule.SAS, null, 2);
        assertEquals(240, f.size());
        assertEquals(0., f.get(0).principal(), 0.);
        assertEquals(P * I, f.get(1).interest(), 1e-6);
        double expected = P * I / (1 - Math.pow(1 + I, -238));       // ammortamento sulle 238 restanti
        assertEquals(expected, f.get(2).getTotalAmount(), 1e-6);
    }

    @Test
    void shortFirstPeriodIsTechnicalPreAmortization() {
        // erogazione 2/11, prima rata 30/11: 28 giorni (30/360) di soli interessi, poi 240 rate piene
        List<CashFlow> f = flows(AmortizationSchedule.SAS, LocalDate.of(2026, 11, 30), 0);
        assertEquals(0., f.get(0).principal(), 0.);
        assertEquals(P * RATE * 28 / 360, f.get(0).interest(), 1e-6);
        assertTrue(f.get(1).principal() > 0);
    }

    @Test
    void endOfMonthDisbursementKeepsRegularInstallments() {
        // erogazione 31/10: rate 30/11, 31/12, 31/1, 28/2, 31/3 ... sempre a fine mese, rata costante
        LocalDate disbursement = LocalDate.of(2026, 10, 31);
        List<PaymentPeriod> periods = builder.periods(disbursement, null, LocalDate.of(2046, 10, 31), Frequency.MONTHLY,
                BusinessDayConvention.UNADJUSTED, DaycountBasis.EUR_30_360, NO_HOLIDAYS);
        List<CashFlow> f = builder.cashFlows(P, RATE, AmortizationSchedule.SAS, periods, Frequency.MONTHLY, 0, DaycountBasis.EUR_30_360);
        assertEquals(240, f.size());
        assertEquals(LocalDate.of(2027, 2, 28), f.get(3).paymentDate());
        assertEquals(LocalDate.of(2027, 3, 31), f.get(4).paymentDate());
        double expected = P * I / (1 - Math.pow(1 + I, -240));
        assertEquals(expected, f.get(0).getTotalAmount(), 1e-6);
        assertEquals(expected, f.get(100).getTotalAmount(), 1e-6);
    }

    @Test
    void taegWithoutFeesIsEffectiveRateAndFeesRaiseIt() {
        List<CashFlow> f = flows(AmortizationSchedule.SAS, null, 0);
        double effective = Math.pow(1 + I, 12) - 1;                  // 3,2482%
        double noFees = pricer.taeg(input(f, 0., 0., DISBURSEMENT, 0.03));
        assertEquals(effective, noFees, 0.0003);                     // ACT/365 contro mesi: pochi decimi di bp
        double withFees = pricer.taeg(input(f, 2_000., 2.5, DISBURSEMENT, 0.03));
        assertTrue(withFees > noFees + 0.001, withFees + " > " + noFees);
    }

    @Test
    void fairValueAtContractRateIsPrincipal() {
        List<CashFlow> f = flows(AmortizationSchedule.SAS, null, 0);
        // tasso continuo equivalente al 3,2% mensile composto
        double continuous = 12 * Math.log(1 + I);
        LoanResult r = pricer.calculate(input(f, 0., 0., DISBURSEMENT, continuous));
        assertEquals(P, r.analytics().dirtyPrice(), P * 0.002);
        assertTrue(r.analytics().dv01() < 0);
        assertEquals(P, r.outstandingPrincipal(), 1e-6);
        assertEquals(LocalDate.of(2026, 12, 2), r.nextPaymentDate());
    }

    @Test
    void midLifeValuation() {
        List<CashFlow> f = flows(AmortizationSchedule.SAS, null, 0);
        // dopo 12 rate pagate, a meta' del periodo successivo
        LoanResult r = pricer.calculate(input(f, 0., 0., LocalDate.of(2027, 11, 17), 0.03));
        double paid = f.subList(0, 12).stream().mapToDouble(CashFlow::principal).sum();
        assertEquals(P - paid, r.outstandingPrincipal(), 1e-6);
        assertEquals(f.get(12).interest() * 15 / 30, r.analytics().accruedInterest(), 1e-6);
        assertEquals(228, r.analytics().flows().size());
    }
}
