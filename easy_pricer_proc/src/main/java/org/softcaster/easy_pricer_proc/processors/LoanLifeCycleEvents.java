package org.softcaster.easy_pricer_proc.processors;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.LoanMasterDataDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.AccountingEventAccrualsDAO;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.easy_pricer_mds_core.calc.LoanCalculator;
import org.softcaster.easy_pricer_proc.exceptions.TxnProcessingException;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Ciclo di vita dei mutui per i deal dell'utente fuori dalla chiusura: il deal
 * retrodatato (tipicamente il nuovo deal di un amend) e lo storno di un deal
 * cancellato o sostituito, come se non fosse mai esistito.
 *
 * Un contratto (anagrafica) ha un solo deal dell'utente: LoanDealService crea
 * un'anagrafica per ogni mutuo. Con altri deal dell'utente vivi sulla stessa
 * posizione lo storno non si puo' attribuire e il deal viene rifiutato.
 *
 * Gli importi seguono LoanScheduler / LoanRepaymentService (easy_pricer_lc):
 * la posizione detiene la quota nominale / residuo del contratto.
 */
@Component
public class LoanLifeCycleEvents {

    private static final Logger log = LoggerFactory.getLogger(LoanLifeCycleEvents.class);

    public static final String REPAYMENT_KEY = "LOAN_REPAY:";
    private static final String REVERSAL_SUFFIX = " REV";

    @Autowired
    private LoanMasterDataDAO loanMasterDataDAO;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private AccountingEventDAO accountingEventDAO;
    @Autowired
    private AccountingEventAccrualsDAO accountingEventAccrualsDAO;

    /**
     * Deal dell'utente su un mutuo, l'unico che porta ratei, rate e rimborsi
     * propri (i rimborsi EOD no).
     */
    public static boolean isUserLoanDeal(FinancialTxn txn) {
        return org.hibernate.Hibernate.unproxy(txn.getMasterData()) instanceof LoanMasterData
                && txn.getTxnOrigin() != TxnOrigin.EOD;
    }

    // ------------------------------------------------------------------------
    // -- Deal retrodatato
    // ------------------------------------------------------------------------

    /**
     * Deal eseguito con regolamento prima della data ufficiale: rateo dal
     * regolamento a officialDate, interessi e rimborsi delle rate scadute in
     * (regolamento, lastOfficialDate]. Quelle in (lastOfficialDate,
     * officialDate] le genera la chiusura, sulla posizione che comprende il
     * deal. I rimborsi sono deal EOD (PENDING) regolati alla scadenza della
     * rata, cosi' le quote regolate delle rate successive li comprendono.
     */
    public List<AccountingEvent> backdatedEvents(FinancialTxn txn, Integer positionDetailId, LocalDate officialDate,
            LocalDate lastOfficialDate) {
        LoanMasterData lmd = loanMasterDataDAO.getById(txn.getMasterData().getIdMasterData());
        LocalDate settlement = txn.getSettlement().toLocalDate();
        double signed = txn.getTxnSide() == TxnSide.SELL ? -txn.getQuantity() : txn.getQuantity();
        double outstandingAtSettlement = LoanCalculator.outstandingAfter(lmd, settlement);
        List<AccountingEvent> events = new ArrayList<>();
        if (outstandingAtSettlement <= 0.) {
            return events;
        }
        // quota del contratto detenuta dal deal
        double share = signed / outstandingAtSettlement;

        double repaid = 0.;
        for (CashFlowItem item : LoanCalculator.installmentsIn(lmd, settlement, lastOfficialDate)) {
            LocalDate due = item.getEnddate().toLocalDate();
            double nominal = share * LoanCalculator.outstandingBefore(lmd, due);
            double interest = share * item.getInterest();
            if (!NumberUtils.isZero(interest)) {
                events.add(event(lmd, positionDetailId, EventType.COUPON, interest, nominal,
                        (int) ChronoUnit.DAYS.between(item.getStartDate().toLocalDate(), due),
                        "[" + EventType.COUPON.getCode() + "]" + due + " txn " + txn.getIdFinancialTxn()));
            }
            if (item.getAmount() != null && !NumberUtils.isZero(item.getAmount())) {
                boolean last = LoanCalculator.outstandingAfter(lmd, due) <= 0.005;
                double quantity = last ? round2(Math.abs(signed) - repaid) : round2(Math.abs(share * item.getAmount()));
                repaid += quantity;
                financialTxnDAO.saveOrUpdate(repaymentDeal(txn, officialDate, due, quantity, positionDetailId));
            }
        }

        double accrual = share * LoanCalculator.interestBetween(lmd, settlement, officialDate);
        if (!NumberUtils.isZero(accrual)) {
            events.add(event(lmd, positionDetailId, EventType.ACCRUAL, accrual,
                    share * LoanCalculator.outstandingAfter(lmd, officialDate),
                    (int) ChronoUnit.DAYS.between(settlement, officialDate),
                    "[" + EventType.ACCRUAL.getCode() + "]" + officialDate + " txn " + txn.getIdFinancialTxn()));
        }
        log.info("Backdated loan txn {}: {} events", txn.getIdFinancialTxn(), events.size());
        return events;
    }

