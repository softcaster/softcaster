package org.softcaster.easy_pricer_lc.services;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.CounterpartyDAO;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.FinancialTxnDAO;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.FutureMasterData;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.OptionMasterData;
import org.softcaster.core.data.PositionDetail;
import org.softcaster.core.data.PositionDetailDAO;
import org.softcaster.core.data.PositionMasterData;
import org.softcaster.core.data.PositionMasterDataDAO;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SpotOptionMasterData;
import org.softcaster.core.data.SystemBusinessCalendar;
import org.softcaster.core.data.SystemBusinessCalendarDAO;
import org.softcaster.core.data.account.AccountingEvent;
import org.softcaster.core.data.account.AccountingEventDAO;
import org.softcaster.core.dto.EodTaskResult;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.engine.enums.AccountingEventStatus;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.EodStep;
import org.softcaster.engine.enums.EventSourceType;
import org.softcaster.engine.enums.EventType;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.enums.RequestType;
import org.softcaster.engine.enums.SettlementType;
import org.softcaster.engine.enums.TxnComponentType;
import org.softcaster.engine.enums.TxnOrigin;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.softcaster.engine.utils.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Scadenza degli strumenti nella chiusura (step MATURITY): per ogni posizione
 * aperta su uno strumento che scade in (last_official_date, official_date]
 * genera deal EOD che la chiudono e, con consegna fisica, il deal sul
 * sottostante. I deal entrano PENDING e li esegue il processor; il realizzato
 * lo registra il MtM di chiusura, come per i deal dell'utente. La origin_key
 * (MATURITY:<posizione>:<data>[:UND]) rende innocuo un rilancio.
 *
 *  - Titoli (XRB, XRN, FRB, FRN, BLL), alla data di scadenza: rimborso del
 *    nominale residuo a 100, regolato alla scadenza. L'ultima cedola e' dello
 *    step COUPON.
 *  - Future, all'ultimo giorno di negoziazione: chiusura al prezzo di
 *    regolamento finale (quotazione di chiusura). Consegna fisica: FFU genera
 *    lo spot sulla coppia alla data di consegna; per BFU, CFU e MFU la
 *    consegna non e' ancora gestita e lo step fallisce.
 *  - Opzioni, alla scadenza: esercizio automatico se in the money (valore
 *    intrinseco sul prezzo del sottostante > 0). L'opzione si chiude al valore
 *    intrinseco (0 se fuori dal denaro); con consegna fisica il sottostante
 *    entra al prezzo di mercato, cosi' il costo netto e' lo strike e il fair
 *    value dell'opzione entra nel costo del sottostante (IFRS 9).
 *
 * Margine iniziale: un evento MATURITY per ogni deal della posizione che ne
 * ha uno, che la contabilita' rilascia (script FUT, OPTION).
 *
 * Gli accumulator (FXA, CMA) hanno un ciclo di vita proprio e non passano di
 * qui; i mutui si chiudono con il rimborso dell'ultima rata.
 */
@Service
public class MaturityService {

    private static final Logger log = LoggerFactory.getLogger(MaturityService.class);

    private static final int SBC_ID = 1;
    private static final String KEY = "MATURITY:";

    @Autowired
    private SystemBusinessCalendarDAO sbcDAO;
    @Autowired
    private PositionMasterDataDAO positionMasterDataDAO;
    @Autowired
    private PositionDetailDAO positionDetailDAO;
    @jakarta.persistence.PersistenceContext
    private jakarta.persistence.EntityManager entityManager;
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

    private static final class Counters {

        int processed;
        int created;
        int skipped;
        int errors;
        String firstError;
    }

