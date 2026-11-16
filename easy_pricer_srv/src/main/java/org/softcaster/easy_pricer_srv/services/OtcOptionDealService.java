package org.softcaster.easy_pricer_srv.services;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.AssetClassDAO;
import org.softcaster.core.data.CmdFutureMasterData;
import org.softcaster.core.data.FinancialTxn;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.MasterDataDAO;
import org.softcaster.core.data.OtcCmdOptionMasterData;
import org.softcaster.core.data.OtcFxOptionMasterData;
import org.softcaster.core.data.OtcOptionMasterData;
import org.softcaster.core.data.OtcOptionMasterDataDAO;
import org.softcaster.core.dto.FinancialTxnDto;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.OptionCalculator;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.easy_pricer_srv.dto.OtcOptionDealRequest;
import org.softcaster.easy_pricer_srv.dto.OtcOptionDealResponse;
import org.softcaster.easy_pricer_srv.dto.OtcOptionDto;
import org.softcaster.engine.enums.AccountingPhase;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.enums.SettlementType;
import org.softcaster.engine.enums.TxnSide;
import org.softcaster.engine.enums.TxnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Deal capture delle opzioni OTC (OFX su cambio, OCM su commodity): dai
 * termini si costruisce l'anagrafica del contratto, che si prezza (price) o
 * si registra insieme alla transazione (book).
 *
 * Transazione: quantita' 1 (un contratto: nozionale e scadenza sono in
 * anagrafica), prezzo = premio totale nella valuta di regolamento, pagato da
 * chi compra; lato BUY / SELL dal punto di vista della banca.
 */
@Service
public class OtcOptionDealService {

    private static final Logger log = LoggerFactory.getLogger(OtcOptionDealService.class);

    private static final List<String> ASSET_CLASSES = List.of(
            OtcOptionMasterData.FX_ASSET_CLASS, OtcOptionMasterData.COMMODITY_ASSET_CLASS);

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private OtcOptionMasterDataDAO otcOptionDAO;
    @Autowired
    private MasterDataDAO masterDataDAO;
    @Autowired
    private AssetClassDAO assetClassDAO;
    @Autowired
    private OptionCalculator optionCalculator;
    @Autowired
    private FinancialTxnService financialTxnService;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Prezzo dei termini, senza salvare nulla.
     */
    public PricingResponse price(OtcOptionDealRequest request) {
        return transactionTemplate.execute(status -> {
            status.setRollbackOnly();
            OtcOptionMasterData omd = build(request);
            return valuation(omd, tradeDate(request), request);
        });
    }

    /**
     * Valutazione di un contratto registrato.
     */
    public PricingResponse value(String code, LocalDate valuationDate, OtcOptionDealRequest pricing) {
        return transactionTemplate.execute(status -> valuation(otcOptionDAO.getByCode(code),
                valuationDate != null ? valuationDate : marketDataService.getOfficialDate(), pricing));
    }

    /**
     * Registra anagrafica e transazione (PENDING) in un'unica transazione;
     * restituisce anche la valutazione alla data dell'operazione.
     */
    public OtcOptionDealResponse book(OtcOptionDealRequest request) {
        return transactionTemplate.execute(status -> {
            TxnSide side = TxnSide.fromCode(require(request.side, "side"));
            require(request.counterpartyId, "counterpartyId");
            require(request.positionMdId, "positionMdId");
            OtcOptionMasterData omd = otcOptionDAO.create(build(request));
            double premium = premium(request, omd);

            FinancialTxnDto txn = new FinancialTxnDto(0, omd.getDescription(),
                    request.counterpartyId, null, null,
                    request.positionMdId, null,
                    omd.getIdMasterData(), omd.getCode(), omd.getDescription(),
                    TxnStatus.PENDING.getId(), null, null,
                    null, (short) side.getId(),
                    java.sql.Date.valueOf(tradeDate(request)), null,
                    1., premium, null, AccountingPhase.NONE.getId());
            financialTxnService.saveOrUpdateTransaction(txn);

            return new OtcOptionDealResponse(OtcOptionDto.of(omd), side.getCode(), 1., premium,
                    valuationAtBooking(omd, request));
        });
    }

