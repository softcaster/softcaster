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
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.AccumulatorType;
import org.softcaster.engine.enums.BarrierObservation;
import org.softcaster.engine.enums.Frequency;
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
            double premium = request.premium == null ? 0. : request.premium;

            AccumulatorMasterData amd = accumulatorDAO.create(build(request));

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
            log.warn("Accumulator {} registrato senza valutazione: {}", amd.getCode(), ex.getMessage());
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
            throw new NoSuchElementException("Sottostante non trovato: " + r.underlyingCode);
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
            throw new IllegalArgumentException("Sottostante " + underlying.getCode()
                    + " non gestito: serve una coppia di cambi o un future su commodity");
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
        amd.setDailyQuantity(require(r.dailyQuantity, "dailyQuantity"));
        LocalDate first = require(r.firstFixingDate, "firstFixingDate");
        LocalDate last = require(r.lastFixingDate, "lastFixingDate");
        amd.setFirstFixingDate(java.sql.Date.valueOf(first));
        amd.setLastFixingDate(java.sql.Date.valueOf(last));
        amd.setGuaranteedFixings(r.guaranteedFixings == null ? 0 : r.guaranteedFixings);
        amd.setKoAccrual(Boolean.TRUE.equals(r.koAccrual));
        amd.setFixingTime(r.fixingTime);
        amd.setFixingTimeZone(r.fixingTimeZone);

        LocalDate tradeDate = tradeDate(r);
        if (!first.isAfter(tradeDate)) {
            throw new IllegalArgumentException("Il primo fixing (" + first + ") deve seguire la data dell'operazione (" + tradeDate + ")");
        }
        int spotLag = r.spotLag != null ? r.spotLag
                : underlying.getBusinessDays() != null ? underlying.getBusinessDays() : 2;
        List<AccumulatorFixing> fixings = AccumulatorCalculator.schedule(first, last, frequency, spotLag,
                new Calendar(amd.getCurrencyList()));
        amd.getFixings().addAll(fixings);

        amd.setIssueDate(java.sql.Date.valueOf(tradeDate));
        amd.setMaturityDate(fixings.get(fixings.size() - 1).getSettlementDate());
        amd.setBusinessDays(spotLag);
        amd.setMultiplier(1.);
        amd.setDescription(r.description != null && !r.description.isBlank() ? r.description : description(amd));
        return amd;
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
            throw new IllegalArgumentException("Campo obbligatorio mancante: " + name);
        }
        return value;
    }
}
