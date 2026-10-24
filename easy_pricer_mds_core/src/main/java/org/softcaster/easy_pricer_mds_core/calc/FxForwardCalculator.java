package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.engine.analytics.FxForwardPricer;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.FxForwardInput;
import org.softcaster.engine.dto.FxForwardResult;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
// package e import come nel file originale
// + import java.time.LocalDate;
// + import java.util.Objects;
/**
 * Costruisce l'input del pricer FX forward a partire dai dati di mercato
 * risolti in mds_core e converte il risultato nel DTO di output.
 *
 * Creato come @Bean in MdsCoreAutoConfiguration: nessuna annotazione @Service.
 */
// package e import come nel file originale
// + import java.time.LocalDate;
// + import java.util.NoSuchElementException;
// + import java.util.Objects;

/**
 * Costruisce l'input del pricer FX forward/future a partire da anagrafica e
 * dati di mercato di mds_core, e converte il risultato nel DTO di output.
 *
 * Creato come @Bean in MdsCoreAutoConfiguration: nessuna annotazione @Service.
 *
 * Errori:
 *  - NoSuchElementException   -> strumento o curva non trovati (REST: 404)
 *  - IllegalArgumentException -> dati non validi (REST: 400)
 */
public class FxForwardCalculator {

    private final FxFutureMasterDataDAO fxFutureMasterDataDAO;
    private final MarketDataService marketDataService;
    private final FxForwardPricer fxForwardPricer;

    public FxForwardCalculator(FxFutureMasterDataDAO fxFutureMasterDataDAO,
                               MarketDataService marketDataService,
                               FxForwardPricer fxForwardPricer) {
        this.fxFutureMasterDataDAO = Objects.requireNonNull(fxFutureMasterDataDAO);
        this.marketDataService = Objects.requireNonNull(marketDataService);
        this.fxForwardPricer = Objects.requireNonNull(fxForwardPricer);
    }

    // ------------------------------------------------------------------------
    // -- Costruzione input
    // ------------------------------------------------------------------------

    /**
     * Input da dati gia' risolti.
     *
     * @param instrumentId
     * @param valuationDate data di regolamento dello spot (oggi + business days)
     * @param deliveryDate
     * @param spot
     * @param domesticCurve
     * @param foreignCurve
     * @param contract      null se serve solo il cambio forward,
     *                      ForwardContract per un forward OTC, FutureContract per un future
     * @return 
     */
    public FxForwardInput getFxForwardInput(String instrumentId,
                                            LocalDate valuationDate,
                                            LocalDate deliveryDate,
                                            double spot,
                                            DiscountCurve domesticCurve,
                                            DiscountCurve foreignCurve,
                                            FxForwardInput.FxContract contract) {
        // la validazione (spot > 0, date, curve non null) e' nel costruttore del record
        return new FxForwardInput(instrumentId, valuationDate, deliveryDate,
                spot, domesticCurve, foreignCurve, contract);
    }

    /**
     * Input per un future in anagrafica, senza posizione: calcola solo il prezzo teorico.
     * @param code
     * @param officialDate
     * @param spotPrice
     * @param domesticCurveCode
     * @param foreignCurveCode
     * @return 
     */
    public FxForwardInput getFxForwardInput(String code, LocalDate officialDate, double spotPrice,
                                            String domesticCurveCode, String foreignCurveCode) {
        return getFxForwardInput(code, officialDate, spotPrice, domesticCurveCode, foreignCurveCode, null);
    }

    /**
     * Input per un future in anagrafica, con eventuale posizione da valutare.
     *
     * @param code
     * @param officialDate
     * @param spotPrice
     * @param domesticCurveCode
     * @param foreignCurveCode
     * @param contract null, oppure FutureContract con numero di contratti e prezzo di riferimento
     * @return 
     */
    public FxForwardInput getFxForwardInput(String code, LocalDate officialDate, double spotPrice,
                                            String domesticCurveCode, String foreignCurveCode,
                                            FxForwardInput.FxContract contract) {
        FxFutureMasterData fmd = fxFutureMasterDataDAO.getByCodeOrIsin(code);

        LocalDate valuationDate = CashFlowInstrumentCalculator.valuationDate(fmd, officialDate);
        
        if(domesticCurveCode == null || domesticCurveCode.isBlank())
            domesticCurveCode = fmd.getUnderlying().getCcyIrc();
        if(foreignCurveCode == null || foreignCurveCode.isBlank())
            foreignCurveCode = fmd.getUnderlying().getBcyIrc();
        DiscountCurve domesticCurve = loadCurve(domesticCurveCode, valuationDate);
        DiscountCurve foreignCurve = loadCurve(foreignCurveCode, valuationDate);

        LocalDate deliveryDate = deliveryDate(fmd);

        return new FxForwardInput(code, valuationDate, deliveryDate,
                spotPrice, domesticCurve, foreignCurve, contract);
    }

    // ------------------------------------------------------------------------
    // -- Valutazione
    // ------------------------------------------------------------------------

    public FxForwardResult fxFwdValuation(FxForwardInput input) {
        return fxForwardPricer.calculate(Objects.requireNonNull(input, "input"));
    }

    /**
     * Entry point REST: referencePrice e' il cambio a pronti; le curve non
     * indicate si prendono dalla coppia di valute del future.
     *
     * @param request
     * @return
     */
    public PricingResponse fxFwdValuation(PricingRequest request) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(request.referenceDate, "referenceDate");
        Objects.requireNonNull(request.referencePrice, "referencePrice");

        FxForwardInput input = getFxForwardInput(request.isin, request.referenceDate,
                request.referencePrice, request.domesticRCurve, request.foreignRCurve);
        FxForwardResult result = fxFwdValuation(input);

        PricingResponse response = new PricingResponse();
        response.theoreticalPrice = result.forwardRate();
        response.forwardPoints = result.forwardPoints();
        return response;
    }

    // ------------------------------------------------------------------------
    // -- Helper privati
    // ------------------------------------------------------------------------

    /**
     * Data di consegna della scadenza; se non e' in anagrafica, regolamento
     * spot dell'ultimo giorno di negoziazione (per il CME 6E: due giorni
     * lavorativi dopo, il terzo mercoledi' del mese).
     */
    private static LocalDate deliveryDate(FxFutureMasterData fmd) {
        if (fmd.getDeliveryLocalDate() != null) {
            return fmd.getDeliveryLocalDate();
        }
        LocalDate lastTrading = fmd.getLastTradingLocalDate();
        if (lastTrading == null) {
            throw new IllegalArgumentException("Ultimo giorno di negoziazione mancante per " + fmd.getCode());
        }
        return CashFlowInstrumentCalculator.valuationDate(fmd, lastTrading);
    }

    /**
     * Recupera la curva e controlla che sia utilizzabile alla valuationDate:
     * YieldCurve rifiuta date precedenti alla propria data di riferimento.
     */
    private DiscountCurve loadCurve(String curveCode, LocalDate valuationDate) {
        if (curveCode == null || curveCode.isBlank()) {
            throw new IllegalArgumentException("Codice curva mancante");
        }
        DiscountCurve curve = marketDataService.getDiscountCurve(curveCode);
        if (curve == null) {
            throw new NoSuchElementException("Curva non trovata: " + curveCode);
        }
        if (curve.getValuationDate().isAfter(valuationDate)) {
            throw new IllegalArgumentException("La curva " + curveCode + " ha data "
                    + curve.getValuationDate() + ", successiva alla valuationDate " + valuationDate);
        }
        return curve;
    }
}