    public EodTaskResult processMaturities() {
        SystemBusinessCalendar sbc = sbcDAO.findBySbcId(SBC_ID);
        if (sbc.getNextBusinessDate() == null) {
            throw new IllegalStateException("Maturities need the calendar in closing (next business date not set)");
        }
        LocalDate from = sbc.getLastOfficialDate();
        LocalDate date = sbc.getOfficialDate();

        // prezzi di chiusura: regolamento finale dei future, sottostanti delle opzioni, cambi
        marketDataService.loadSpotPrice();

        Counters counters = new Counters();
        for (PositionMasterData position : positionMasterDataDAO.findAll()) {
            for (PositionDetail detail : positionDetailDAO.findByPositionMasterData(position)) {
                try {
                    transactionTemplate.executeWithoutResult(status -> matureDetail(detail, position, sbc, from, date, counters));
                } catch (Exception e) {
                    counters.processed++;
                    counters.errors++;
                    log.error("### Maturity error for position detail {}", detail.getIdPositionDetail(), e);
                    if (counters.firstError == null) {
                        counters.firstError = "position detail " + detail.getIdPositionDetail() + ": " + e.getLocalizedMessage();
                    }
                }
            }
        }
        return EodTaskResult.of(EodStep.MATURITY, date, counters.processed, counters.created, counters.skipped, counters.errors,
                counters.firstError == null ? "" : "First error: " + counters.firstError);
    }

    /**
     * Data di fine vita dello strumento, null se non scade in questo step.
     */
    private static LocalDate endDate(MasterData md) {
        if (md instanceof SecurityMasterData) {
            return toLocal(md.getMaturityDate());
        }
        if (md instanceof FutureMasterData fmd) {
            LocalDate last = fmd.getLastTradingLocalDate();
            return last != null ? last : toLocal(md.getMaturityDate());
        }
        if (md instanceof OptionMasterData omd) {
            return omd.getExpiryLocalDate();
        }
        return null;
    }

    private void matureDetail(PositionDetail detail, PositionMasterData position, SystemBusinessCalendar sbc, LocalDate from,
            LocalDate date, Counters counters) {
        // find e non findByIdMasterData: la query con entity graph lascia vuota la
        // testata delle opzioni (contract value, tipo di regolamento)
        MasterData md = (MasterData) Hibernate.unproxy(entityManager.find(MasterData.class, detail.getMasterData()));
        LocalDate end = endDate(md);
        if (end == null || !end.isAfter(from) || end.isAfter(date)) {
            return;
        }
        double net = nz(detail.getBuyQty()) - nz(detail.getSellQty());
        if (NumberUtils.isZero(net)) {
            return;
        }
        counters.processed++;
        String originKey = KEY + detail.getIdPositionDetail() + ":" + end;
        if (financialTxnDAO.existsByOriginKey(originKey)) {
            counters.skipped++;
            return;
        }

        // margine iniziale dei deal della posizione: prima dei deal EOD, che non ne hanno
        counters.created += releaseMargins(detail, md, end);

        TxnSide closeSide = net > 0 ? TxnSide.SELL : TxnSide.BUY;
        if (md instanceof SecurityMasterData smd) {
            save(deal(detail, position, smd, date, end, closeSide, Math.abs(net), 100., sbc, "Redemption " + end, originKey));
            counters.created++;
        } else if (md instanceof FutureMasterData fmd) {
            counters.created += matureFuture(detail, position, fmd, sbc, date, end, net, closeSide, originKey);
        } else if (md instanceof OptionMasterData omd) {
            counters.created += expireOption(detail, position, omd, sbc, date, end, net, closeSide, originKey);
        }
    }

