package org.softcaster.easy_pricer_lc.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.AccumulatorFixing;
import org.softcaster.core.data.AccumulatorMasterData;
import org.softcaster.core.data.CmdFutureMasterData;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.data.account.AccountingEventAccruals;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.PnlCalc;
import org.softcaster.engine.analytics.AccumulatorFixingRule;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.GuaranteeType;
import org.softcaster.engine.enums.RequestType;
import org.softcaster.engine.enums.SettlementType;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Ciclo di vita degli accumulator / decumulator (FXA, CMA) nella chiusura.
 *
 *  - FIXING: ogni fixing del calendario fino alla data ufficiale si rileva al
 *    prezzo di chiusura del sottostante (mid, caricato a inizio chiusura; per
 *    un fixing in un giorno di chiusura del sistema si usa quello della data
 *    ufficiale). Knock-out, knock-in e quantita' maturata con la regola del
 *    pricer (AccumulatorFixingRule). La barriera continua si controlla al solo
 *    prezzo di fixing: un tocco intraday si registra a mano (ko_date).
 *    Dopo il knock-out i fixing si rilevano con quantita' 0.
 *  - COUPON: alla data di regolamento di un periodo, come la cedola di un
 *    titolo. Consegna fisica: deal EOD sul sottostante allo strike (spot sulla
 *    coppia per FXA, future su commodity per CMA), chi detiene l'accumulator
 *    compra, il decumulator vende. Regolamento cash: evento COUPON con
 *    segno x Q x (S - K), S il prezzo alla data di regolamento (come il pricer).
 *  - MATURITY (MaturityService): dopo l'ultimo regolamento, o dopo il
 *    knock-out, il contratto si chiude a prezzo 0, come un'opzione scaduta.
 *
 * Un contratto e' un'anagrafica: i fixing sono del contratto, i regolamenti
 * di ogni posizione aperta (quantita' x contratti in posizione).
 */
@Service
public class AccumulatorLifeCycleService {

    private static final Logger log = LoggerFactory.getLogger(AccumulatorLifeCycleService.class);
    private static final int SBC_ID = 1;
    static final String SETTLE_KEY = "ACC_SETTLE:";

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @Autowired
    private CounterpartyDAO counterpartyDAO;
    @Autowired
    private FinancialTxnDAO financialTxnDAO;
    @Autowired
    private AccountingEventDAO accountingEventDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    // ------------------------------------------------------------------------
    // -- FIXING
    // ------------------------------------------------------------------------

    /**
     * Rileva i fixing dei contratti in posizione fino alla data ufficiale.
     */
    public EodTaskResult fixAccumulators() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate date = sbc.getOfficialDate();
        int processed = 0;
        int created = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();
        for (Integer id : contractsInPosition()) {
            processed++;
            try {
                Integer fixed = transactionTemplate.execute(status -> fixContract(id, date));
                if (fixed != null && fixed > 0) {
                    created += fixed;
                } else {
                    skipped++;
                }
            } catch (Exception e) {
                errors.add(e.getLocalizedMessage());
                log.error("### Accumulator fixing error for master data {}", id, e);
            }
        }
        return EodTaskResult.of(EodStep.FIXING, date, processed, created, skipped, errors.size(), String.join("; ", errors));
    }

    private int fixContract(Integer id, LocalDate date) {
        AccumulatorMasterData amd = entityManager.find(AccumulatorMasterData.class, id);
        List<AccumulatorFixing> fixings = amd.getFixings();
        MasterData underlying = (MasterData) Hibernate.unproxy(amd.getUnderlying());
        int guaranteed = amd.getGuaranteedFixings();
        boolean knockedIn = amd.isKnockedIn();
        int fixed = 0;
        for (int i = 0; i < fixings.size(); i++) {
            AccumulatorFixing f = fixings.get(i);
            LocalDate fixingDate = f.getFixingLocalDate();
            if (fixingDate.isAfter(date)) {
                break;
            }
            if (f.isFixed()) {
                continue;
            }
            if (fixingDate.isBefore(date)) {
                log.warn("Accumulator {}: fixing of {} recorded at the closing price of {}", amd.getCode(), fixingDate, date);
            }
            double price = closePrice(underlying, RequestType.MIDDLE);
            f.setFixingPrice(price);
            fixed++;
            if (amd.isKnockedOut()) {
                f.setAccumulatedQuantity(0.);
                continue;
            }
            boolean periodEnd = i == fixings.size() - 1
                    || !fixings.get(i + 1).getSettlementDate().equals(f.getSettlementDate());
            boolean baseOnly = amd.getGuaranteeType() == GuaranteeType.UNLEVERAGED && i < guaranteed;
            if (AccumulatorFixingRule.koObserved(i, guaranteed, amd.getKoObservation(), periodEnd)
                    && amd.getAccumulatorType().isKnockedOut(price, amd.getKoBarrier())) {
                f.setAccumulatedQuantity(Boolean.TRUE.equals(amd.getKoAccrual())
                        ? quantity(amd, knockedIn, baseOnly, price) : 0.);
                amd.setKoDate(Date.valueOf(fixingDate));
                log.info("Accumulator {}: knock-out at {} on {}", amd.getCode(), price, fixingDate);
                continue;
            }
            if (amd.getKiBarrier() != null && !knockedIn && amd.getAccumulatorType().isKnockedIn(price, amd.getKiBarrier())) {
                knockedIn = true;
                amd.setKiDate(Date.valueOf(fixingDate));
                log.info("Accumulator {}: knock-in at {} on {}", amd.getCode(), price, fixingDate);
            }
            f.setAccumulatedQuantity(quantity(amd, knockedIn, baseOnly, price));
        }
        return fixed;
    }

    private static double quantity(AccumulatorMasterData amd, boolean knockedIn, boolean baseOnly, double price) {
        return AccumulatorFixingRule.quantity(amd.getAccumulatorType(), amd.getStrike(), amd.getDailyQuantity(),
                amd.getLeverage(), amd.getKiEffect(), knockedIn, baseOnly, price);
    }

    // ------------------------------------------------------------------------
    // -- REGOLAMENTI (step COUPON)
    // ------------------------------------------------------------------------

    /**
     * Regola i periodi con data di regolamento in (last_official_date,
     * official_date] per ogni posizione aperta su un accumulator.
     */
    public EodTaskResult settlePeriods() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        LocalDate from = sbc.getLastOfficialDate();
        LocalDate date = sbc.getOfficialDate();
        int[] counters = new int[3]; // processed, created, skipped
        List<String> errors = new ArrayList<>();
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                if (PnlCalc.isFlat(detail.getBuyQty(), detail.getSellQty())) {
                    continue;
                }
                try {
                    transactionTemplate.executeWithoutResult(status -> settleDetail(detail, position, sbc, from, date, counters));
                } catch (Exception e) {
                    errors.add("position detail " + detail.getIdPositionDetail() + ": " + e.getLocalizedMessage());
                    log.error("### Accumulator settlement error for position detail {}", detail.getIdPositionDetail(), e);
                }
            }
        }
        return EodTaskResult.of(EodStep.COUPON, date, counters[0], counters[1], counters[2], errors.size(),
                errors.isEmpty() ? "" : "Accumulators: " + String.join("; ", errors));
    }

    private void settleDetail(PositionDetail detail, PositionMasterData position, SystemBusinessCalendar sbc, LocalDate from,
            LocalDate date, int[] counters) {
        MasterData md = (MasterData) Hibernate.unproxy(entityManager.find(MasterData.class, detail.getMasterData()));
        if (!(md instanceof AccumulatorMasterData amd)) {
            return;
        }
        double contracts = nz(detail.getBuyQty()) - nz(detail.getSellQty());
        for (Map.Entry<LocalDate, Double> period : periodQuantities(amd, from, date).entrySet()) {
            LocalDate settlementDate = period.getKey();
            double quantity = period.getValue();
            counters[0]++;
            if (NumberUtils.isZero(quantity)) {
                counters[2]++;
                continue;
            }
            if (amd.getSettlementType() == SettlementType.PHYSICAL) {
                counters[1] += deliver(detail, position, amd, sbc, date, settlementDate, quantity * contracts) ? 1 : 0;
            } else {
                counters[1] += cashSettle(detail, amd, date, settlementDate, quantity, contracts) ? 1 : 0;
            }
        }
    }

    /**
     * Quantita' per data di regolamento in (from, to]: somma dei fixing del
     * periodo, tutti rilevati.
     */
    private static Map<LocalDate, Double> periodQuantities(AccumulatorMasterData amd, LocalDate from, LocalDate to) {
        Map<LocalDate, Double> periods = new TreeMap<>();
        for (AccumulatorFixing f : amd.getFixings()) {
            LocalDate settlementDate = f.getSettlementLocalDate();
            if (!settlementDate.isAfter(from) || settlementDate.isAfter(to)) {
                continue;
            }
            if (!f.isFixed()) {
                throw new IllegalStateException("Accumulator " + amd.getCode() + ": fixing of " + f.getFixingLocalDate()
                        + " not recorded, period settling on " + settlementDate);
            }
            periods.merge(settlementDate, nz(f.getAccumulatedQuantity()), Double::sum);
        }
        return periods;
    }

    /**
     * Consegna fisica allo strike: contracts con segno (lungo = chi detiene il
     * contratto). Chi detiene l'accumulator compra il sottostante, il
     * decumulator vende.
     */
    private boolean deliver(PositionDetail detail, PositionMasterData position, AccumulatorMasterData amd,
            SystemBusinessCalendar sbc, LocalDate date, LocalDate settlementDate, double signedQuantity) {
        String originKey = SETTLE_KEY + detail.getIdPositionDetail() + ":" + settlementDate;
        if (financialTxnDAO.existsByOriginKey(originKey)) {
            return false;
        }
        MasterData underlying = (MasterData) Hibernate.unproxy(amd.getUnderlying());
        boolean buys = (signedQuantity > 0) == (amd.getAccumulatorType().getSign() > 0);
        double quantity = Math.abs(signedQuantity);
        LocalDate valueDate = settlementDate;
        if (underlying instanceof CmdFutureMasterData cfmd) {
            if (cfmd.getContractValue() == null || cfmd.getContractValue() <= 0.) {
                throw new IllegalStateException("Missing contract value for " + cfmd.getCode());
            }
            quantity = quantity / cfmd.getContractValue();
            valueDate = date;
        } else if (!(underlying instanceof ForexMasterData)) {
            throw new IllegalStateException("Physical settlement on " + underlying.getCode() + " is not supported");
        }
        FinancialTxn txn = new FinancialTxn();
        txn.setCounterparty(counterpartyDAO.findByIdCounterparty(detail.getCounterparty()));
        txn.setPositionMd(position);
        txn.setMasterData(underlying);
        txn.setDescription("Accumulator " + amd.getCode() + " settlement " + settlementDate);
        txn.setTradeDate(Date.valueOf(date));
        txn.setValueDate(Date.valueOf(valueDate));
        txn.setSettlement(Date.valueOf(valueDate));
        txn.setTxnSide(buys ? TxnSide.BUY : TxnSide.SELL);
        txn.setQuantity(quantity);
        txn.setPrice(amd.getStrike());
        // FX spot: il cambio contrattuale, come FxRollingService; future: cambio verso la valuta di sistema
        txn.setFxRate(underlying instanceof ForexMasterData ? amd.getStrike() : fxRate(underlying, sbc));
        txn.setTxnStatus(TxnStatus.PENDING);
        txn.setTxnStatusPreElab(TxnStatus.PENDING);
        txn.setTxnAcctPhase(AccountingPhase.NONE);
        txn.setTxnOrigin(TxnOrigin.EOD);
        txn.setOriginKey(originKey);
        financialTxnDAO.saveOrUpdate(txn);
        log.info("Accumulator {} position detail {}: delivery {} {} {} at {}", amd.getCode(), detail.getIdPositionDetail(),
                txn.getTxnSide(), quantity, underlying.getCode(), amd.getStrike());
        return true;
    }

    /**
     * Regolamento cash: evento COUPON della posizione, importo per chi la
     * detiene sign x Q x (S - K) x contratti con segno.
     */
    private boolean cashSettle(PositionDetail detail, AccumulatorMasterData amd, LocalDate date, LocalDate settlementDate,
            double quantity, double contracts) {
        String eventKey = amd.getCode() + " [" + detail.getIdPositionDetail() + "] [" + EventType.COUPON.getCode() + "]" + settlementDate;
        if (accountingEventDAO.findByEventKey(eventKey) != null) {
            return false;
        }
        if (settlementDate.isBefore(date)) {
            log.warn("Accumulator {}: settlement of {} at the closing price of {}", amd.getCode(), settlementDate, date);
        }
        double price = closePrice((MasterData) Hibernate.unproxy(amd.getUnderlying()), RequestType.MIDDLE);
        double amount = amd.getAccumulatorType().getSign() * quantity * (price - amd.getStrike()) * contracts;
        AccountingEventAccruals event = new AccountingEventAccruals();
        event.setAccrualAmount(amount);
        event.setAccountingNominal(contracts);
        event.setCouponRate(price);
        event.setDaycount(DaycountBasis.ACT_365);
        event.setDays(0);
        event.setCreatedAt(LocalDateTime.now());
        event.setEventStatus(AccountingEventStatus.NEW);
        event.setEventType(EventType.COUPON);
        event.setPositionDetail(detail.getIdPositionDetail());
        event.setSourceId(detail.getIdPositionDetail());
        event.setSourceType(EventSourceType.INSTRUMENT);
        event.setEventKey(eventKey);
        event.setGeneratedBy(amd.getIdMasterData());
        event.setGeneratedRef(amd.getCode());
        accountingEventDAO.saveOrUpdate(event);
        log.info("Accumulator {} position detail {}: cash settlement {} of {} x ({} - {})", amd.getCode(),
                detail.getIdPositionDetail(), amount, quantity, price, amd.getStrike());
        return true;
    }

    // ------------------------------------------------------------------------
    // -- SCADENZA (MaturityService)
    // ------------------------------------------------------------------------

    /**
     * Fine vita del contratto: dopo il knock-out, l'ultimo regolamento ancora
     * dovuto (o il giorno del knock-out se non c'e' nulla da regolare);
     * altrimenti l'ultimo regolamento del calendario.
     */
    public static LocalDate endDate(AccumulatorMasterData amd) {
        LocalDate end = null;
        if (amd.isKnockedOut()) {
            LocalDate ko = amd.getKoDate().toLocalDate();
            end = ko;
            for (AccumulatorFixing f : amd.getFixings()) {
                if (!f.getFixingLocalDate().isAfter(ko) && nz(f.getAccumulatedQuantity()) > 0.
                        && f.getSettlementLocalDate().isAfter(end)) {
                    end = f.getSettlementLocalDate();
                }
            }
            return end;
        }
        for (AccumulatorFixing f : amd.getFixings()) {
            if (end == null || f.getSettlementLocalDate().isAfter(end)) {
                end = f.getSettlementLocalDate();
            }
        }
        return end != null ? end : (amd.getMaturityDate() == null ? null : amd.getMaturityDate().toLocalDate());
    }

    // ------------------------------------------------------------------------

    private Set<Integer> contractsInPosition() {
        Set<Integer> ids = new TreeSet<>();
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                if (PnlCalc.isFlat(detail.getBuyQty(), detail.getSellQty())) {
                    continue;
                }
                MasterData md = (MasterData) Hibernate.unproxy(entityManager.find(MasterData.class, detail.getMasterData()));
                if (md instanceof AccumulatorMasterData) {
                    ids.add(md.getIdMasterData());
                }
            }
        }
        return ids;
    }

    /**
     * Prezzo di chiusura dalla quotazione dello strumento (per le coppie di
     * cambi dal codice della coppia); senza prezzo la chiusura si ferma.
     */
    private double closePrice(MasterData md, RequestType request) {
        double price = marketDataService.getSpotPrice(md.getIdMasterData(), request);
        if (price <= 0.) {
            price = marketDataService.getSpotPrice(md.getCode(), request);
        }
        if (price <= 0.) {
            throw new IllegalStateException("Missing closing price for " + md.getCode());
        }
        return price;
    }

    private double fxRate(MasterData md, SystemBusinessCalendar sbc) {
        if (md.getCurrency().getIdCurrency().equals(sbc.getCurrency().getIdCurrency())) {
            return 1.;
        }
        String pair = sbc.getCurrency().getIsoCode() + md.getCurrency().getIsoCode();
        double rate = marketDataService.getSpotPrice(pair, RequestType.BID);
        if (rate <= 0.) {
            throw new IllegalStateException("No rate for " + pair);
        }
        return rate;
    }

    private static double nz(Double value) {
        return value == null ? 0. : value;
    }
}
