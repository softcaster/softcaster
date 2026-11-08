package org.softcaster.easy_pricer_srv.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.hibernate.Hibernate;
import org.softcaster.core.data.AssetClassDAO;
import org.softcaster.core.data.Currency;
import org.softcaster.core.data.CurrencyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FltLoanMasterData;
import org.softcaster.core.data.FloatingRateTerms;
import org.softcaster.core.data.LoanFee;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.LoanMasterDataDAO;
import org.softcaster.core.data.RefRateIndexDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.LoanCalculator;
import org.softcaster.easy_pricer_mds_core.calc.RateFixing;
import org.softcaster.engine.enums.CashFlowStatus;
import org.softcaster.easy_pricer_mds_core.dto.LoanPricingResponse;
import org.softcaster.easy_pricer_srv.dto.LoanCashFlowDto;
import org.softcaster.easy_pricer_srv.dto.LoanDealRequest;
import org.softcaster.easy_pricer_srv.dto.LoanDto;
import org.softcaster.engine.cashflow.CashFlow;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.AccrualScheduleType;
import org.softcaster.engine.enums.AmortizationSchedule;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CouponProjectionMethod;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.FeeBasis;
import org.softcaster.engine.enums.FeeTiming;
import org.softcaster.engine.enums.FixingDayType;
import org.softcaster.engine.enums.Form;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.LoanFeeType;
import org.softcaster.engine.enums.RollConvention;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.softcaster.engine.enums.TypeOfInterest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Deal capture dei mutui OTC: dai termini si costruiscono anagrafica, spese e
 * piano delle rate, che si simulano (price) o si registrano insieme alla
 * transazione (book).
 *
 * Transazione: quantita' = debito residuo alla data operazione (il capitale
 * all'erogazione), prezzo = 100 (percentuale del residuo, come per i titoli);
 * BUY = la banca presta, SELL = la banca prende a prestito.
 */
