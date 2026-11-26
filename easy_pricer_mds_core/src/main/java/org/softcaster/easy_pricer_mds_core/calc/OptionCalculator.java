package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.softcaster.core.data.AcDcBcyLinkDAO;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.FutureMasterData;
import org.softcaster.core.data.FutureOptionMasterData;
import org.softcaster.core.data.FutureOptionMasterDataDAO;
import org.softcaster.core.data.FxFutureMasterData;
import org.softcaster.core.data.FxFutureMasterDataDAO;
import org.softcaster.core.data.IndexMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.OptionMasterData;
import org.softcaster.core.data.OtcCmdOptionMasterData;
import org.softcaster.core.data.OtcFxOptionMasterData;
import org.softcaster.core.data.OtcOptionMasterData;
import org.softcaster.core.data.SecurityMasterData;
import org.softcaster.core.data.SpotOptionMasterData;
import org.softcaster.core.data.SpotOptionMasterDataDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.PricingRequest;
import org.softcaster.easy_pricer_mds_core.dto.PricingResponse;
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.analytics.CRRBinomialPricer;
import org.softcaster.engine.analytics.GarmanKohlhagenPricer;
import org.softcaster.engine.analytics.ImpliedVolatility;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.curve.YieldCurve;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.FxOptionInput;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.dto.OptionInput;
import org.softcaster.engine.dto.OptionResult;
import org.softcaster.engine.dto.XRBForwardInput;
import org.softcaster.engine.enums.CurveRole;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.PremiumStyle;
import org.softcaster.engine.enums.RequestType;

/**
 * Valutazione delle opzioni in anagrafica: costruisce l'input del pricer
 * dell'engine da anagrafica e dati di mercato e converte il risultato nel DTO
 * di output.
 *
 * Il modello dipende dalla serie, non da chi chiama:
 *  - opzione su future (FFO, BFO)  -> Black-76 sul prezzo del future
 *  - opzione su cambio (FXO)       -> Garman-Kohlhagen su spot e curve della coppia
 *  - opzione su titolo (BDO)       -> Black-76 sul prezzo secco a termine del titolo
 *                                     alla consegna (bond forward con la curva repo)
 *  - opzione su indice (IXO)       -> Black-76 sul forward dell'indice (spot, curva
 *                                     e dividend yield dell'indice)
 *  - americane (tutti i rami)      -> albero binomiale CRR sullo stesso input
 *  - OTC su cambio (OFX)           -> come FXO, nozionale e consegna del contratto
 *  - OTC su commodity (OCM)        -> Black-76 sul prezzo del future, premio scontato
 *
 * Volatilita': indicata, implicita dal premio di mercato oppure, se mancano
 * entrambi, dalla superficie del sottostante della serie (VolatilityProvider)
 * alla scadenza e allo strike della serie.
 *
 * Premio: "futures-style" (regolato con i margini) non si sconta,
 * "equity-style" si sconta con la curva o il tasso indicati; per le opzioni su
 * FX future, senza indicazioni, con la curva della valuta di quotazione della
 * coppia (USD per EUR/USD).
 *
 * Creato come @Bean in MdsCoreAutoConfiguration: nessuna annotazione @Service.
 *
 * Errori:
 *  - NoSuchElementException   -> strumento o curva non trovati (REST: 404)
 *  - IllegalArgumentException -> dati non validi (REST: 400)
 */
public class OptionCalculator {

    public static final String BLACK_76 = "Black-76";
    public static final String GARMAN_KOHLHAGEN = "Garman-Kohlhagen";
    public static final String CRR = "CRR binomial";

    private final FutureOptionMasterDataDAO futureOptionMasterDataDAO;
    private final SpotOptionMasterDataDAO spotOptionMasterDataDAO;
    private final FxFutureMasterDataDAO fxFutureMasterDataDAO;
    private final BondForwardCalculator bondForwardCalculator;
    private final MarketDataService marketDataService;
    private final Black76Pricer black76Pricer;
    private final GarmanKohlhagenPricer garmanKohlhagenPricer;
    private final CRRBinomialPricer crrPricer;
    private final VolatilityProvider volatilityProvider;
    private final FxForwardCalculator fxForwardCalculator;
    private final AcDcBcyLinkDAO acDcBcyLinkDAO;