    private int matureFuture(PositionDetail detail, PositionMasterData position, FutureMasterData fmd, SystemBusinessCalendar sbc,
            LocalDate date, LocalDate end, double net, TxnSide closeSide, String originKey) {
        double price = closePrice(fmd);
        double contracts = Math.abs(net) / contractValue(fmd);
        save(deal(detail, position, fmd, date, date, closeSide, contracts, price, sbc,
                "Final settlement " + end, originKey));
        if (fmd.getSettlementType() != SettlementType.PHYSICAL) {
            return 1;
        }
        if (!(fmd instanceof FxFutureMasterData ffmd) || ffmd.getUnderlying() == null) {
            throw new IllegalStateException("Physical delivery of " + fmd.getCode() + " (" + fmd.getAssetClass().getCode()
                    + ") is not supported yet");
        }
        // consegna della valuta base al prezzo di regolamento finale: chi e' lungo compra
        ForexMasterData pair = (ForexMasterData) Hibernate.unproxy(ffmd.getUnderlying());
        LocalDate delivery = ffmd.getDeliveryLocalDate() != null ? ffmd.getDeliveryLocalDate() : settlementDate(pair, date);
        save(deal(detail, position, pair, date, delivery, opposite(closeSide), Math.abs(net), price, sbc,
                "Delivery of " + fmd.getCode(), originKey + ":UND"));
        return 2;
    }

    private int expireOption(PositionDetail detail, PositionMasterData position, OptionMasterData omd, SystemBusinessCalendar sbc,
            LocalDate date, LocalDate end, double net, TxnSide closeSide, String originKey) {
        MasterData underlying = (MasterData) Hibernate.unproxy(omd.getUnderlying());
        if (underlying == null || omd.getStrike() == null) {
            throw new IllegalStateException("Missing underlying or strike for option " + omd.getCode());
        }
        double underlyingPrice = closePrice(underlying);
        boolean call = omd.getOptionType() == OptionType.CALL;
        double intrinsic = Math.max(call ? underlyingPrice - omd.getStrike() : omd.getStrike() - underlyingPrice, 0.);
        double contracts = Math.abs(net) / omd.getContractValue();

        save(deal(detail, position, omd, date, date, closeSide, contracts, intrinsic, sbc,
                (intrinsic > 0. ? "Exercise " : "Expiry ") + end, originKey));
        if (intrinsic <= 0. || omd.getSettlementType() != SettlementType.PHYSICAL) {
            return 1;
        }

        // sottostante al prezzo di mercato. Chi e' lungo di una call (o corto di
        // una put) compra; la quantita' e' nelle unita' del sottostante
        boolean longOption = net > 0;
        TxnSide underlyingSide = (longOption == call) ? TxnSide.BUY : TxnSide.SELL;
        double units = Math.abs(net);
        double quantity = underlying instanceof FutureMasterData ufmd ? units / contractValue(ufmd) : units;
        LocalDate settlement = omd instanceof SpotOptionMasterData somd && somd.getDeliveryLocalDate() != null
                ? somd.getDeliveryLocalDate()
                : underlying instanceof FutureMasterData ? date : settlementDate(underlying, date);
        save(deal(detail, position, underlying, date, settlement, underlyingSide, quantity, underlyingPrice, sbc,
                "Exercise of " + omd.getCode(), originKey + ":UND"));
        return 2;
    }

    /**
     * Un evento MATURITY per ogni deal eseguito della posizione con margine
     * iniziale: la contabilita' lo rilascia.
     */
    private int releaseMargins(PositionDetail detail, MasterData md, LocalDate end) {
        int created = 0;
        List<FinancialTxn> txns = financialTxnDAO.findByMasterData(md.getIdMasterData());
        for (FinancialTxn txn : txns) {
            if (txn.getTxnStatus() != TxnStatus.EXECUTED
                    || !txn.getPositionMd().getIdPosition().equals(detail.getPositionMd())
                    || !txn.getCounterparty().getIdCounterparty().equals(detail.getCounterparty())
                    || txn.getComponents() == null
                    || txn.getComponents().stream().noneMatch(c -> c.getComponentType() == TxnComponentType.INITIAL_MARGIN
                    && c.getAmount() != null && c.getAmount().signum() > 0)) {
                continue;
            }
            String eventKey = md.getCode() + " [" + txn.getIdFinancialTxn() + "] [" + EventType.MATURITY.getCode() + "]" + end;
            if (accountingEventDAO.findByEventKey(eventKey) != null) {
                continue;
            }
            AccountingEvent event = new AccountingEvent();
            event.setSourceId(txn.getIdFinancialTxn());
            event.setEventStatus(AccountingEventStatus.NEW);
            event.setEventType(EventType.MATURITY);
            event.setSourceType(EventSourceType.TRADE);
            event.setEventKey(eventKey);
            event.setCreatedAt(LocalDateTime.now());
            event.setPositionDetail(detail.getIdPositionDetail());
            event.setGeneratedBy(md.getIdMasterData());
            event.setGeneratedRef(md.getCode());
            accountingEventDAO.saveOrUpdate(event);
            created++;
        }
        return created;
    }

