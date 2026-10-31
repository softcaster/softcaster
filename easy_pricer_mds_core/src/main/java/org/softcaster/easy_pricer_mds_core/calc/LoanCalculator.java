package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.softcaster.core.data.AcDcBcyLink;
import org.softcaster.core.data.AcDcBcyLinkDAO;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.LoanPricingResponse;
import org.softcaster.engine.analytics.LoanPricer;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.cashflow.HolidayCalendar;
import org.softcaster.engine.cashflow.LoanScheduleBuilder;
import org.softcaster.engine.cashflow.PaymentPeriod;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.CashFlowSchedule;
import org.softcaster.engine.dto.LoanInput;
import org.softcaster.engine.dto.LoanResult;
import org.softcaster.engine.enums.BusinessDayConvention;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.engine.enums.CurveRole;
import org.softcaster.engine.enums.RollConvention;

/**
 * Mutui: piano delle rate dall'anagrafica (LoanScheduleBuilder) e input di
 * LoanPricer con i dati di mercato.
 *
 * Tasso: interest_rate dell'anagrafica (punti percentuali). Per i mutui a
 * tasso variabile e' il tasso corrente applicato a tutte le rate future
 * (proiezione LAST_COUPON); la proiezione sulla curva dell'indice e' da fare.
 *
 * Sconto: curva indicata, altrimenti la curva DISCOUNT di ac_dc_bcy_link per
 * (asset class, valuta), altrimenti un tasso piatto indicato; piu' lo spread
 * di credito.
 *
 * Creato come @Bean in MdsCoreAutoConfiguration.
 */
public class LoanCalculator {

    private final AcDcBcyLinkDAO acDcBcyLinkDAO;
    private final YieldCurveEntityDAO yieldCurveEntityDAO;
    private final SpreadCurveEntityDAO spreadCurveEntityDAO;
    private final MarketDataService marketDataService;
    private final LoanPricer loanPricer;
    private final LoanScheduleBuilder scheduleBuilder = new LoanScheduleBuilder();

    public LoanCalculator(AcDcBcyLinkDAO acDcBcyLinkDAO, YieldCurveEntityDAO yieldCurveEntityDAO,
            SpreadCurveEntityDAO spreadCurveEntityDAO, MarketDataService marketDataService, LoanPricer loanPricer) {
        this.acDcBcyLinkDAO = Objects.requireNonNull(acDcBcyLinkDAO);
        this.yieldCurveEntityDAO = Objects.requireNonNull(yieldCurveEntityDAO);
        this.spreadCurveEntityDAO = Objects.requireNonNull(spreadCurveEntityDAO);
        this.marketDataService = Objects.requireNonNull(marketDataService);
        this.loanPricer = Objects.requireNonNull(loanPricer);
    }

    // ------------------------------------------------------------------------
    // -- Piano delle rate
    // ------------------------------------------------------------------------

    /**
     * Piano dai termini dell'anagrafica: erogazione (issue_date), prima rata,
     * scadenza, frequenza, convenzione (roll_convention), daycount,
     * preammortamento, tipo di piano.
     */
    public List<CashFlow> generateSchedule(LoanMasterData lmd, HolidayCalendar calendar) {
        Objects.requireNonNull(lmd, "lmd");
        RollConvention roll = lmd.getRollConvention() != null ? lmd.getRollConvention() : RollConvention.UNADJUSTED;
        List<PaymentPeriod> periods = scheduleBuilder.periods(
                lmd.getIssueDate().toLocalDate(), lmd.getFirstPaymentLocalDate(), lmd.getMaturityDate().toLocalDate(),
                lmd.getFrequency(), BusinessDayConvention.fromId(roll.getId()), lmd.getDaycount(), calendar);
        return scheduleBuilder.cashFlows(lmd.getPrincipalAmount(), lmd.getInterestRate() / 100., lmd.getAmortizationSchedule(),
                periods, lmd.getFrequency(), lmd.getPreAmortizationPeriods(), lmd.getDaycount());
    }

    /**
     * Righe di cash_flow_item: amount = quota capitale, interest = interessi.
     */
    public static List<CashFlowItem> toItems(List<CashFlow> flows) {
        List<CashFlowItem> items = new ArrayList<>();
        for (CashFlow cf : flows) {
            CashFlowItem item = new CashFlowItem();
            item.setStartDate(java.sql.Date.valueOf(cf.accrualStart()));
            item.setEndDate(java.sql.Date.valueOf(cf.accrualEnd()));
            item.setAmount(cf.principal());
            item.setInterest(cf.interest());
            item.setCashFlowStatus(CashFlowStatus.RECORDED);
            items.add(item);
        }
        return items;
    }

    /**
     * Rate dall'anagrafica salvata (cash_flow_item), con il residuo
     * ricostruito; la data di pagamento e' la fine del periodo.
     */
    public static List<CashFlow> fromItems(LoanMasterData lmd) {
        double outstanding = lmd.getPrincipalAmount();
        List<CashFlow> flows = new ArrayList<>();
        List<CashFlowItem> items = new ArrayList<>(lmd.getCashFlows());
        items.sort((a, b) -> a.getEnddate().compareTo(b.getEnddate()));
        for (CashFlowItem item : items) {
            outstanding -= item.getAmount();
            flows.add(new CashFlow(item.getStartDate().toLocalDate(), item.getEnddate().toLocalDate(),
                    item.getEnddate().toLocalDate(), item.getAmount(), item.getInterest(), Math.max(outstanding, 0.),
                    item.getCashFlowStatus()));
        }
        return flows;
    }

