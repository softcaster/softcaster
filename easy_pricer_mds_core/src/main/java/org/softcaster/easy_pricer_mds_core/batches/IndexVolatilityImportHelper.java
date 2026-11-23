package org.softcaster.easy_pricer_mds_core.batches;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.IndexMasterData;
import org.softcaster.core.data.VolatilityPoint;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.analytics.ImpliedVolatility;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;
import org.softcaster.engine.enums.RequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Superficie di volatilita' di un indice azionario dai prezzi di settlement
 * delle sue opzioni europee (Eurex OESX per SX5E). Chi chiama scarica la
 * catena dal provider; qui arrivano solo scadenza, strike, call/put e premio.
 *
 * Per ogni scadenza:
 *  1. forward dalla parita' put-call, C - P = DF (F - K), sugli strike con
 *     call e put: mediana dei tre strike con |C - P| piu' piccolo (i piu'
 *     vicini al denaro). Il forward contiene gia' i dividendi;
 *  2. DF dalla curva dell'indice (irc) alla scadenza;
 *  3. volatilita' implicita Black-76 sul forward delle sole opzioni fuori dal
 *     denaro (call sopra il forward, put sotto), con premio almeno minPremium
 *     e strike entro [minMoneyness, maxMoneyness] x forward.
 *
 * Con lo spot dell'indice in anagrafica il log riporta per ogni scadenza il
 * dividend yield implicito, q = -ln(F DF / S) / T, da confrontare con quello
 * dell'indice (IndexMasterData.dividendYield) usato per prezzare le serie.
 *
 * Salvataggio: una rilevazione completa per strike (VolatilitySurfaceDAO.replacePoints).
 */
@Service("indexVolatilityImportHelper")
public class IndexVolatilityImportHelper {

    private static final Logger log = LoggerFactory.getLogger(IndexVolatilityImportHelper.class);

    public static final String SOURCE = "EUREX";
    private static final int PARITY_STRIKES = 3;

