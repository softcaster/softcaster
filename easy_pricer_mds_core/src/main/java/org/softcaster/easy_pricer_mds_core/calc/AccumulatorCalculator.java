package org.softcaster.easy_pricer_mds_core.calc;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.hibernate.Hibernate;
import org.softcaster.core.data.AcDcBcyLink;
import org.softcaster.core.data.AcDcBcyLinkDAO;
import org.softcaster.core.data.AccumulatorFixing;
import org.softcaster.core.data.AccumulatorMasterData;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.MasterData;
import org.softcaster.core.data.SpreadCurveEntityDAO;
import org.softcaster.core.data.YieldCurveEntityDAO;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.dto.AccumulatorPricingResponse;
import org.softcaster.engine.analytics.AccumulatorPricer;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.AccumulatorFixingInput;
import org.softcaster.engine.dto.AccumulatorInput;
import org.softcaster.engine.dto.AccumulatorPendingSettlement;
import org.softcaster.engine.dto.AccumulatorResult;
import org.softcaster.engine.enums.CurveRole;
import org.softcaster.provider.enums.RequestType;

/**
 * Valutazione degli accumulator / decumulator: traduce contratto, calendario
 * dei fixing e dati di mercato nell'input di AccumulatorPricer.
 *
 * Forward del sottostante:
 *  - FXA: F(t) = S DF_estera(t) / DF_domestica(t), curve della coppia
 *    (ccyIrc domestica = valuta di regolamento, bcyIrc estera) o indicate;
 *  - CMA: prezzo del sottostante (una scadenza di future e' gia' un forward),
 *    sconto con la curva DISCOUNT di ac_dc_bcy_link per (CMA, valuta) o
 *    indicata.
 *
 * Calendario alla data di valutazione V:
 *  - fixing dopo V: futuri, simulati;
 *  - fixing a V non ancora rilevato (es. primo fixing = data dell'operazione):
 *    futuro con tempo 0, al forward corrente;
 *  - fixing fino a V rilevati e regolati dopo V: quantita' da regolare;
 *  - fixing fino a V rilevati e gia' regolati: fuori dalla valutazione;
 *  - fixing fino a V non rilevati: errore (vanno registrati prima).
 *
 * Volatilita': piatta se indicata dal chiamante, altrimenti dalla superficie
 * del sottostante (VolatilityProvider), allo strike e alla data di ogni
 * fixing: struttura a termine nel Monte Carlo, smile valutato allo strike
 * (dove pesano la leva e la parte opzionale). Creato come @Bean in
 * MdsCoreAutoConfiguration.
 *
 * Errori:
 *  - NoSuchElementException   -> contratto o curva non trovati (REST: 404)
 *  - IllegalArgumentException -> dati non validi (REST: 400)
 */
public class AccumulatorCalculator {

    public static final String MODEL = "Monte Carlo lognormale (GK/Black)";

    private final AcDcBcyLinkDAO acDcBcyLinkDAO;
    private final YieldCurveEntityDAO yieldCurveEntityDAO;
    private final SpreadCurveEntityDAO spreadCurveEntityDAO;
    private final MarketDataService marketDataService;
    private final VolatilityProvider volatilityProvider;
    private final AccumulatorPricer accumulatorPricer;

    public AccumulatorCalculator(AcDcBcyLinkDAO acDcBcyLinkDAO,
                                 YieldCurveEntityDAO yieldCurveEntityDAO,
                                 SpreadCurveEntityDAO spreadCurveEntityDAO,
                                 MarketDataService marketDataService,
                                 VolatilityProvider volatilityProvider,
                                 AccumulatorPricer accumulatorPricer) {
        this.acDcBcyLinkDAO = Objects.requireNonNull(acDcBcyLinkDAO);
        this.yieldCurveEntityDAO = Objects.requireNonNull(yieldCurveEntityDAO);
        this.spreadCurveEntityDAO = Objects.requireNonNull(spreadCurveEntityDAO);
        this.marketDataService = Objects.requireNonNull(marketDataService);
        this.volatilityProvider = Objects.requireNonNull(volatilityProvider);
        this.accumulatorPricer = Objects.requireNonNull(accumulatorPricer);
    }

    /**
     * Volatilita' di un fixing: piatta se indicata, altrimenti dalla
     * superficie del sottostante.
     */
    private record Volatility(Double flat, VolatilityProvider.SurfaceData surface) {

        double at(LocalDate fixingDate, double strike) {
            return flat != null ? flat : surface.surface().volatility(fixingDate, strike);
        }

        String source() {
            return flat != null ? "flat" : surface.describe();
        }
    }