    public OptionCalculator(FutureOptionMasterDataDAO futureOptionMasterDataDAO,
                            SpotOptionMasterDataDAO spotOptionMasterDataDAO,
                            FxFutureMasterDataDAO fxFutureMasterDataDAO,
                            BondForwardCalculator bondForwardCalculator,
                            MarketDataService marketDataService,
                            Black76Pricer black76Pricer,
                            GarmanKohlhagenPricer garmanKohlhagenPricer,
                            CRRBinomialPricer crrPricer,
                            VolatilityProvider volatilityProvider,
                            FxForwardCalculator fxForwardCalculator,
                            AcDcBcyLinkDAO acDcBcyLinkDAO) {
        this.fxForwardCalculator = Objects.requireNonNull(fxForwardCalculator);
        this.acDcBcyLinkDAO = Objects.requireNonNull(acDcBcyLinkDAO);
        this.futureOptionMasterDataDAO = Objects.requireNonNull(futureOptionMasterDataDAO);
        this.spotOptionMasterDataDAO = Objects.requireNonNull(spotOptionMasterDataDAO);
        this.fxFutureMasterDataDAO = Objects.requireNonNull(fxFutureMasterDataDAO);
        this.bondForwardCalculator = Objects.requireNonNull(bondForwardCalculator);
        this.marketDataService = Objects.requireNonNull(marketDataService);
        this.black76Pricer = Objects.requireNonNull(black76Pricer);
        this.garmanKohlhagenPricer = Objects.requireNonNull(garmanKohlhagenPricer);
        this.crrPricer = Objects.requireNonNull(crrPricer);
        this.volatilityProvider = Objects.requireNonNull(volatilityProvider);
    }

    /**
     * Volatilita' della serie dalla superficie del suo sottostante, alla
     * scadenza e allo strike della serie; vuota se il sottostante non ha
     * superficie alla data.
     *
     * @param code codice o ISIN della serie
     * @param valuationDate
     * @return
     */
    public Optional<VolatilityProvider.SurfaceData> surface(String code, LocalDate valuationDate) {
        OptionMasterData omd = findSeries(code);
        return volatilityProvider.surface(omd.getUnderlying().getIdMasterData(), valuationDate);
    }

    /**
     * Volatilita' della serie alla sua scadenza e al suo strike: dalla
     * superficie del sottostante; per un'opzione su future su cambi senza
     * superficie propria, da quella della coppia. Quella superficie e' in
     * termini di forward della coppia alla consegna OTC
     * (FxForwardCalculator.optionForward): lo strike
     * del future si porta in quei termini, K x F_coppia / F_future, a parita' di
     * moneyness.
     * @param code
     * @param valuationDate
     * @return 
     */
    public Optional<Double> surfaceVolatility(String code, LocalDate valuationDate) {
        OptionMasterData omd = findSeries(code);
        if (omd.getStrike() == null || omd.getExpiryLocalDate() == null) {
            return Optional.empty();
        }
        Optional<Double> own = volatilityProvider.surface(omd.getUnderlying().getIdMasterData(), valuationDate)
                .map(s -> s.surface().volatility(omd.getExpiryLocalDate(), omd.getStrike()));
        if (own.isPresent() || !(omd instanceof FutureOptionMasterData)) {
            return own;
        }
        FutureMasterData future = (FutureMasterData) Hibernate.unproxy(omd.getUnderlying());
        if (!(future instanceof FxFutureMasterData)) {
            return own;
        }
        FxFutureMasterData fxFuture = fxFutureMasterDataDAO.getByCodeOrIsin(future.getCode());
        ForexMasterData pair = (ForexMasterData) Hibernate.unproxy(fxFuture.getUnderlying());
        Optional<VolatilityProvider.SurfaceData> pairSurface = volatilityProvider.surface(pair.getIdMasterData(), valuationDate);
        if (pairSurface.isEmpty()) {
            return own;
        }
        double futurePrice = price(null, future);
        double pairForward = fxForwardCalculator.optionForward(pair, valuationDate, omd.getExpiryLocalDate());
        double strike = omd.getStrike() * pairForward / futurePrice;
        return Optional.of(pairSurface.get().surface().volatility(omd.getExpiryLocalDate(), strike));
    }