    /**
     * Premio di settlement di una serie.
     *
     * @param settlementDate data del settlement
     * @param expiry scadenza della serie
     * @param strike prezzo di esercizio, in punti indice
     * @param type call o put
     * @param price premio di settlement, in punti indice
     */
    public record OptionSettlement(LocalDate settlementDate, LocalDate expiry, double strike, OptionType type, double price) {
    }

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private VolatilitySurfaceDAO volatilitySurfaceDAO;
    @Autowired
    private Black76Pricer black76Pricer;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * @param code superficie dell'indice (es. SX5E-VOL)
     * @param options premi di settlement della catena (una o piu' scadenze)
     * @param minPremium premio minimo: sotto, il prezzo non dice nulla sulla volatilita'
     * @param minMoneyness strike minimo rispetto al forward (es. 0.6)
     * @param maxMoneyness strike massimo rispetto al forward (es. 1.4)
     * @return esito dell'import
     */
    public VolatilityImportHelper.ImportResult importSettlements(String code, List<OptionSettlement> options,
            double minPremium, double minMoneyness, double maxMoneyness) {
        log.info("=== Volatility import {} from {} option settlements ===", code, options.size());
        VolatilityImportHelper.ImportResult result = transactionTemplate.execute(status ->
                importInTransaction(code, options, minPremium, minMoneyness, maxMoneyness));
        log.info("=== {} ===", result.describe());
        return result;
    }

    private VolatilityImportHelper.ImportResult importInTransaction(String code, List<OptionSettlement> options,
            double minPremium, double minMoneyness, double maxMoneyness) {
        VolatilitySurfaceEntity surface = volatilitySurfaceDAO.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Volatility surface not found: " + code));
        if (!(Hibernate.unproxy(surface.getUnderlying()) instanceof IndexMasterData index)) {
            throw new IllegalArgumentException("Volatility surface " + code + " is not on an equity index");
        }
        if (index.getIrc() == null || index.getIrc().isBlank()) {
            throw new IllegalArgumentException("Index " + index.getCode() + " has no discount curve (irc)");
        }
        if (options.isEmpty()) {
            throw new IllegalArgumentException("No option settlements for " + code);
        }

        // data di rilevazione: quella della maggior parte dei settlement
        LocalDate bizDate = options.stream()
                .collect(Collectors.groupingBy(OptionSettlement::settlementDate, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).orElseThrow().getKey();

        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves(List.of(index.getIrc()));
        DiscountCurve curve = marketDataService.getDiscountCurve(index.getIrc());
        if (curve == null) {
            throw new NoSuchElementException("Curve not found: " + index.getIrc());
        }
        if (curve.getValuationDate().isAfter(bizDate)) {
            throw new IllegalArgumentException("Curve " + index.getIrc() + " is dated " + curve.getValuationDate()
                    + ", after the settlement date " + bizDate);
        }
        double spot = marketDataService.getSpotPrice(index.getIdMasterData(), RequestType.MIDDLE);

        Map<String, Integer> skipped = new TreeMap<>();
        List<VolatilityPoint> points = new ArrayList<>();
        Map<LocalDate, List<OptionSettlement>> byExpiry = new TreeMap<>();
        for (OptionSettlement o : options) {
            if (!o.settlementDate().equals(bizDate)) {
                skipped.merge("other settlement date", 1, Integer::sum);
            } else if (!o.expiry().isAfter(bizDate)) {
                skipped.merge("expired", 1, Integer::sum);
            } else {
                byExpiry.computeIfAbsent(o.expiry(), e -> new ArrayList<>()).add(o);
            }
        }

        for (Map.Entry<LocalDate, List<OptionSettlement>> e : byExpiry.entrySet()) {
            LocalDate expiry = e.getKey();
            double df = curve.getDiscountFactor(expiry);
            Double forward = parityForward(e.getValue(), df);
            if (forward == null) {
                skipped.merge("no call/put pair for the forward", e.getValue().size(), Integer::sum);
                continue;
            }
            double t = ChronoUnit.DAYS.between(bizDate, expiry) / 365.0;
            if (spot > 0) {
                double q = -Math.log(forward * df / spot) / t;
                log.info("{} expiry {}: forward {} (spot {}), implied dividend yield {}", code, expiry,
                        round(forward), spot, String.format(java.util.Locale.ROOT, "%.4f", q));
            } else {
                log.info("{} expiry {}: forward {}", code, expiry, round(forward));
            }

            for (OptionSettlement o : e.getValue()) {
                boolean call = o.type() == OptionType.CALL;
                if (call ? o.strike() < forward : o.strike() > forward) {
                    skipped.merge("in the money", 1, Integer::sum);
                    continue;
                }
                if (o.price() < minPremium) {
                    skipped.merge("premium below minimum", 1, Integer::sum);
                    continue;
                }
                double moneyness = o.strike() / forward;
                if (moneyness < minMoneyness || moneyness > maxMoneyness) {
                    skipped.merge("strike outside the band", 1, Integer::sum);
                    continue;
                }
                double vol;
                try {
                    ForwardOptionInput input = new ForwardOptionInput(code + " " + expiry + " " + o.type() + " " + o.strike(),
                            bizDate, forward, curve, true,
                            new OptionContract(o.type(), ExerciseStyle.EUROPEAN, o.strike(), expiry, 1., List.of()), 0.2);
                    vol = ImpliedVolatility.solve(black76Pricer, input, o.price());
                } catch (RuntimeException ex) {
                    skipped.merge("no implied volatility", 1, Integer::sum);
                    continue;
                }
                if (vol <= 0 || vol >= 5) {
                    skipped.merge("volatility out of range", 1, Integer::sum);
                    continue;
                }
                points.add(new VolatilityPoint(surface.getIdVolatilitySurface(), bizDate, expiry, o.strike(), vol, SOURCE));
            }
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No usable option for " + code + ": " + skipped);
        }
        volatilitySurfaceDAO.replacePoints(surface.getIdVolatilitySurface(), bizDate, points);
        return new VolatilityImportHelper.ImportResult(code, bizDate, options.size(), points.size(), skipped);
    }

    /**
     * Forward della scadenza dalla parita' put-call, F = K + (C - P) / DF:
     * mediana sui PARITY_STRIKES strike con |C - P| piu' piccolo. Null se
     * nessuno strike ha sia la call sia la put.
     */
    static Double parityForward(List<OptionSettlement> chain, double df) {
        Map<Double, OptionSettlement> calls = new HashMap<>();
        Map<Double, OptionSettlement> puts = new HashMap<>();
        for (OptionSettlement o : chain) {
            (o.type() == OptionType.CALL ? calls : puts).put(o.strike(), o);
        }
        record Pair(double strike, double diff) {
        }
        List<Pair> pairs = calls.keySet().stream()
                .filter(puts::containsKey)
                .map(k -> new Pair(k, calls.get(k).price() - puts.get(k).price()))
                .sorted(Comparator.comparingDouble(p -> Math.abs(p.diff())))
                .limit(PARITY_STRIKES)
                .toList();
        if (pairs.isEmpty()) {
            return null;
        }
        List<Double> forwards = pairs.stream().map(p -> p.strike() + p.diff() / df).sorted().toList();
        return forwards.get(forwards.size() / 2);
    }

    private static double round(double value) {
        return Math.round(value * 100.) / 100.;
    }
}
