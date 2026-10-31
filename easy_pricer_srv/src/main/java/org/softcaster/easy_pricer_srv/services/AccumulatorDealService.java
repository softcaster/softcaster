package org.softcaster.easy_pricer_srv.services;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.AccumulatorFixing;
import org.softcaster.core.data.AccumulatorMasterData;
import org.softcaster.core.data.AccumulatorMasterDataDAO;
import org.softcaster.core.data.AssetClassDAO;
import org.softcaster.core.data.CmdFutureMasterData;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.easy_pricer_mds_core.Calendar;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.AccumulatorCalculator;
import org.softcaster.easy_pricer_mds_core.dto.AccumulatorPricingResponse;
import org.softcaster.easy_pricer_srv.dto.AccumulatorDealRequest;
import org.softcaster.easy_pricer_srv.dto.AccumulatorDealResponse;
import org.softcaster.easy_pricer_srv.dto.AccumulatorDto;
import org.softcaster.engine.cashflow.AccumulatorFixingDate;
import org.softcaster.engine.cashflow.AccumulatorScheduleGenerator;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.Frequency;
import org.softcaster.engine.enums.GuaranteeType;
import org.softcaster.engine.enums.KnockInEffect;
import org.softcaster.engine.enums.SettlementType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Deal capture degli accumulator / decumulator OTC: dai termini del contratto
 * si costruiscono anagrafica e calendario dei fixing, che si prezzano
 * (price) o si registrano insieme alla transazione (book).
 *
 * Transazione: quantita' 1 (un contratto: nozionale e calendario sono in
 * anagrafica), prezzo = premio iniziale nella valuta di regolamento, pagato
 * da chi compra; lato BUY / SELL dal punto di vista della banca.
 */
@Service
public class AccumulatorDealService {

    private static final Logger log = LoggerFactory.getLogger(AccumulatorDealService.class);

    private static final List<String> ASSET_CLASSES = List.of(
            AccumulatorMasterData.FX_ASSET_CLASS, AccumulatorMasterData.COMMODITY_ASSET_CLASS);

    private final AccumulatorScheduleGenerator scheduleGenerator = new AccumulatorScheduleGenerator();

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private AccumulatorMasterDataDAO accumulatorDAO;
    @Autowired
    private MasterDataDAO masterDataDAO;
    @Autowired
    private AssetClassDAO assetClassDAO;
    @Autowired
    private AccumulatorCalculator accumulatorCalculator;
    @Autowired
    private FinancialTxnService financialTxnService;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Prezzo dei termini, senza salvare nulla.
     */
    public AccumulatorPricingResponse price(AccumulatorDealRequest request) {
        return transactionTemplate.execute(status -> {
            status.setRollbackOnly();
            AccumulatorMasterData amd = build(request);
            return accumulatorCalculator.accumulatorValuation(amd, tradeDate(request), request.spot,
                    request.volatility, request.domesticRCurve, request.foreignRCurve, request.paths);
        });
    }

    /**
     * Valutazione di un contratto registrato.
     */
    public AccumulatorPricingResponse value(String code, LocalDate valuationDate, AccumulatorDealRequest pricing) {
        return transactionTemplate.execute(status -> {
            AccumulatorMasterData amd = accumulatorDAO.getByCode(code);
            return accumulatorCalculator.accumulatorValuation(amd, valuationDate, pricing.spot,
                    pricing.volatility, pricing.domesticRCurve, pricing.foreignRCurve, pricing.paths);
        });
    }

    /**
     * Registra anagrafica, calendario e transazione (PENDING) in un'unica
     * transazione; con la volatilita' restituisce anche la valutazione.
     */
    public AccumulatorDealResponse book(AccumulatorDealRequest request) {
        return transactionTemplate.execute(status -> {
            TxnSide side = TxnSide.fromCode(require(request.side, "side"));
            require(request.counterpartyId, "counterpartyId");
            require(request.positionMdId, "positionMdId");
            AccumulatorMasterData amd = accumulatorDAO.create(build(request));
            double premium = premium(request, amd);

            FinancialTxnDto txn = new FinancialTxnDto(0, amd.getDescription(),
                    request.counterpartyId, null, null,
                    request.positionMdId, null,
                    amd.getIdMasterData(), amd.getCode(), amd.getDescription(),
                    TxnStatus.PENDING.getId(), null, null,
                    null, (short) side.getId(),
                    java.sql.Date.valueOf(tradeDate(request)), null,
                    1., premium, null, AccountingPhase.NONE.getId());
            financialTxnService.saveOrUpdateTransaction(txn);

            return new AccumulatorDealResponse(AccumulatorDto.of(amd, true), side.getCode(), 1., premium,
                    valuationAtBooking(amd, request));
        });
    }