    // ------------------------------------------------------------------------
    // -- Costruzione input
    // ------------------------------------------------------------------------

    /**
     * Input per una serie qualunque: sceglie il ramo (su future o su
     * strumento secco) dal codice.
     *
     * @param code          codice o ISIN della serie
     * @param valuationDate data di valutazione (trade date)
     * @param underlyingPrice prezzo del sottostante (future, cambio, prezzo secco del titolo);
     *                      null o <= 0 = quotazione in anagrafica
     * @param curveCode     curva per il premio (cambi: curva domestica; titoli: curva repo)
     * @param rate          tasso se la curva non e' indicata (titoli: repo ACT/360 semplice)
     * @param foreignCurveCode solo cambi: curva della valuta estera
     * @param volatility    volatilita' annua in decimale
     * @return
     */
    public OptionInput getOptionInput(String code, LocalDate valuationDate, Double underlyingPrice,
                                      String curveCode, Double rate, String foreignCurveCode, double volatility) {
        OptionMasterData omd = findSeries(code);
        if (omd instanceof FutureOptionMasterData fomd) {
            return getFutureOptionInput(fomd, valuationDate, underlyingPrice, curveCode, rate, volatility);
        }
        SpotOptionMasterData somd = (SpotOptionMasterData) omd;
        MasterData underlying = (MasterData) Hibernate.unproxy(somd.getUnderlying());
        if (underlying instanceof ForexMasterData fx) {
            return getFxOptionInput(somd, fx, valuationDate, underlyingPrice, curveCode, foreignCurveCode, volatility);
        }
        if (underlying instanceof SecurityMasterData bond) {
            return getBondOptionInput(somd, bond, valuationDate, underlyingPrice, curveCode, rate, volatility);
        }
        if (underlying instanceof IndexMasterData index) {
            return getIndexOptionInput(somd, index, valuationDate, underlyingPrice, curveCode, rate, volatility);
        }
        throw new IllegalArgumentException("Underlying " + underlying.getCode() + " not supported for " + omd.getCode());
    }

    /**
     * Black-76 / CRR sul forward dell'indice alla scadenza della serie:
     * F = spot x exp(-q T) / DF(T), con q il dividend yield continuo
     * dell'indice e DF dalla curva indicata, dal tasso indicato o, di default,
     * dalla curva dell'indice. Il premio (equity-style) si sconta con la
     * stessa curva.
     */
    private ForwardOptionInput getIndexOptionInput(SpotOptionMasterData omd, IndexMasterData index, LocalDate valuationDate,
                                                   Double spotPrice, String curveCode, Double rate, double volatility) {
        OptionContract contract = contract(omd);
        double spot = price(spotPrice, index);

        DiscountCurve curve;
        if (curveCode != null && !curveCode.isBlank()) {
            curve = loadCurve(curveCode, valuationDate);
        } else if (rate != null) {
            curve = new FlatCurve(valuationDate, rate);
        } else if (index.getIrc() != null && !index.getIrc().isBlank()) {
            curve = loadCurve(index.getIrc(), valuationDate);
        } else {
            throw new IllegalArgumentException("Missing discount curve of index " + index.getCode()
                    + " (irc) and no curve or rate in the request");
        }

        LocalDate expiry = contract.expiryDate().isAfter(valuationDate) ? contract.expiryDate() : valuationDate;
        double t = ChronoUnit.DAYS.between(valuationDate, expiry) / 365.0;
        double q = index.getDividendYield() == null ? 0. : index.getDividendYield();
        double forward = spot * Math.exp(-q * t) / curve.getDiscountFactor(expiry);

        boolean discounted = premiumStyle(omd).isDiscounted();
        DiscountCurve premiumCurve = discounted ? curve : new FlatCurve(valuationDate, 0.0);
        return new ForwardOptionInput(omd.getCode(), valuationDate, forward, premiumCurve, discounted, contract, volatility);
    }