    /**
     * Modifica di un deal registrato: termini dell'anagrafica (stesso id; il
     * codice cambia col sottostante o il tipo) e transazione (PENDING
     * aggiornata, EXECUTED -> TO_AMEND con storno e nuova transazione). Il
     * tipo di sottostante (cambio o commodity) non cambia.
     */
    public OtcOptionDealResponse amend(Integer txnId, OtcOptionDealRequest request) {
        return transactionTemplate.execute(status -> {
            TxnSide side = TxnSide.fromCode(require(request.side, "side"));
            require(request.counterpartyId, "counterpartyId");
            require(request.positionMdId, "positionMdId");
            FinancialTxn txn = financialTxnService.findAmendable(txnId);
            if (!(Hibernate.unproxy(txn.getMasterData()) instanceof OtcOptionMasterData)) {
                throw new IllegalArgumentException("Trade " + txnId + " is not an OTC option");
            }
            OtcOptionMasterData omd = (OtcOptionMasterData) Hibernate.unproxy(
                    otcOptionDAO.getById(txn.getMasterData().getIdMasterData()));
            // dati non presenti nel form: restano quelli del contratto
            if (request.spotLag == null) {
                request.spotLag = omd.getBusinessDays();
            }
            if (request.expiryTime == null) {
                request.expiryTime = omd.getExpiryTime();
                request.expiryTimeZone = omd.getExpiryTimeZone();
            }
            // descrizione generata dai vecchi termini: si rigenera
            if (omd.getDescription().equals(request.description) && omd.getDescription().equals(description(omd))) {
                request.description = null;
            }

            fill(omd, request);
            omd.setCode(omd.contractCode());

            double premium = premium(request, omd);
            financialTxnService.amendOtcDeal(txn, omd.getDescription(), side, request.counterpartyId,
                    request.positionMdId, tradeDate(request), 1., premium);
            return new OtcOptionDealResponse(OtcOptionDto.of(omd), side.getCode(), 1., premium,
                    valuationAtBooking(omd, request));
        });
    }

    public List<OtcOptionDto> findAll() {
        return transactionTemplate.execute(status -> otcOptionDAO.findAllByAssetClasses(ASSET_CLASSES).stream()
                .map(OtcOptionDto::of)
                .toList());
    }

    public OtcOptionDto findByCode(String code) {
        return transactionTemplate.execute(status -> OtcOptionDto.of(otcOptionDAO.getByCode(code)));
    }

    private PricingResponse valuation(OtcOptionMasterData omd, LocalDate valuationDate, OtcOptionDealRequest r) {
        return optionCalculator.otcOptionValuation(omd, valuationDate, r.spot, r.volatility, r.optionPrice,
                r.domesticRCurve, r.foreignRCurve);
    }

    /**
     * Valutazione alla data dell'operazione, informativa: senza volatilita'
     * (indicata o da superficie valida, non scaduta) o curve il deal resta
     * registrato senza valutazione.
     */
    private PricingResponse valuationAtBooking(OtcOptionMasterData omd, OtcOptionDealRequest request) {
        try {
            return valuation(omd, tradeDate(request), request);
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException ex) {
            log.warn("OTC option {} booked without valuation: {}", omd.getCode(), ex.getMessage());
            return null;
        }
    }

    // ------------------------------------------------------------------------
    // -- Costruzione del contratto
    // ------------------------------------------------------------------------

    /**
     * Anagrafica non salvata. Foglia e asset class dal sottostante: coppia di
     * cambi -> OFX (regolamento nella valuta quotata), future su commodity ->
     * OCM (valuta del future).
     */
    private OtcOptionMasterData build(OtcOptionDealRequest r) {
        MasterData underlying = underlying(r);
        OtcOptionMasterData omd;
        if (underlying instanceof ForexMasterData) {
            omd = new OtcFxOptionMasterData();
            omd.setAssetClass(assetClassDAO.findByCode(OtcOptionMasterData.FX_ASSET_CLASS));
        } else if (underlying instanceof CmdFutureMasterData) {
            omd = new OtcCmdOptionMasterData();
            omd.setAssetClass(assetClassDAO.findByCode(OtcOptionMasterData.COMMODITY_ASSET_CLASS));
        } else {
            throw new IllegalArgumentException("Underlying " + underlying.getCode()
                    + " not supported: a currency pair or a commodity future is required");
        }
        fill(omd, r);
        return omd;
    }

    private MasterData underlying(OtcOptionDealRequest r) {
        MasterData underlying = masterDataDAO.findByCode(require(r.underlyingCode, "underlyingCode"));
        if (underlying == null) {
            throw new NoSuchElementException("Underlying not found: " + r.underlyingCode);
        }
        return (MasterData) Hibernate.unproxy(underlying);
    }

