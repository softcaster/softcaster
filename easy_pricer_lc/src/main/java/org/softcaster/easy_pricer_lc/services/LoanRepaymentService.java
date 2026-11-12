package org.softcaster.easy_pricer_lc.services;

import java.sql.Date;
import java.time.LocalDate;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CashFlowItem;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.LoanMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.PositionTxnLinksDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.calc.LoanCalculator;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.RequestType;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Rimborso del capitale delle rate dei mutui che scadono nella chiusura, in
 * (last_official_date, official_date]: per ogni posizione un deal EOD di segno
 * opposto alla posizione, al prezzo 100, con data operazione e regolamento
 * official_date. Riduce il nominale in posizione, che segue cosi' il debito
 * residuo: interessi e ratei restano per quota (LoanScheduler).
 *
 * Quantita' = quota capitale della rata per la quota della posizione regolata
 * prima della scadenza; l'ultima rata chiude tutto il regolato. I deal entrano
 * PENDING e li esegue il processor; la origin_key rende innocuo un rilancio.
 */
@Service
public class LoanRepaymentService {

    private static final Logger log = LoggerFactory.getLogger(LoanRepaymentService.class);

    private static final int SBC_ID = 1;

    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private PositionTxnLinksDAO positionTxnLinksDAO;
    @Autowired
    private MasterDataDAO masterDataDAO;
    @Autowired
    private CounterpartyDAO counterpartyDAO;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    private static final class Counters {

        int processed;
        int created;
        int skipped;
        int errors;
        String firstError;
    }

    public EodTaskResult generateRepayments() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate from = sbc.getLastOfficialDate();
        LocalDate date = sbc.getOfficialDate();

        // cambi di chiusura per i mutui non nella valuta di sistema (fx_rate del deal)
        marketDataService.loadSpotPrice();

        Counters counters = new Counters();
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                try {
                    transactionTemplate.executeWithoutResult(status -> repayDetail(detail, position, sbc, from, date, counters));
                } catch (Exception e) {
                    counters.processed++;
                    counters.errors++;
                    log.error("### Loan repayment error for position detail {}", detail.getIdPositionDetail(), e);
                    if (counters.firstError == null) {
                        counters.firstError = "position detail " + detail.getIdPositionDetail() + ": " + e.getLocalizedMessage();
                    }
                }
            }
        }
        return EodTaskResult.of(EodStep.COUPON, date, counters.processed, counters.created, counters.skipped, counters.errors,
                counters.firstError == null ? "" : "First error: " + counters.firstError);
    }

    private void repayDetail(PositionDetail detail, PositionMasterData position, SystemBusinessCalendar sbc, LocalDate from,
            LocalDate date, Counters counters) {
        MasterData masterData = (MasterData) Hibernate.unproxy(masterDataDAO.findByIdMasterData(detail.getMasterData()));
        if (!(masterData instanceof LoanMasterData lmd)) {
            return;
        }
        for (CashFlowItem item : LoanCalculator.installmentsIn(lmd, from, date)) {
            if (item.getAmount() == null || NumberUtils.isZero(item.getAmount())) {
                continue; // preammortamento o rata di soli interessi
            }
            LocalDate dueDate = item.getEnddate().toLocalDate();
            double settled = positionTxnLinksDAO.sumQuantitySettledBefore(detail.getIdPositionDetail(), dueDate);
            double outstanding = LoanCalculator.outstandingBefore(lmd, dueDate);
            if (NumberUtils.isZero(settled) || outstanding <= 0.) {
                continue;
            }
            counters.processed++;
            String originKey = "LOAN_REPAY:" + detail.getIdPositionDetail() + ":" + dueDate;
            if (financialTxnDAO.existsByOriginKey(originKey)) {
                counters.skipped++;
                continue;
            }

            boolean last = LoanCalculator.outstandingAfter(lmd, dueDate) <= 0.005;
            double quantity = last ? Math.abs(settled) : round2(Math.abs(settled) * item.getAmount() / outstanding);
            TxnSide side = settled > 0 ? TxnSide.SELL : TxnSide.BUY;
            financialTxnDAO.saveOrUpdate(repaymentDeal(detail, position, lmd, date, side, quantity, fxRate(lmd, sbc),
                    "Loan repayment " + dueDate, originKey));
            counters.created++;
            log.info("Loan repayment {} position detail {}: installment {} {} {}", lmd.getCode(), detail.getIdPositionDetail(),
                    dueDate, side, quantity);
        }
    }

    private static double round2(double value) {
        return Math.round(value * 100.) / 100.;
    }

    /**
     * Come per i deal dell'utente (FinancialTxnService): 1 nella valuta di
     * sistema, altrimenti il cambio BID valuta di sistema / valuta del mutuo.
     */
    private double fxRate(LoanMasterData lmd, SystemBusinessCalendar sbc) {
        if (lmd.getCurrency().getIdCurrency().equals(sbc.getCurrency().getIdCurrency())) {
            return 1.;
        }
        String pair = sbc.getCurrency().getIsoCode() + lmd.getCurrency().getIsoCode();
        double rate = marketDataService.getSpotPrice(pair, RequestType.BID);
        if (rate <= 0.) {
            throw new IllegalStateException("No rate for " + pair);
        }
        return rate;
    }

    private FinancialTxn repaymentDeal(PositionDetail detail, PositionMasterData position, MasterData masterData,
            LocalDate date, TxnSide side, double quantity, double fxRate, String description, String originKey) {
        FinancialTxn txn = new FinancialTxn();
        txn.setCounterparty(counterpartyDAO.findByIdCounterparty(detail.getCounterparty()));
        txn.setPositionMd(position);
        txn.setMasterData(masterData);
        txn.setDescription(description);
        txn.setTradeDate(Date.valueOf(date));
        txn.setValueDate(Date.valueOf(date));
        txn.setSettlement(Date.valueOf(date));
        txn.setTxnSide(side);
        txn.setQuantity(quantity);
        txn.setPrice(100.);
        txn.setFxRate(fxRate);
        txn.setTxnStatus(TxnStatus.PENDING);
        txn.setTxnStatusPreElab(TxnStatus.PENDING);
        txn.setTxnAcctPhase(AccountingPhase.NONE);
        txn.setTxnOrigin(TxnOrigin.EOD);
        txn.setOriginKey(originKey);
        return txn;
    }
}