    /**
     * Black-76 / CRR sul prezzo del future sottostante.
     */
    private ForwardOptionInput getFutureOptionInput(FutureOptionMasterData omd, LocalDate valuationDate,
                                                    Double futurePrice, String curveCode, Double rate, double volatility) {
        // dentro una transazione gia' aperta (MtM) il future puo' essere un proxy di FutureMasterData
        FutureMasterData underlying = (FutureMasterData) Hibernate.unproxy(omd.getUnderlying());
        double forward = price(futurePrice, underlying);

        boolean discounted = premiumStyle(omd).isDiscounted();
        DiscountCurve curve;
        if (!discounted) {
            curve = new FlatCurve(valuationDate, 0.0);   // premio regolato con i margini: nessuno sconto
        } else if (curveCode != null && !curveCode.isBlank()) {
            curve = loadCurve(curveCode, valuationDate);
        } else if (rate != null) {
            curve = new FlatCurve(valuationDate, rate);
        } else if (underlying instanceof FxFutureMasterData) {
            // premio nella valuta di quotazione della coppia; sottostante ricaricato con la coppia (LAZY sulla testata)
            FxFutureMasterData fmd = fxFutureMasterDataDAO.getByCodeOrIsin(underlying.getCode());
            curve = loadCurve(fmd.getUnderlying().getCcyIrc(), valuationDate);
        } else {
            throw new IllegalArgumentException("Missing premium discount curve or rate (domesticRCurve / domesticRate) for "
                    + omd.getCode());
        }

        return new ForwardOptionInput(omd.getCode(), valuationDate, forward, curve, discounted,
                contract(omd), volatility);
    }

    /**
     * Garman-Kohlhagen / CRR: spot della coppia, curve domestica (valuta di
     * quotazione) ed estera (valuta base), di default quelle della coppia.
     */
    private FxOptionInput getFxOptionInput(SpotOptionMasterData omd, ForexMasterData pair, LocalDate valuationDate,
                                           Double spotPrice, String domesticCurveCode, String foreignCurveCode,
                                           double volatility) {
        return fxOptionInput(omd.getCode(), contract(omd), pair, null, omd.getDeliveryLocalDate(), valuationDate,
                spotPrice, domesticCurveCode, foreignCurveCode, volatility);
    }

    /**
     * Input Garman-Kohlhagen / CRR comune a serie quotate e opzioni OTC:
     * spot della coppia, curve domestica (valuta quotata) ed estera (valuta
     * base), di default quelle della coppia. Convenzione FX: premio al
     * regolamento dello spot, payoff alla consegna (indicata, altrimenti
     * scadenza + spot lag).
     *
     * @param spotLag giorni di regolamento; null = quelli della coppia (2 se mancano)
     */
    private FxOptionInput fxOptionInput(String code, OptionContract contract, ForexMasterData pair, Integer spotLag,
                                        LocalDate deliveryDate, LocalDate valuationDate, Double spotPrice,
                                        String domesticCurveCode, String foreignCurveCode, double volatility) {
        double spot = price(spotPrice, pair);
        String domestic = domesticCurveCode != null && !domesticCurveCode.isBlank() ? domesticCurveCode : pair.getCcyIrc();
        String foreign = foreignCurveCode != null && !foreignCurveCode.isBlank() ? foreignCurveCode : pair.getBcyIrc();
        int days = spotLag != null ? spotLag : pair.getBusinessDays() == null ? 2 : pair.getBusinessDays();
        org.softcaster.easy_pricer_mds_core.Calendar calendar = new org.softcaster.easy_pricer_mds_core.Calendar(pair.getCurrencyList());
        LocalDate spotDate = calendar.getNextBusinessDate(valuationDate, days);
        LocalDate delivery = deliveryDate != null ? deliveryDate
                : calendar.getNextBusinessDate(contract.expiryDate(), days);
        if (delivery.isBefore(spotDate)) {
            delivery = spotDate;   // opzione gia' scaduta: nessun periodo di sconto
        }
        return new FxOptionInput(code, valuationDate, spot,
                loadCurve(domestic, valuationDate), loadCurve(foreign, valuationDate),
                contract, volatility, spotDate, delivery);
    }