@Service
public class LoanDealService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private LoanMasterDataDAO loanDAO;
    @Autowired
    private AssetClassDAO assetClassDAO;
    @Autowired
    private CurrencyDAO currencyDAO;
    @Autowired
    private RefRateIndexDAO refRateIndexDAO;
    @Autowired
    private LoanCalculator loanCalculator;
    @Autowired
    private org.softcaster.core.data.SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private FinancialTxnService financialTxnService;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Piano e valutazione dei termini, senza salvare nulla.
     */
    public LoanPricingResponse price(LoanDealRequest r) {
        return transactionTemplate.execute(status -> {
            status.setRollbackOnly();
            LoanMasterData lmd = build(r);
            List<CashFlow> flows = loanCalculator.generateSchedule(lmd, calendar(lmd));
            return loanCalculator.loanValuation(lmd, flows, valuationDate(r), r.discountCurve, r.discountRate, r.creditSpread);
        });
    }

    /**
     * Registra anagrafica, spese, piano e transazione (PENDING); restituisce
     * il mutuo e, se i dati di sconto ci sono, la valutazione.
     */
    public LoanPricingResponse book(LoanDealRequest r) {
        return transactionTemplate.execute(status -> {
            TxnSide side = TxnSide.fromCode(require(r.side, "side"));
            require(r.counterpartyId, "counterpartyId");
            require(r.positionMdId, "positionMdId");

            LoanMasterData lmd = build(r);
            List<CashFlow> flows = loanCalculator.generateSchedule(lmd, calendar(lmd));
            lmd.getCashFlows().addAll(LoanCalculator.toItems(flows));
            markPeriods(lmd);
            lmd = loanDAO.create(lmd);

            LocalDate tradeDate = tradeDate(r, lmd);
            FinancialTxnDto txn = new FinancialTxnDto(0, lmd.getDescription(),
                    r.counterpartyId, null, null,
                    r.positionMdId, null,
                    lmd.getIdMasterData(), lmd.getCode(), lmd.getDescription(),
                    TxnStatus.PENDING.getId(), null, null,
                    null, (short) side.getId(),
                    java.sql.Date.valueOf(tradeDate), null,
                    dealQuantity(lmd, tradeDate), 100., null, AccountingPhase.NONE.getId());
            financialTxnService.saveOrUpdateTransaction(txn);

            return loanCalculator.loanValuation(lmd, flows, valuationDate(r), r.discountCurve, r.discountRate, r.creditSpread);
        });
    }

    /**
     * Modifica di un deal registrato: termini, spese e piano dell'anagrafica
     * (stesso codice) e transazione (PENDING aggiornata, EXECUTED -> TO_AMEND
     * con storno e nuova transazione). Il tipo di tasso non cambia.
     */
    public LoanDto amend(Integer txnId, LoanDealRequest r) {
        return transactionTemplate.execute(status -> {
            TxnSide side = TxnSide.fromCode(require(r.side, "side"));
            require(r.counterpartyId, "counterpartyId");
            require(r.positionMdId, "positionMdId");
            FinancialTxn txn = financialTxnService.findAmendable(txnId);
            if (!(Hibernate.unproxy(txn.getMasterData()) instanceof LoanMasterData)) {
                throw new IllegalArgumentException("Trade " + txnId + " is not a loan");
            }
            // la transazione puo' aver gia' caricato l'anagrafica come proxy
            LoanMasterData lmd = (LoanMasterData) Hibernate.unproxy(loanDAO.getById(txn.getMasterData().getIdMasterData()));
            boolean floating = "FLOATING".equals(require(r.rateType, "rateType"));
            if (floating != (lmd instanceof FltLoanMasterData)) {
                throw new IllegalArgumentException("The rate type of " + lmd.getCode() + " cannot change");
            }
            // descrizione generata dai vecchi termini: si rigenera
            String oldDescription = lmd.getDescription();
            if (oldDescription.equals(r.description) && oldDescription.equals(description(lmd, lmd.getAmortizationSchedule()))) {
                r.description = null;
            }

            // spese e piano si sostituiscono: prima le cancellazioni (spesa univoca per tipo)
            lmd.getFees().clear();
            lmd.getCashFlows().clear();
            entityManager.flush();
            fill(lmd, r);
            lmd.getCashFlows().addAll(LoanCalculator.toItems(loanCalculator.generateSchedule(lmd, calendar(lmd))));
            markPeriods(lmd);
            entityManager.flush();

            LocalDate tradeDate = tradeDate(r, lmd);
            financialTxnService.amendOtcDeal(txn, lmd.getDescription(), side, r.counterpartyId, r.positionMdId,
                    tradeDate, dealQuantity(lmd, tradeDate), 100.);
            return LoanDto.of(lmd);
        });
    }

    /**
     * Valutazione di un mutuo registrato sul suo piano salvato.
     */
    public LoanPricingResponse value(String code, LoanDealRequest pricing) {
        return transactionTemplate.execute(status -> {
            LoanMasterData lmd = loanDAO.getByCode(code);
            return loanCalculator.loanValuation(lmd, LoanCalculator.fromItems(lmd), pricing.valuationDate,
                    pricing.discountCurve, pricing.discountRate, pricing.creditSpread);
        });
    }

    /**
     * Piano salvato di un mutuo registrato (cash_flow_item), in ordine di data.
     */
    public List<LoanCashFlowDto> cashFlows(String code) {
        return transactionTemplate.execute(status -> {
            List<CashFlow> flows = LoanCalculator.fromItems(loanDAO.getByCode(code));
            return java.util.stream.IntStream.range(0, flows.size())
                    .mapToObj(i -> LoanCashFlowDto.of(i + 1, flows.get(i))).toList();
        });
    }

    public List<LoanDto> findAll() {
        return transactionTemplate.execute(status -> loanDAO.findAll().stream().map(LoanDto::of).toList());
    }

    public LoanDto findByCode(String code) {
        return transactionTemplate.execute(status -> LoanDto.of(loanDAO.getByCode(code)));
    }

    // ------------------------------------------------------------------------
    // -- Costruzione del mutuo
    // ------------------------------------------------------------------------

    private LoanMasterData build(LoanDealRequest r) {
        boolean floating = "FLOATING".equals(require(r.rateType, "rateType"));
        LoanMasterData lmd = floating ? new FltLoanMasterData() : new LoanMasterData();
        lmd.setAssetClass(assetClassDAO.findByCode(floating ? LoanMasterData.FLOATING_ASSET_CLASS : LoanMasterData.FIXED_ASSET_CLASS));
        fill(lmd, r);
        return lmd;
    }

    /**
     * Termini e spese della richiesta su un mutuo nuovo o registrato (le
     * spese si aggiungono: per un mutuo registrato vanno tolte prima).
     */
    private void fill(LoanMasterData lmd, LoanDealRequest r) {
        boolean floating = lmd instanceof FltLoanMasterData;
        Currency currency = currencyDAO.findByIsoCode(require(r.currency, "currency"));
        if (currency == null) {
            throw new NoSuchElementException("Currency not found: " + r.currency);
        }
        lmd.setCurrency(currency);

        lmd.setPrincipalAmount(require(r.principalAmount, "principalAmount"));
        lmd.setInterestRate(require(r.annualRate, "annualRate"));
        LocalDate disbursement = require(r.disbursementDate, "disbursementDate");
        LocalDate maturity = require(r.maturityDate, "maturityDate");
        if (!maturity.isAfter(disbursement)) {
            throw new IllegalArgumentException("Maturity " + maturity + " is not after disbursement " + disbursement);
        }
        lmd.setIssueDate(java.sql.Date.valueOf(disbursement));
        lmd.setMaturityDate(java.sql.Date.valueOf(maturity));
        lmd.setFirstPaymentDate(r.firstPaymentDate == null ? null : java.sql.Date.valueOf(r.firstPaymentDate));
        Frequency frequency = Frequency.fromCode(require(r.frequency, "frequency"));
        if (!frequency.isCouponFrequency()) {
            throw new IllegalArgumentException("Installment frequency " + r.frequency + " not allowed");
        }
        lmd.setFrequency(frequency);
        AmortizationSchedule amortization = AmortizationSchedule.fromCode(require(r.amortization, "amortization"));
        lmd.setAmortizationSchedule(amortization);
        DaycountBasis daycount = DaycountBasis.fromCode(require(r.daycount, "daycount"));
        lmd.setDaycount(daycount);
        lmd.setAccrualDaycount(daycount);
        lmd.setRollConvention(RollConvention.fromCode(require(r.rollConvention, "rollConvention")));
        lmd.setPreAmortizationPeriods(r.preAmortizationPeriods == null ? 0 : r.preAmortizationPeriods);
        lmd.setTypeOfInterest(floating ? TypeOfInterest.FLOATING : TypeOfInterest.FIXED);
        // contratto nominativo (NONE non e' nella tabella form)
        lmd.setForm(Form.REGISTERED);
        lmd.setAccrualScheduleType(AccrualScheduleType.NONE);
        lmd.setCompounding(Compounding.COMPOUNDED);
        lmd.setIssuePrice(100.);
        lmd.setRedempionPrice(100.);
        lmd.setBusinessDays(0);
        lmd.setMultiplier(1.);
        lmd.setDefaultInterest(r.defaultInterest == null ? 0. : r.defaultInterest);
        lmd.setLatePaymentFee(r.latePaymentFee == null ? 0. : r.latePaymentFee);

        if (r.fees != null) {
            for (LoanDealRequest.Fee f : r.fees) {
                LoanFee fee = new LoanFee(LoanFeeType.fromCode(require(f.type, "fees.type")),
                        FeeBasis.fromCode(require(f.basis, "fees.basis")), FeeTiming.fromCode(require(f.timing, "fees.timing")),
                        require(f.value, "fees.value"));
                if (!lmd.getFees().add(fee)) {
                    throw new IllegalArgumentException("Fee " + f.type + " given twice");
                }
            }
        }

        if (lmd instanceof FltLoanMasterData flt) {
            FloatingRateTerms terms = flt.getFloatingTerms();
            terms.setSpread(require(r.spread, "spread"));
            terms.setFloor(r.floor);
            terms.setCap(r.cap);
            if (r.floor != null && r.cap != null && r.floor > r.cap) {
                throw new IllegalArgumentException("Floor above cap");
            }
            terms.setIndexMonths(r.indexMonths == null ? 6 : r.indexMonths);
            terms.setFixingLagDays(r.fixingLagDays == null ? 2 : r.fixingLagDays);
            terms.setFixingDayType(FixingDayType.BUSINESS_DAYS);
            terms.setCouponPm(CouponProjectionMethod.LAST_COUPON);
            terms.setProjectionIndex(r.indexCode == null || r.indexCode.isBlank() ? null
                    : refRateIndexDAO.findAll().stream()
                            .filter(i -> r.indexCode.equals(i.getCode())).findFirst()
                            .orElseThrow(() -> new NoSuchElementException("Index not found: " + r.indexCode)));
        }

        lmd.setDescription(r.description != null && !r.description.isBlank() ? r.description : description(lmd, amortization));
    }

    private static String description(LoanMasterData lmd, AmortizationSchedule amortization) {
        String plan = switch (amortization) {
            case SAS -> "French";
            case SLP -> "Italian";
            case IOL -> "bullet";
            case NONE -> "";
        };
        return (lmd instanceof FltLoanMasterData ? "Floating " : "Fixed ") + "loan " + plan + " "
                + String.format("%,.0f", lmd.getPrincipalAmount()) + " " + lmd.getCurrency().getIsoCode()
                + " " + lmd.getInterestRate() + "% " + lmd.getIssueDate() + "/" + lmd.getMaturityDate();
    }

    /**
     * Stato delle rate di un piano nuovo o rigenerato: PAID quelle scadute fino
     * all'ultima chiusura (le rate della chiusura in corso le marca lo step
     * COUPON). Mutuo variabile: il tasso della richiesta (indice corrente +
     * spread) vale per i periodi gia' fissati, cioe' con data di fixing fino
     * alla data ufficiale (RECORDED); i successivi restano stimati (ESTIMATED)
     * a quel tasso e li fissa lo step FIXING della chiusura (RateFixingService).
     */
    private void markPeriods(LoanMasterData lmd) {
        LocalDate official = marketDataService.getOfficialDate();
        LocalDate lastOfficial = sbcDAO.findBySbcId(1).getLastOfficialDate();
        Calendar calendar = calendar(lmd);
        for (org.softcaster.core.data.CashFlowItem item : lmd.getCashFlows()) {
            CashFlowStatus status = CashFlowStatus.RECORDED;
            if (!item.getEnddate().toLocalDate().isAfter(lastOfficial)) {
                status = CashFlowStatus.PAID;
            } else if (lmd instanceof FltLoanMasterData flt
                    && RateFixing.fixingDate(item.getStartDate().toLocalDate(), flt.getFloatingTerms(), calendar).isAfter(official)) {
                status = CashFlowStatus.ESTIMATED;
            }
            item.setCashFlowStatus(status);
        }
    }

    /**
     * Data operazione: quella della richiesta, altrimenti l'erogazione; per
     * un'erogazione futura la data ufficiale (il regolamento resta alla data di
     * erogazione, FinancialTxnService.calcSettlementDate).
     */
    private LocalDate tradeDate(LoanDealRequest r, LoanMasterData lmd) {
        if (r.tradeDate != null) {
            return r.tradeDate;
        }
        LocalDate disbursement = lmd.getIssueDate().toLocalDate();
        LocalDate official = marketDataService.getOfficialDate();
        return disbursement.isAfter(official) ? official : disbursement;
    }

    /**
     * Quantita' del deal: debito residuo alla data operazione (il capitale per
     * un deal all'erogazione). I rimborsi delle rate successive sono deal EOD
     * che la riducono (LoanRepaymentService in easy_pricer_lc).
     */
    private static double dealQuantity(LoanMasterData lmd, LocalDate tradeDate) {
        double outstanding = LoanCalculator.outstanding(lmd, tradeDate);
        if (outstanding <= 0.) {
            throw new IllegalArgumentException("Loan " + lmd.getCode() + " has no outstanding principal at " + tradeDate);
        }
        return Math.round(outstanding * 100.) / 100.;
    }

    private static Calendar calendar(LoanMasterData lmd) {
        return new Calendar(lmd.getCurrencyList());
    }

    private LocalDate valuationDate(LoanDealRequest r) {
        return r.valuationDate != null ? r.valuationDate : marketDataService.getOfficialDate();
    }

    private static <T> T require(T value, String name) {
        if (value == null || (value instanceof String s && s.isBlank())) {
            throw new IllegalArgumentException("Missing required field: " + name);
        }
        return value;
    }
}