    /**
     * Prezzo di chiusura dalla quotazione dello strumento (per le coppie di
     * cambi dal codice della coppia); senza prezzo lo step fallisce.
     */
    private double closePrice(MasterData md) {
        double price = marketDataService.getSpotPrice(md.getIdMasterData(), RequestType.BID);
        if (price <= 0.) {
            price = marketDataService.getSpotPrice(md.getCode(), RequestType.BID);
        }
        if (price <= 0.) {
            throw new IllegalStateException("Missing closing price for " + md.getCode());
        }
        return price;
    }

    private static double contractValue(FutureMasterData fmd) {
        if (fmd.getContractValue() == null || fmd.getContractValue() <= 0.) {
            throw new IllegalStateException("Missing contract value for " + fmd.getCode());
        }
        return fmd.getContractValue();
    }

    private static LocalDate settlementDate(MasterData md, LocalDate date) {
        int days = md.getBusinessDays() == null ? 0 : md.getBusinessDays();
        return new Calendar(md.getCurrencyList()).getNextBusinessDate(date, days);
    }

    private static TxnSide opposite(TxnSide side) {
        return side == TxnSide.BUY ? TxnSide.SELL : TxnSide.BUY;
    }

    private void save(FinancialTxn txn) {
        financialTxnDAO.saveOrUpdate(txn);
        log.info("Maturity deal {} {} {} {} @ {} settlement {}", txn.getOriginKey(), txn.getMasterData().getCode(),
                txn.getTxnSide(), txn.getQuantity(), txn.getPrice(), txn.getSettlement());
    }

    /**
     * Come per i deal dell'utente (FinancialTxnService): 1 nella valuta di
     * sistema, altrimenti il cambio BID valuta di sistema / valuta dello
     * strumento.
     */
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

    private FinancialTxn deal(PositionDetail detail, PositionMasterData position, MasterData md, LocalDate date,
            LocalDate settlement, TxnSide side, double quantity, double price, SystemBusinessCalendar sbc, String description,
            String originKey) {
        FinancialTxn txn = new FinancialTxn();
        txn.setCounterparty(counterpartyDAO.findByIdCounterparty(detail.getCounterparty()));
        txn.setPositionMd(position);
        txn.setMasterData(md);
        txn.setDescription(description);
        txn.setTradeDate(Date.valueOf(date));
        txn.setValueDate(Date.valueOf(settlement));
        txn.setSettlement(Date.valueOf(settlement));
        txn.setTxnSide(side);
        txn.setQuantity(quantity);
        txn.setPrice(price);
        txn.setFxRate(fxRate(md, sbc));
        txn.setTxnStatus(TxnStatus.PENDING);
        txn.setTxnStatusPreElab(TxnStatus.PENDING);
        txn.setTxnAcctPhase(AccountingPhase.NONE);
        txn.setTxnOrigin(TxnOrigin.EOD);
        txn.setOriginKey(originKey);
        return txn;
    }

    private static LocalDate toLocal(java.sql.Date date) {
        return date == null ? null : date.toLocalDate();
    }

    private static double nz(Double value) {
        return value == null ? 0. : value;
    }
}