    /**
     * Black-76 / CRR sul prezzo secco a termine del titolo alla consegna della
     * serie (bond forward con la curva o il tasso repo). Il premio si sconta
     * con la stessa curva, o con il tasso repo reso continuo ACT/365.
     */
    private ForwardOptionInput getBondOptionInput(SpotOptionMasterData omd, SecurityMasterData bond, LocalDate valuationDate,
                                                  Double cleanPrice, String repoCurveCode, Double repoRate, double volatility) {
        OptionContract contract = contract(omd);
        LocalDate delivery = omd.getDeliveryLocalDate() != null ? omd.getDeliveryLocalDate() : contract.expiryDate();
        XRBForwardInput fwdInput = bondForwardCalculator.getSingleBondForwardInput(bond.getIsin(), valuationDate, delivery,
                cleanPrice, repoCurveCode, repoRate);
        double forward = bondForwardCalculator.bondFwdValuation(fwdInput).theoreticalPrice();

        boolean discounted = premiumStyle(omd).isDiscounted();
        DiscountCurve curve;
        if (!discounted) {
            curve = new FlatCurve(valuationDate, 0.0);
        } else if (repoCurveCode != null && !repoCurveCode.isBlank()) {
            curve = loadCurve(repoCurveCode, valuationDate);
        } else {
            // repo semplice ACT/360 fino alla scadenza dell'opzione -> continuo ACT/365
            long days = ChronoUnit.DAYS.between(valuationDate, contract.expiryDate());
            double t360 = YieldCurve.yearFraction(valuationDate, contract.expiryDate(), DaycountBasis.ACT_360);
            double continuous = days > 0 ? Math.log(1 + repoRate * t360) / (days / 365.0) : 0.0;
            curve = new FlatCurve(valuationDate, continuous);
        }
        return new ForwardOptionInput(omd.getCode(), valuationDate, forward, curve, discounted, contract, volatility);
    }

    // ------------------------------------------------------------------------
    // -- Valutazione
    // ------------------------------------------------------------------------

    /**
     * Modello per input e stile di esercizio: CRR per americane e bermudan,
     * altrimenti Black-76 (future e titoli) o Garman-Kohlhagen (cambi).
     *
     * @param input
     * @return
     */
    public static String model(OptionInput input) {
        if (input.contract().style() != ExerciseStyle.EUROPEAN) {
            return CRR;
        }
        return input instanceof FxOptionInput ? GARMAN_KOHLHAGEN : BLACK_76;
    }

    public OptionResult valuation(OptionInput input) {
        Objects.requireNonNull(input, "input");
        if (input.contract().style() != ExerciseStyle.EUROPEAN) {
            return crrPricer.calculate(input);
        }
        if (input instanceof FxOptionInput fx) {
            return garmanKohlhagenPricer.calculate(fx);
        }
        return black76Pricer.calculate((ForwardOptionInput) input);
    }

    /**
     * Volatilita' implicita di un premio di mercato unitario.
     *
     * @param input       la volatilita' dell'input e' il primo tentativo
     * @param optionPrice premio unitario di mercato
     * @return
     */
    public double impliedVolatility(OptionInput input, double optionPrice) {
        Objects.requireNonNull(input, "input");
        if (input.contract().style() != ExerciseStyle.EUROPEAN) {
            return ImpliedVolatility.solve(crrPricer, input, optionPrice);
        }
        if (input instanceof FxOptionInput fx) {
            return ImpliedVolatility.solve(garmanKohlhagenPricer, fx, optionPrice);
        }
        return ImpliedVolatility.solve(black76Pricer, (ForwardOptionInput) input, optionPrice);
    }