    /**
     * Valutazione alla data dell'operazione, informativa: senza volatilita'
     * (indicata o da superficie) o curve il deal resta registrato senza
     * valutazione.
     */
    private AccumulatorPricingResponse valuationAtBooking(AccumulatorMasterData amd, AccumulatorDealRequest request) {
        try {
            return accumulatorCalculator.accumulatorValuation(amd, tradeDate(request), request.spot,
                    request.volatility, request.domesticRCurve, request.foreignRCurve, request.paths);
        } catch (IllegalArgumentException | NoSuchElementException ex) {
            log.warn("Accumulator {} booked without valuation: {}", amd.getCode(), ex.getMessage());
            return null;
        }
    }

    public List<AccumulatorDto> findAll() {
        return transactionTemplate.execute(status -> accumulatorDAO.findAllByAssetClasses(ASSET_CLASSES).stream()
                .map(amd -> AccumulatorDto.of(amd, false))
                .toList());
    }

    public AccumulatorDto findByCode(String code) {
        return transactionTemplate.execute(status -> AccumulatorDto.of(accumulatorDAO.getByCode(code), true));
    }

    // ------------------------------------------------------------------------
    // -- Costruzione del contratto
    // ------------------------------------------------------------------------

    /**
     * Anagrafica non salvata con il calendario dei fixing. Asset class dal
     * sottostante: coppia di cambi -> FXA (regolamento nella valuta quotata),
     * future su commodity -> CMA (valuta del future).
     */
    private AccumulatorMasterData build(AccumulatorDealRequest r) {
        MasterData underlying = masterDataDAO.findByCode(require(r.underlyingCode, "underlyingCode"));
        if (underlying == null) {
            throw new NoSuchElementException("Underlying not found: " + r.underlyingCode);
        }
        underlying = (MasterData) Hibernate.unproxy(underlying);

        AccumulatorMasterData amd = new AccumulatorMasterData();
        amd.setUnderlying(underlying);
        if (underlying instanceof ForexMasterData pair) {
            amd.setAssetClass(assetClassDAO.findByCode(AccumulatorMasterData.FX_ASSET_CLASS));
            amd.setCurrency(pair.getCcy());
        } else if (underlying instanceof CmdFutureMasterData) {
            amd.setAssetClass(assetClassDAO.findByCode(AccumulatorMasterData.COMMODITY_ASSET_CLASS));
            amd.setCurrency(underlying.getCurrency());
        } else {
            throw new IllegalArgumentException("Underlying " + underlying.getCode()
                    + " not supported: a currency pair or a commodity future is required");
        }

        amd.setAccumulatorType(AccumulatorType.fromCode(require(r.accumulatorType, "accumulatorType")));
        amd.setSettlementType(SettlementType.fromCode(require(r.settlementType, "settlementType")));
        Frequency frequency = Frequency.fromCode(require(r.settlementFrequency, "settlementFrequency"));
        amd.setSettlementFrequency(frequency);
        amd.setStrike(require(r.strike, "strike"));
        amd.setKoBarrier(require(r.koBarrier, "koBarrier"));
        amd.setKoObservation(r.koObservation == null ? BarrierObservation.FIXING : BarrierObservation.fromCode(r.koObservation));
        amd.setKiBarrier(r.kiBarrier);
        amd.setKiEffect(r.kiEffect == null || r.kiEffect.isBlank() ? null : KnockInEffect.fromCode(r.kiEffect));
        amd.setLeverage(r.leverage == null ? 1. : r.leverage);
        LocalDate first = require(r.firstFixingDate, "firstFixingDate");
        LocalDate last = require(r.lastFixingDate, "lastFixingDate");
        amd.setFirstFixingDate(java.sql.Date.valueOf(first));
        amd.setLastFixingDate(java.sql.Date.valueOf(last));
        int guaranteedPeriods = r.guaranteedPeriods == null ? 0 : r.guaranteedPeriods;
        if (guaranteedPeriods < 0) {
            throw new IllegalArgumentException("Negative guaranteedPeriods");
        }
        amd.setGuaranteedPeriods(guaranteedPeriods);
        if (guaranteedPeriods == 0) {
            if (r.guaranteeType != null && !r.guaranteeType.isBlank() && !"NONE".equals(r.guaranteeType)) {
                throw new IllegalArgumentException("guaranteeType " + r.guaranteeType + " without guaranteed periods");
            }
            amd.setGuaranteeType(GuaranteeType.NONE);
        } else {
            GuaranteeType type = r.guaranteeType == null || r.guaranteeType.isBlank()
                    ? GuaranteeType.LEVERAGED : GuaranteeType.fromCode(r.guaranteeType);
            if (type == GuaranteeType.NONE) {
                throw new IllegalArgumentException("guaranteeType NONE with " + guaranteedPeriods + " guaranteed periods");
            }
            amd.setGuaranteeType(type);
        }
        amd.setKoAccrual(Boolean.TRUE.equals(r.koAccrual));
        amd.setFixingTime(r.fixingTime);
        amd.setFixingTimeZone(r.fixingTimeZone);

        LocalDate tradeDate = tradeDate(r);
        // il primo fixing puo' essere il giorno dell'operazione (fixing al cut, dopo la conclusione)
        if (first.isBefore(tradeDate)) {
            throw new IllegalArgumentException("The first fixing (" + first + ") is before the trade date (" + tradeDate + ")");
        }
        int spotLag = r.spotLag != null ? r.spotLag
                : underlying.getBusinessDays() != null ? underlying.getBusinessDays() : 2;
        // fixing sul calendario della valuta del fixing (base della coppia), regolamento su entrambe
        Calendar settlementCalendar = new Calendar(amd.getCurrencyList());
        Calendar fixingCalendar = underlying instanceof ForexMasterData pair ? new Calendar(pair.getBcy()) : settlementCalendar;
        List<AccumulatorFixingDate> schedule = scheduleGenerator.generate(first, last, frequency, spotLag,
                fixingCalendar, settlementCalendar);
        schedule.forEach(d -> amd.getFixings().add(new AccumulatorFixing(d.fixingDate(), d.settlementDate())));
        amd.setDailyQuantity(dailyQuantity(r, schedule.size()));

        amd.setIssueDate(java.sql.Date.valueOf(tradeDate));
        amd.setMaturityDate(java.sql.Date.valueOf(schedule.get(schedule.size() - 1).settlementDate()));
        amd.setBusinessDays(spotLag);
        amd.setMultiplier(1.);
        amd.setDescription(r.description != null && !r.description.isBlank() ? r.description : description(amd));
        return amd;
    }