    private Volatility volatility(Double flat, MasterData underlying, LocalDate valuationDate) {
        if (flat != null && flat > 0) {
            return new Volatility(flat, null);
        }
        return volatilityProvider.surface(underlying.getIdMasterData(), valuationDate)
                .map(s -> new Volatility(null, s))
                .orElseThrow(() -> new IllegalArgumentException("Volatilita' mancante: indicare volatility o caricare "
                        + "la superficie di volatilita' di " + underlying.getCode() + " al " + valuationDate));
    }

    // ------------------------------------------------------------------------
    // -- Costruzione input
    // ------------------------------------------------------------------------

    /**
     * Input del pricer per un contratto (salvato o solo simulato).
     *
     * @param spot             prezzo del sottostante; null o <= 0 = quotazione mid
     * @param volatility       volatilita' piatta annua in decimale; null o <= 0 =
     *                         superficie del sottostante
     * @param domesticCurveCode curva di sconto (valuta di regolamento); vuota = default
     * @param foreignCurveCode solo FXA: curva della valuta base; vuota = default
     * @param paths            sentieri Monte Carlo; null = default
     */
    public AccumulatorInput getAccumulatorInput(AccumulatorMasterData amd, LocalDate valuationDate, Double spot,
                                                Double volatility, String domesticCurveCode, String foreignCurveCode,
                                                Integer paths) {
        return input(amd, valuationDate, spot, volatility, domesticCurveCode, foreignCurveCode, paths).input();
    }

    private record Prepared(AccumulatorInput input, String volatilitySource) {
    }

    private Prepared input(AccumulatorMasterData amd, LocalDate valuationDate, Double spot,
                           Double volatility, String domesticCurveCode, String foreignCurveCode, Integer paths) {
        Objects.requireNonNull(amd, "amd");
        Objects.requireNonNull(valuationDate, "valuationDate");
        MasterData underlying = (MasterData) Hibernate.unproxy(amd.getUnderlying());
        double s = price(spot, underlying);
        Volatility vol = null;   // letta solo se ci sono fixing futuri

        Forwards forwards;
        if (underlying instanceof ForexMasterData pair) {
            DiscountCurve domestic = loadCurve(blank(domesticCurveCode) ? pair.getCcyIrc() : domesticCurveCode, valuationDate);
            DiscountCurve foreign = loadCurve(blank(foreignCurveCode) ? pair.getBcyIrc() : foreignCurveCode, valuationDate);
            forwards = new Forwards(valuationDate, s, domestic, foreign);
        } else {
            String curveCode = blank(domesticCurveCode) ? discountCurveCode(amd) : domesticCurveCode;
            forwards = new Forwards(valuationDate, s, loadCurve(curveCode, valuationDate), null);
        }

        List<AccumulatorFixing> schedule = amd.getFixings();
        List<AccumulatorFixingInput> future = new ArrayList<>();
        List<AccumulatorPendingSettlement> pending = new ArrayList<>();
        List<LocalDate> missing = new ArrayList<>();
        for (int i = 0; i < schedule.size(); i++) {
            AccumulatorFixing f = schedule.get(i);
            LocalDate fixingDate = f.getFixingLocalDate();
            LocalDate settlementDate = f.getSettlementLocalDate();
            // fixing del giorno non ancora rilevato: futuro con tempo 0 (al forward corrente)
            boolean pendingToday = fixingDate.equals(valuationDate) && !f.isFixed();
            if (fixingDate.isAfter(valuationDate) || pendingToday) {
                if (amd.isKnockedOut()) {
                    continue;
                }
                boolean periodEnd = i == schedule.size() - 1
                        || !schedule.get(i + 1).getSettlementLocalDate().equals(settlementDate);
                if (vol == null) {
                    vol = volatility(volatility, underlying, valuationDate);
                }
                future.add(new AccumulatorFixingInput(i,
                        ChronoUnit.DAYS.between(valuationDate, fixingDate) / 365.0,
                        forwards.forward(fixingDate), forwards.forward(settlementDate),
                        forwards.discountFactor(settlementDate), periodEnd,
                        vol.at(fixingDate, amd.getStrike())));
            } else if (!f.isFixed()) {
                missing.add(fixingDate);
            } else if (settlementDate.isAfter(valuationDate) && f.getAccumulatedQuantity() != null
                    && f.getAccumulatedQuantity() > 0) {
                pending.add(new AccumulatorPendingSettlement(f.getAccumulatedQuantity(),
                        forwards.forward(settlementDate), forwards.discountFactor(settlementDate)));
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Accumulator " + amd.getCode() + ": fixing non rilevati al "
                    + valuationDate + " " + missing);
        }

        AccumulatorInput input = new AccumulatorInput(amd.getCode(), valuationDate, amd.getAccumulatorType(), amd.getStrike(),
                amd.getKoBarrier(), amd.getKoObservation(), amd.getKiBarrier(), amd.getKiEffect(),
                amd.getLeverage(), amd.getDailyQuantity(), amd.getGuaranteedFixings(), amd.getGuaranteeType(),
                Boolean.TRUE.equals(amd.getKoAccrual()), amd.isKnockedIn(), s, future, pending,
                paths != null && paths > 1 ? paths : AccumulatorInput.DEFAULT_PATHS, AccumulatorInput.DEFAULT_SEED);
        return new Prepared(input, vol == null ? "" : vol.source());
    }