    /**
     * Entry point REST per tutte le opzioni: referencePrice e' il prezzo del
     * sottostante (se manca, la quotazione in anagrafica). Con optionPrice si
     * calcola la volatilita' implicita e si valuta con quella; altrimenti
     * serve volatility. domesticRCurve / domesticRate: sconto del premio
     * (titoli: curva o tasso repo); foreignRCurve: curva estera per i cambi.
     *
     * @param request
     * @return
     */
    public PricingResponse optionValuation(PricingRequest request) {
        Objects.requireNonNull(request, "request");
        LocalDate valuationDate = request.referenceDate != null
                ? request.referenceDate : marketDataService.getOfficialDate();
        boolean implied = request.optionPrice != null && request.optionPrice > 0;
        String volatilitySource = implied ? "implied" : "request";
        Double volatility = request.volatility;
        if (!implied && (volatility == null || volatility <= 0)) {
            // senza volatilita' ne' premio: superficie del sottostante
            Optional<VolatilityProvider.SurfaceData> surface = surface(request.isin, valuationDate);
            volatility = surfaceVolatility(request.isin, valuationDate).orElseThrow(() -> new IllegalArgumentException(
                    "Missing volatility (volatility) or market premium (optionPrice), and no "
                    + "volatility surface for the underlying at " + valuationDate));
            volatilitySource = surface.map(VolatilityProvider.SurfaceData::describe).orElse("surface");
        }

        // con il premio di mercato la volatilita' della request e' solo il primo tentativo
        double firstVol = volatility != null && volatility > 0 ? volatility : 0.20;
        OptionInput input = getOptionInput(request.isin, valuationDate, request.referencePrice,
                request.domesticRCurve, request.domesticRate, request.foreignRCurve, firstVol);
        // covered warrant: prezzo per warrant = premio per unita' di sottostante x ratio
        double ratio = findSeries(request.isin).getPriceRatio();
        if (implied) {
            input = input.withVolatility(impliedVolatility(input, request.optionPrice / ratio));
        }
        return response(input, volatilitySource, ratio);
    }

    /**
     * Prezzo teorico nell'unita' quotata della serie: premio per unita' di
     * sottostante per le opzioni di borsa, prezzo del warrant (x ratio) per i
     * covered warrant.
     *
     * @param omd serie
     * @param input input del pricer per la serie
     * @return
     */
    public double quotedPrice(OptionMasterData omd, OptionInput input) {
        return valuation(input).price() * omd.getPriceRatio();
    }

    /**
     * Risultati nell'unita' quotata: con ratio diverso da 1 (covered warrant)
     * premio, valore e greche sono per warrant.
     */
    private PricingResponse response(OptionInput input, String volatilitySource, double ratio) {
        OptionResult result = valuation(input);
        PricingResponse response = new PricingResponse();
        response.optionModel = model(input);
        response.optionPremium = result.price() * ratio;
        response.optionValue = result.positionValue() * ratio;
        response.volatility = input.volatility();
        response.volatilitySource = volatilitySource;
        response.underlyingForward = result.forward();
        response.discountFactor = result.discountFactor();
        response.timeToExpiry = result.timeToExpiry();
        response.delta = result.greeks().delta() * ratio;
        response.gamma = result.greeks().gamma() * ratio;
        response.vega = result.greeks().vega() * ratio;
        response.theta = result.greeks().theta() * ratio;
        response.rho = result.greeks().rho() * ratio;
        return response;
    }

    // ------------------------------------------------------------------------
    // -- Opzioni OTC (OFX, OCM): un contratto per anagrafica
    // ------------------------------------------------------------------------

    /**
     * Superficie del sottostante del contratto (coppia o future).
     * @param omd
     * @param valuationDate
     * @return 
     */
    public Optional<VolatilityProvider.SurfaceData> otcSurface(OtcOptionMasterData omd, LocalDate valuationDate) {
        MasterData underlying = (MasterData) Hibernate.unproxy(omd.getUnderlying());
        return volatilityProvider.surface(underlying.getIdMasterData(), valuationDate);
    }