    /**
     * Termini della richiesta su un contratto nuovo o registrato.
     */
    private void fill(OtcOptionMasterData omd, OtcOptionDealRequest r) {
        MasterData underlying = underlying(r);
        if (omd instanceof OtcFxOptionMasterData fx) {
            if (!(underlying instanceof ForexMasterData pair)) {
                throw new IllegalArgumentException("The underlying of an FX option must be a currency pair: " + underlying.getCode());
            }
            fx.setUnderlying(pair);
            omd.setCurrency(pair.getCcy());
        } else if (omd instanceof OtcCmdOptionMasterData cmd) {
            if (!(underlying instanceof CmdFutureMasterData future)) {
                throw new IllegalArgumentException("The underlying of a commodity option must be a commodity future: "
                        + underlying.getCode());
            }
            cmd.setUnderlying(future);
            omd.setCurrency(future.getCurrency());
        }

        omd.setOptionType(OptionType.fromCode(require(r.optionType, "optionType")));
        ExerciseStyle style = r.exerciseStyle == null || r.exerciseStyle.isBlank()
                ? ExerciseStyle.EUROPEAN : ExerciseStyle.fromCode(r.exerciseStyle);
        if (style == ExerciseStyle.BERMUDAN) {
            throw new IllegalArgumentException("Bermudan exercise is not supported for OTC options");
        }
        omd.setExerciseStyle(style);
        omd.setSettlementType(SettlementType.fromCode(require(r.settlementType, "settlementType")));
        omd.setStrike(positive(r.strike, "strike"));
        omd.setNotional(positive(r.notional, "notional"));

        LocalDate tradeDate = tradeDate(r);
        LocalDate expiry = require(r.expiryDate, "expiryDate");
        if (expiry.isBefore(tradeDate)) {
            throw new IllegalArgumentException("The expiry (" + expiry + ") is before the trade date (" + tradeDate + ")");
        }
        if (r.deliveryDate != null && r.deliveryDate.isBefore(expiry)) {
            throw new IllegalArgumentException("The delivery date (" + r.deliveryDate + ") is before the expiry (" + expiry + ")");
        }
        omd.setIssueDate(java.sql.Date.valueOf(tradeDate));
        omd.setMaturityDate(java.sql.Date.valueOf(expiry));
        omd.setDeliveryDate(r.deliveryDate == null ? null : java.sql.Date.valueOf(r.deliveryDate));
        omd.setExpiryTime(r.expiryTime);
        omd.setExpiryTimeZone(r.expiryTimeZone == null || r.expiryTimeZone.isBlank() ? null : r.expiryTimeZone);
        omd.setBusinessDays(r.spotLag != null ? r.spotLag
                : underlying.getBusinessDays() != null ? underlying.getBusinessDays() : 2);
        omd.setMultiplier(1.);
        omd.setDescription(r.description != null && !r.description.isBlank() ? r.description : description(omd));
    }

    /**
     * Premio totale nella valuta di regolamento: importo (premium), per unita'
     * di sottostante (premiumUnit x nozionale) oppure percentuale del
     * controvalore allo strike (premiumPercent x nozionale x strike / 100).
     */
    private static double premium(OtcOptionDealRequest r, OtcOptionMasterData omd) {
        int given = (r.premium != null ? 1 : 0) + (r.premiumUnit != null ? 1 : 0) + (r.premiumPercent != null ? 1 : 0);
        if (given != 1) {
            throw new IllegalArgumentException("Provide the premium either as an amount (premium), per unit of underlying "
                    + "(premiumUnit) or as a percentage (premiumPercent)");
        }
        double premium = r.premium != null ? r.premium
                : r.premiumUnit != null ? r.premiumUnit * omd.getNotional()
                : r.premiumPercent / 100. * omd.getNotional() * omd.getStrike();
        if (premium <= 0) {
            throw new IllegalArgumentException("The premium must be positive");
        }
        return premium;
    }

    private static String description(OtcOptionMasterData omd) {
        return (omd.getExerciseStyle() == ExerciseStyle.AMERICAN ? "American " : "")
                + (omd.getOptionType() == OptionType.CALL ? "Call " : "Put ")
                + omd.getUnderlying().getCode() + " K " + omd.getStrike() + " exp " + omd.getExpiryLocalDate()
                + (omd.getSettlementType() == SettlementType.CASH ? " cash" : "");
    }

    private LocalDate tradeDate(OtcOptionDealRequest r) {
        return r.tradeDate != null ? r.tradeDate : marketDataService.getOfficialDate();
    }

    private static double positive(Double value, String name) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("Missing or non-positive field: " + name);
        }
        return value;
    }

    private static <T> T require(T value, String name) {
        if (value == null || (value instanceof String s && s.isBlank())) {
            throw new IllegalArgumentException("Missing required field: " + name);
        }
        return value;
    }
}