    // ------------------------------------------------------------------------
    // -- Valutazione
    // ------------------------------------------------------------------------

    public AccumulatorResult valuation(AccumulatorInput input) {
        return accumulatorPricer.calculate(Objects.requireNonNull(input, "input"));
    }

    /**
     * Valuta il contratto e prepara la risposta REST.
     */
    public AccumulatorPricingResponse accumulatorValuation(AccumulatorMasterData amd, LocalDate valuationDate,
                                                           Double spot, Double volatility, String domesticCurveCode,
                                                           String foreignCurveCode, Integer paths) {
        LocalDate date = valuationDate != null ? valuationDate : marketDataService.getOfficialDate();
        Prepared prepared = input(amd, date, spot, volatility, domesticCurveCode, foreignCurveCode, paths);
        AccumulatorInput input = prepared.input();
        AccumulatorResult result = valuation(input);

        AccumulatorPricingResponse response = new AccumulatorPricingResponse();
        response.code = amd.getCode();
        response.model = MODEL;
        response.valuationDate = date;
        response.spot = input.spot();
        response.volatility = input.averageVolatility();
        response.volatilitySource = prepared.volatilitySource();
        response.value = result.value();
        response.standardError = result.standardError();
        response.pendingValue = result.pendingValue();
        response.maxQuantity = amd.getMaxQuantity();
        response.unitValue = response.maxQuantity > 0 ? result.value() / response.maxQuantity : 0.;
        response.expectedQuantity = result.expectedQuantity();
        response.knockOutProbability = result.knockOutProbability();
        response.totalFixings = amd.getFixings().size();
        response.futureFixings = input.fixings().size();
        response.delta = result.delta();
        response.gamma = result.gamma();
        response.vega = result.vega();
        response.paths = result.paths();
        return response;
    }

    // ------------------------------------------------------------------------
    // -- Helper privati
    // ------------------------------------------------------------------------

    /**
     * Forward e sconto dalla data di valutazione: per FXA con le due curve,
     * per CMA forward costante (il prezzo del sottostante).
     */
    private record Forwards(LocalDate valuationDate, double spot, DiscountCurve domestic, DiscountCurve foreign) {

        double discountFactor(LocalDate date) {
            return relative(domestic, date);
        }

        double forward(LocalDate date) {
            return foreign == null ? spot : spot * relative(foreign, date) / relative(domestic, date);
        }

        private double relative(DiscountCurve curve, LocalDate date) {
            return curve.getDiscountFactor(date) / curve.getDiscountFactor(valuationDate);
        }
    }

    /**
     * Curva DISCOUNT configurata per (asset class, valuta di regolamento).
     */
    private String discountCurveCode(AccumulatorMasterData amd) {
        AcDcBcyLink link = acDcBcyLinkDAO.find(amd.getAssetClass().getIdAssetClass(),
                amd.getCurrency().getIdCurrency(), CurveRole.DISCOUNT)
                .orElseThrow(() -> new IllegalArgumentException("Curva di sconto mancante (domesticRCurve) e nessuna curva "
                        + "DISCOUNT configurata per " + amd.getAssetClass().getCode() + "/" + amd.getCurrency().getIsoCode()));
        return link.isSpreadCurve()
                ? spreadCurveEntityDAO.findByIdWithBase(link.getSpreadCurve()).getCode()
                : yieldCurveEntityDAO.findByIdYieldCurve(link.getCurveId()).getCode();
    }

    private double price(Double price, MasterData underlying) {
        double p = price != null && price > 0
                ? price
                : marketDataService.getSpotPrice(underlying.getIdMasterData(), RequestType.MIDDLE);
        if (p <= 0) {
            throw new IllegalArgumentException("Prezzo del sottostante " + underlying.getCode() + " mancante");
        }
        return p;
    }

    private DiscountCurve loadCurve(String curveCode, LocalDate valuationDate) {
        if (blank(curveCode)) {
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

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