    // ------------------------------------------------------------------------
    // -- Valutazione
    // ------------------------------------------------------------------------

    /**
     * @param flows             rate (generate o lette dall'anagrafica)
     * @param discountCurveCode curva di sconto; vuota = DISCOUNT di ac_dc_bcy_link
     * @param discountRate      tasso piatto continuo in decimale, se non c'e' curva
     * @param creditSpread      spread di credito continuo in decimale (null = 0)
     */
    public LoanInput getLoanInput(LoanMasterData lmd, List<CashFlow> flows, LocalDate valuationDate,
            String discountCurveCode, Double discountRate, Double creditSpread) {
        DiscountCurve curve = null;
        double rate = 0.;
        String code = blank(discountCurveCode) ? linkedCurveCode(lmd) : discountCurveCode;
        if (code != null) {
            curve = loadCurve(code, valuationDate);
        } else if (discountRate != null) {
            rate = discountRate;
        } else {
            throw new IllegalArgumentException("Missing discount curve (discountCurve) or rate (discountRate) and no "
                    + "DISCOUNT curve configured for " + lmd.getAssetClass().getCode() + "/" + lmd.getCurrency().getIsoCode());
        }
        List<Double> fees = new ArrayList<>();
        double outstanding = lmd.getPrincipalAmount();
        for (CashFlow cf : flows) {
            fees.add(lmd.installmentFees(outstanding));
            outstanding -= cf.principal();
        }
        return new LoanInput(lmd.getCode(), valuationDate, lmd.getPrincipalAmount(), lmd.getIssueDate().toLocalDate(),
                lmd.getInterestRate() / 100., new CashFlowSchedule(flows, lmd.getDaycount(), lmd.getFrequency(), lmd.getCompounding()),
                lmd.upfrontFees(), fees, curve, rate, creditSpread == null ? 0. : creditSpread);
    }

    public LoanResult valuation(LoanInput input) {
        return loanPricer.calculate(Objects.requireNonNull(input, "input"));
    }

    public LoanPricingResponse loanValuation(LoanMasterData lmd, List<CashFlow> flows, LocalDate valuationDate,
            String discountCurveCode, Double discountRate, Double creditSpread) {
        LocalDate date = valuationDate != null ? valuationDate : marketDataService.getOfficialDate();
        LoanInput input = getLoanInput(lmd, flows, date, discountCurveCode, discountRate, creditSpread);
        LoanResult r = valuation(input);

        LoanPricingResponse response = new LoanPricingResponse();
        response.code = lmd.getCode();
        response.valuationDate = date;
        response.discountSource = input.discountCurve() != null
                ? (blank(discountCurveCode) ? linkedCurveCode(lmd) : discountCurveCode) : "flat " + input.discountRate();
        response.principal = lmd.getPrincipalAmount();
        response.annualRate = lmd.getInterestRate();
        response.installment = r.installment();
        response.outstandingPrincipal = r.outstandingPrincipal();
        response.accruedInterest = r.analytics().accruedInterest();
        response.nextPaymentDate = r.nextPaymentDate();
        response.nextPaymentAmount = r.nextPaymentAmount();
        response.fairValue = r.analytics().dirtyPrice();
        response.fairValuePercent = r.outstandingPrincipal() > 0 ? 100. * r.analytics().dirtyPrice() / r.outstandingPrincipal() : 0.;
        response.yield = 100. * r.analytics().yield();
        response.modifiedDuration = r.analytics().modifiedDuration();
        response.dv01 = r.analytics().dv01();
        response.taeg = 100. * r.taeg();
        response.upfrontFees = input.upfrontFees();
        response.totalInterest = r.totalInterest();
        response.totalFees = r.totalFees();
        response.schedule = new ArrayList<>();
        for (int k = 0; k < flows.size(); k++) {
            CashFlow cf = flows.get(k);
            double fee = input.installmentFees().get(k);
            response.schedule.add(new LoanPricingResponse.Installment(k + 1, cf.accrualStart(), cf.accrualEnd(), cf.paymentDate(),
                    cf.principal(), cf.interest(), fee, cf.getTotalAmount() + fee, cf.outstandingBalance()));
        }
        return response;
    }

    // ------------------------------------------------------------------------
    // -- Helper privati
    // ------------------------------------------------------------------------

    private String linkedCurveCode(LoanMasterData lmd) {
        return acDcBcyLinkDAO.find(lmd.getAssetClass().getIdAssetClass(), lmd.getCurrency().getIdCurrency(), CurveRole.DISCOUNT)
                .map(this::curveCode)
                .orElse(null);
    }

    private String curveCode(AcDcBcyLink link) {
        return link.isSpreadCurve()
                ? spreadCurveEntityDAO.findByIdWithBase(link.getSpreadCurve()).getCode()
                : yieldCurveEntityDAO.findByIdYieldCurve(link.getCurveId()).getCode();
    }

    private DiscountCurve loadCurve(String curveCode, LocalDate valuationDate) {
        DiscountCurve curve = marketDataService.getDiscountCurve(curveCode);
        if (curve == null) {
            throw new NoSuchElementException("Curve not found: " + curveCode);
        }
        if (curve.getValuationDate().isAfter(valuationDate)) {
            throw new IllegalArgumentException("Curve " + curveCode + " is dated " + curve.getValuationDate()
                    + ", after valuationDate " + valuationDate);
        }
        return curve;
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