    /**
     * Quantita' per fixing: indicata (dailyQuantity) oppure quantita' massima
     * totale senza leva divisa per il numero di fixing (maxQuantity 100.000 su
     * 22 fixing -> 4.545,45).
     */
    private static double dailyQuantity(AccumulatorDealRequest r, int fixings) {
        boolean daily = r.dailyQuantity != null && r.dailyQuantity > 0;
        boolean max = r.maxQuantity != null && r.maxQuantity > 0;
        if (daily == max) {
            throw new IllegalArgumentException("Provide either the quantity per fixing (dailyQuantity) or the maximum quantity (maxQuantity)");
        }
        return daily ? r.dailyQuantity : r.maxQuantity / fixings;
    }

    /**
     * Premio iniziale per contratto: importo (premium) oppure percentuale
     * (premiumPercent) dell'importo massimo nella seconda valuta = quantita'
     * massima x strike (-0,282713% di 115.000 USD -> -325,12 USD).
     */
    private static double premium(AccumulatorDealRequest r, AccumulatorMasterData amd) {
        boolean amount = r.premium != null && r.premium != 0.;
        if (r.premiumPercent == null) {
            return amount ? r.premium : 0.;
        }
        if (amount) {
            throw new IllegalArgumentException("Provide the premium either as an amount (premium) or as a percentage (premiumPercent)");
        }
        return r.premiumPercent / 100. * amd.getMaxQuantity() * amd.getStrike();
    }

    private static String description(AccumulatorMasterData amd) {
        return (amd.getAccumulatorType() == AccumulatorType.ACCUMULATOR ? "Accumulator " : "Decumulator ")
                + amd.getUnderlying().getCode() + " K " + amd.getStrike() + " KO " + amd.getKoBarrier()
                + (amd.getKiBarrier() != null ? " KI " + amd.getKiBarrier() : "")
                + " x" + amd.getLeverage() + " " + amd.getFirstFixingLocalDate() + "/" + amd.getLastFixingLocalDate();
    }

    private LocalDate tradeDate(AccumulatorDealRequest r) {
        return r.tradeDate != null ? r.tradeDate : marketDataService.getOfficialDate();
    }

    private static <T> T require(T value, String name) {
        if (value == null || (value instanceof String s && s.isBlank())) {
            throw new IllegalArgumentException("Missing required field: " + name);
        }
        return value;
    }
}