    /**
     * Input del pricer per un'opzione OTC (salvata o solo simulata). Il
     * nozionale del contratto e' la quantita' del sottostante: positionValue
     * del risultato e' il valore del contratto (prezzo della transazione).
     *
     *  - OFX: Garman-Kohlhagen / CRR su spot e curve della coppia, consegna
     *    del contratto o scadenza + spot lag;
     *  - OCM: Black-76 / CRR sul prezzo del future, premio scontato con la
     *    curva indicata o la DISCOUNT di ac_dc_bcy_link per (OCM, valuta).
     *
     * @param omd
     * @param valuationDate
     * @param underlyingPrice spot della coppia o prezzo del future; null o <= 0 = quotazione mid
     * @param domesticCurveCode sconto del premio (valuta di regolamento); vuota = default
     * @param foreignCurveCode solo OFX: curva della valuta base; vuota = quella della coppia
     * @param volatility
     * @return 
     */
    public OptionInput getOtcOptionInput(OtcOptionMasterData omd, LocalDate valuationDate, Double underlyingPrice,
                                         String domesticCurveCode, String foreignCurveCode, double volatility) {
        Objects.requireNonNull(omd, "omd");
        Objects.requireNonNull(valuationDate, "valuationDate");
        OptionContract contract = new OptionContract(omd.getOptionType(), omd.getExerciseStyle(), omd.getStrike(),
                omd.getExpiryLocalDate(), omd.getNotional(), List.of());
        if (omd instanceof OtcFxOptionMasterData fx) {
            return fxOptionInput(omd.getCode(), contract, fx.getPair(), omd.getBusinessDays(), omd.getDeliveryLocalDate(),
                    valuationDate, underlyingPrice, domesticCurveCode, foreignCurveCode, volatility);
        }
        if (omd instanceof OtcCmdOptionMasterData cmd) {
            double forward = price(underlyingPrice, cmd.getFuture());
            String curveCode = domesticCurveCode != null && !domesticCurveCode.isBlank() ? domesticCurveCode
                    : acDcBcyLinkDAO.findCurveCode(omd.getAssetClass().getIdAssetClass(), omd.getCurrency().getIdCurrency(),
                            CurveRole.DISCOUNT).orElseThrow(() -> new IllegalArgumentException(
                            "Missing discount curve (domesticRCurve) and no DISCOUNT curve configured for "
                            + omd.getAssetClass().getCode() + "/" + omd.getCurrency().getIsoCode()));
            return new ForwardOptionInput(omd.getCode(), valuationDate, forward, loadCurve(curveCode, valuationDate),
                    true, contract, volatility);
        }
        throw new IllegalArgumentException("Unsupported OTC option " + omd.getCode());
    }

    /**
     * Valutazione di un'opzione OTC. Volatilita': indicata, implicita dal
     * premio di mercato unitario (optionPrice, per unita' di sottostante)
     * oppure dalla superficie del sottostante alla scadenza e allo strike.
     * optionValue = valore del contratto nella valuta di regolamento per chi
     * lo detiene.
     * @param omd
     * @param valuationDate
     * @param underlyingPrice
     * @param volatility
     * @param optionPrice
     * @param domesticCurveCode
     * @param foreignCurveCode
     * @return 
     */
    public PricingResponse otcOptionValuation(OtcOptionMasterData omd, LocalDate valuationDate, Double underlyingPrice,
                                              Double volatility, Double optionPrice,
                                              String domesticCurveCode, String foreignCurveCode) {
        Objects.requireNonNull(omd, "omd");
        LocalDate date = valuationDate != null ? valuationDate : marketDataService.getOfficialDate();
        boolean implied = optionPrice != null && optionPrice > 0;
        String volatilitySource = implied ? "implied" : "request";
        Double vol = volatility;
        if (!implied && (vol == null || vol <= 0)) {
            VolatilityProvider.SurfaceData surface = otcSurface(omd, date).orElseThrow(() -> new IllegalArgumentException(
                    "Missing volatility (volatility) or market premium (optionPrice), and no volatility surface for "
                    + omd.getUnderlying().getCode() + " at " + date));
            vol = surface.surface().volatility(omd.getExpiryLocalDate(), omd.getStrike());
            volatilitySource = surface.describe();
        }
        OptionInput input = getOtcOptionInput(omd, date, underlyingPrice, domesticCurveCode, foreignCurveCode,
                vol != null && vol > 0 ? vol : 0.20);
        if (implied) {
            input = input.withVolatility(impliedVolatility(input, optionPrice));
        }
        return response(input, volatilitySource, 1.);
    }