    private FinancialTxn repaymentDeal(FinancialTxn txn, LocalDate officialDate, LocalDate due, double quantity,
            Integer positionDetailId) {
        FinancialTxn repayment = new FinancialTxn();
        repayment.setCounterparty(txn.getCounterparty());
        repayment.setPositionMd(txn.getPositionMd());
        repayment.setMasterData(txn.getMasterData());
        repayment.setDescription("Loan repayment " + due);
        repayment.setTradeDate(Date.valueOf(officialDate));
        repayment.setValueDate(Date.valueOf(due));
        repayment.setSettlement(Date.valueOf(due));
        repayment.setTxnSide(txn.getTxnSide() == TxnSide.BUY ? TxnSide.SELL : TxnSide.BUY);
        repayment.setQuantity(quantity);
        repayment.setPrice(100.);
        repayment.setFxRate(txn.getFxRate());
        repayment.setTxnStatus(TxnStatus.PENDING);
        repayment.setTxnStatusPreElab(TxnStatus.PENDING);
        repayment.setTxnAcctPhase(AccountingPhase.NONE);
        repayment.setTxnOrigin(TxnOrigin.EOD);
        // con il deal: le rate gia' scadute di un deal sostituito hanno la chiave senza
        repayment.setOriginKey(REPAYMENT_KEY + positionDetailId + ":" + due + ":txn" + txn.getIdFinancialTxn());
        return repayment;
    }

    // ------------------------------------------------------------------------
    // -- Storno
    // ------------------------------------------------------------------------

    /**
     * Deal cancellato o sostituito da un amend: si negano i ratei e gli
     * interessi delle rate registrati sulla posizione (gli eventi veri, non
     * ricalcolati: l'amend ha gia' sostituito il piano) e si cancellano i
     * rimborsi EOD, che il processor storna come ogni cancellazione.
     */
    public List<AccountingEvent> reversalEvents(FinancialTxn txn, Integer positionDetailId) {
        requireOnlyUserDeal(txn);

        List<AccountingEvent> events = new ArrayList<>();
        for (AccountingEventAccruals original : accountingEventAccrualsDAO.findByPositionDetail(positionDetailId,
                List.of(EventType.ACCRUAL, EventType.COUPON))) {
            String key = original.getEventKey();
            if (key.endsWith(REVERSAL_SUFFIX) || accountingEventDAO.findByEventKey(key + REVERSAL_SUFFIX) != null) {
                continue; // e' uno storno, o e' gia' stato stornato
            }
            if (original.getAccrualAmount() == null || NumberUtils.isZero(original.getAccrualAmount())) {
                continue;
            }
            AccountingEventAccruals reversal = new AccountingEventAccruals();
            reversal.setAccrualAmount(-original.getAccrualAmount());
            // stesso nominale: stesso lato della posizione, quindi stessi conti
            reversal.setAccountingNominal(original.getAccountingNominal());
            reversal.setCouponRate(original.getCouponRate());
            reversal.setDaycount(original.getDaycount());
            reversal.setDays(original.getDays());
            reversal.setCreatedAt(LocalDateTime.now());
            reversal.setEventStatus(AccountingEventStatus.NEW);
            reversal.setEventType(original.getEventType());
            reversal.setPositionDetail(positionDetailId);
            reversal.setSourceId(original.getSourceId());
            reversal.setSourceType(original.getSourceType());
            reversal.setEventKey(key + REVERSAL_SUFFIX);
            reversal.setGeneratedBy(original.getGeneratedBy());
            reversal.setGeneratedRef(original.getGeneratedRef());
            events.add(reversal);
        }

        int cancelled = 0;
        for (FinancialTxn repayment : financialTxnDAO.findByOriginKeyPrefix(REPAYMENT_KEY + positionDetailId + ":")) {
            if (repayment.getTxnStatus() == TxnStatus.EXECUTED || repayment.getTxnStatus() == TxnStatus.PENDING) {
                repayment.setTxnStatus(TxnStatus.TO_CANCEL);
                repayment.setTxnStatusPreElab(TxnStatus.TO_CANCEL);
                financialTxnDAO.saveOrUpdate(repayment);
                cancelled++;
            }
        }
        log.info("Loan txn {} reversed: {} events, {} repayments to cancel", txn.getIdFinancialTxn(), events.size(), cancelled);
        return events;
    }

    private void requireOnlyUserDeal(FinancialTxn txn) {
        for (FinancialTxn other : financialTxnDAO.findByMasterData(txn.getMasterData().getIdMasterData())) {
            if (other.getIdFinancialTxn().equals(txn.getIdFinancialTxn()) || other.getTxnOrigin() == TxnOrigin.EOD
                    || other.getTxnStatus() != TxnStatus.EXECUTED
                    || !other.getPositionMd().getIdPosition().equals(txn.getPositionMd().getIdPosition())
                    || !other.getCounterparty().getIdCounterparty().equals(txn.getCounterparty().getIdCounterparty())) {
                continue;
            }
            throw new TxnProcessingException("Loan " + txn.getMasterData().getCode() + " has other trades on the position ("
                    + other.getIdFinancialTxn() + "): cancel or amend of a single trade is not supported");
        }
    }

    private static AccountingEventAccruals event(LoanMasterData lmd, Integer positionDetailId, EventType type, double amount,
            double nominal, int days, String keySuffix) {
        AccountingEventAccruals event = new AccountingEventAccruals();
        event.setAccrualAmount(amount);
        event.setAccountingNominal(nominal);
        event.setCouponRate(lmd.getInterestRate());
        event.setDaycount(lmd.getAccrualDaycount());
        event.setDays(days);
        event.setCreatedAt(LocalDateTime.now());
        event.setEventStatus(AccountingEventStatus.NEW);
        event.setEventType(type);
        event.setPositionDetail(positionDetailId);
        event.setSourceId(positionDetailId);
        event.setSourceType(EventSourceType.INSTRUMENT);
        event.setEventKey(lmd.getCode() + " [" + positionDetailId + "] " + keySuffix);
        event.setGeneratedBy(lmd.getIdMasterData());
        event.setGeneratedRef(lmd.getCode());
        return event;
    }

    private static double round2(double value) {
        return Math.round(value * 100.) / 100.;
    }
}