    // ------------------------------------------------------------------------
    // -- Helper privati
    // ------------------------------------------------------------------------

    /**
     * Serie dal codice o ISIN: prima tra le opzioni su future, poi tra quelle
     * su strumento secco.
     */
    private OptionMasterData findSeries(String code) {
        try {
            return futureOptionMasterDataDAO.getByCodeOrIsin(code);
        } catch (NoSuchElementException ex) {
            try {
                return spotOptionMasterDataDAO.getByCodeOrIsin(code);
            } catch (NoSuchElementException ex2) {
                throw new NoSuchElementException("Option not found: " + code);
            }
        }
    }

    /**
     * Prezzo indicato, oppure quotazione corrente (mid) del sottostante.
     */
    private double price(Double price, MasterData underlying) {
        double p = price != null && price > 0
                ? price
                : marketDataService.getSpotPrice(underlying.getIdMasterData(), RequestType.MIDDLE);
        if (p <= 0) {
            throw new IllegalArgumentException("Missing price of underlying " + underlying.getCode());
        }
        return p;
    }

    /**
     * Termini contrattuali della serie: notional = contract value x
     * multiplier del prodotto (es. 125.000 x 1 per l'opzione sul 6E; per le
     * opzioni su titolo, premio per 100 di nominale, multiplier 0,01).
     */
    private static OptionContract contract(OptionMasterData omd) {
        if (omd.getExerciseStyle() == ExerciseStyle.BERMUDAN) {
            throw new IllegalArgumentException("Bermudan option without exercise dates in master data: " + omd.getCode());
        }
        if (omd.getStrike() == null || omd.getExpiryLocalDate() == null) {
            throw new IllegalArgumentException("Missing strike or expiry for " + omd.getCode());
        }
        double contractValue = omd.getContractValue() != null && omd.getContractValue() > 0 ? omd.getContractValue() : 1.0;
        double multiplier = omd.getMultiplier() != null && omd.getMultiplier() > 0 ? omd.getMultiplier() : 1.0;
        double notional = contractValue * multiplier;
        return new OptionContract(omd.getOptionType(), omd.getExerciseStyle(), omd.getStrike(),
                omd.getExpiryLocalDate(), notional, List.of());
    }

    private static PremiumStyle premiumStyle(OptionMasterData omd) {
        if (omd.getPremiumStyle() == null) {
            throw new IllegalArgumentException("Missing premium style for " + omd.getCode());
        }
        return omd.getPremiumStyle();
    }

    /**
     * Recupera la curva e controlla che sia utilizzabile alla valuationDate:
     * YieldCurve rifiuta date precedenti alla propria data di riferimento.
     */
    private DiscountCurve loadCurve(String curveCode, LocalDate valuationDate) {
        if (curveCode == null || curveCode.isBlank()) {
            throw new IllegalArgumentException("Missing curve code");
        }
        DiscountCurve curve = marketDataService.getDiscountCurve(curveCode);
        if (curve == null) {
            throw new NoSuchElementException("Curve not found: " + curveCode);
        }
        if (curve.getValuationDate().isAfter(valuationDate)) {
            throw new IllegalArgumentException("Curve " + curveCode + " is dated "
                    + curve.getValuationDate() + ", after valuationDate " + valuationDate);
        }
        return curve;
    }

    /**
     * Curva piatta a tasso continuo ACT/365: per un tasso indicato a mano o per
     * il premio non scontato (tasso 0, DF = 1).
     */
    private record FlatCurve(LocalDate valuationDate, double rate) implements DiscountCurve {

        @Override
        public LocalDate getValuationDate() {
            return valuationDate;
        }

        @Override
        public java.util.Currency getCurrency() {
            return null;
        }

        @Override
        public int[] getBucketYears() {
            return new int[0];
        }

        @Override
        public double getDiscountFactor(LocalDate date) {
            return Math.exp(-rate * ChronoUnit.DAYS.between(valuationDate, date) / 365.0);
        }
    }
}
