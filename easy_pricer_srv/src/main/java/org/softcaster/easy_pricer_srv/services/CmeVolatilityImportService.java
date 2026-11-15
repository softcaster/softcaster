package org.softcaster.easy_pricer_srv.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
import org.softcaster.easy_pricer_srv.dto.VolatilitySurfaceDto;
import org.softcaster.engine.analytics.Black76Pricer;
import org.softcaster.engine.analytics.ImpliedVolatility;
import org.softcaster.engine.curve.DiscountCurve;
import org.softcaster.engine.dto.ForwardOptionInput;
import org.softcaster.engine.dto.OptionContract;
import org.softcaster.engine.enums.ExerciseStyle;
import org.softcaster.engine.enums.OptionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Superficie di volatilita' di una coppia di cambi dai prezzi di settlement
 * CME delle opzioni sul future (es. EUU sul future 6E), file CSV di
 * settlement della giornata (formato dei file di settlement CME, con
 * intestazione: le colonne si cercano per nome).
 *
 * Per ogni opzione europea fuori dal denaro (call con strike sopra il
 * future, put sotto) con premio almeno minPremium: volatilita' implicita
 * Black-76 sul prezzo di settlement del suo future (stesso file), premio
 * scontato con la curva della valuta di quotazione. Le volatilita' di future e
 * spot coincidono di fatto (il future su cambi e' un forward), quindi i punti
 * vanno sulla superficie della coppia, con lo strike portato in termini di
 * forward della coppia alla consegna OTC della stessa scadenza:
 * K x F_coppia / F_future (stessa moneyness, FxForwardCalculator.optionForward). Le opzioni OTC e gli
 * accumulator leggono la superficie con il proprio strike; le opzioni su
 * future riportano il loro (OptionCalculator.surfaceVolatility).
 *
 * I punti sostituiscono quelli della stessa data di rilevazione.
 */
@Service
public class CmeVolatilityImportService {

    private static final Logger log = LoggerFactory.getLogger(CmeVolatilityImportService.class);

    public static final String SOURCE = "CME";

    // colonne del file di settlement CME
    private static final String BIZ_DATE = "BizDt";
    private static final String SYMBOL = "Sym";
    private static final String SECURITY_TYPE = "SecTyp";
    private static final String MONTH = "MMY";
    private static final String MATURITY = "MatDt";
    private static final String PUT_CALL = "PutCall";
    private static final String STRIKE = "StrkPx";
    private static final String SETTLE = "SettlePrice";
    private static final String UNDERLYING_MONTH = "UndlyMMY";

    private static final List<String> REQUIRED = List.of(BIZ_DATE, SYMBOL, SECURITY_TYPE, MONTH, MATURITY, PUT_CALL,
            STRIKE, SETTLE, UNDERLYING_MONTH);

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private VolatilitySurfaceDAO volatilitySurfaceDAO;
    @Autowired
    private VolatilityService volatilityService;
    @Autowired
    private Black76Pricer black76Pricer;
    @Autowired
    private FxForwardCalculator fxForwardCalculator;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Esito dell'import: superficie caricata e opzioni scartate per motivo.
     */
    public record ImportResult(VolatilitySurfaceDto surface, int options, int loaded, Map<String, Integer> skipped) {
    }

    private record Row(LocalDate bizDate, String symbol, String type, String month, LocalDate maturity, String putCall,
            double strike, double settle, String underlyingMonth) {
    }

    /**
     * @param code         superficie della coppia (es. EURUSD-VOL)
     * @param csv          file di settlement CME
     * @param optionSymbol simbolo delle opzioni (es. EUU)
     * @param futureSymbol simbolo del future sottostante (es. 6E)
     * @param strikeScale  moltiplicatore dello strike del file per avere lo
     *                     strike nell'unita' del prezzo del future (1 se
     *                     quotati allo stesso modo)
     * @param minPremium   premio minimo: sotto, il prezzo di settlement non
     *                     dice nulla sulla volatilita'
     */
    public ImportResult importSettlements(String code, String csv, String optionSymbol, String futureSymbol,
            double strikeScale, double minPremium) {
        VolatilitySurfaceEntity surface = transactionTemplate.execute(status -> volatilitySurfaceDAO.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Volatility surface not found: " + code)));
        if (!(Hibernate.unproxy(surface.getUnderlying()) instanceof ForexMasterData pair)) {
            throw new IllegalArgumentException("Volatility surface " + code + " is not on a currency pair");
        }
        List<Row> rows = parse(csv, optionSymbol, futureSymbol, strikeScale);

        LocalDate bizDate = null;
        Map<String, Double> futures = new HashMap<>();
        for (Row r : rows) {
            if (bizDate == null) {
                bizDate = r.bizDate();
            } else if (!bizDate.equals(r.bizDate())) {
                throw new IllegalArgumentException("The file mixes business dates " + bizDate + " and " + r.bizDate());
            }
            if (r.symbol().equals(futureSymbol) && isFuture(r.type())) {
                futures.put(r.month(), r.settle());
            }
        }
        if (bizDate == null) {
            throw new IllegalArgumentException("No rows for " + optionSymbol + " / " + futureSymbol);
        }

        DiscountCurve quoteCurve = marketDataService.getDiscountCurve(pair.getCcyIrc());
        Map<String, Integer> skipped = new HashMap<>();
        List<VolatilitySurfaceDto.Point> points = new ArrayList<>();
        int options = 0;
        for (Row r : rows) {
            if (!r.symbol().equals(optionSymbol) || isFuture(r.type())) {
                continue;
            }
            options++;
            Double future = futures.get(r.underlyingMonth());
            OptionType type = optionType(r.putCall());
            if (future == null) {
                skipped.merge("future not in file", 1, Integer::sum);
                continue;
            }
            if (!r.maturity().isAfter(bizDate)) {
                skipped.merge("expired", 1, Integer::sum);
                continue;
            }
            if (r.settle() < minPremium) {
                skipped.merge("premium below minimum", 1, Integer::sum);
                continue;
            }
            // solo fuori dal denaro: il prezzo e' tutto valore temporale
            if (type == OptionType.CALL ? r.strike() < future : r.strike() > future) {
                skipped.merge("in the money", 1, Integer::sum);
                continue;
            }
            double vol;
            try {
                ForwardOptionInput input = new ForwardOptionInput(optionSymbol + " " + r.month() + " " + r.strike(), bizDate,
                        future, quoteCurve, true,
                        new OptionContract(type, ExerciseStyle.EUROPEAN, r.strike(), r.maturity(), 1., List.of()), 0.1);
                vol = ImpliedVolatility.solve(black76Pricer, input, r.settle());
            } catch (RuntimeException e) {
                skipped.merge("no implied volatility", 1, Integer::sum);
                continue;
            }
            double pairForward = fxForwardCalculator.optionForward(pair, bizDate, r.maturity());
            double strike = r.strike() * pairForward / future;
            points.add(new VolatilitySurfaceDto.Point(r.maturity(), round(strike), vol, SOURCE));
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No usable option in the file: " + skipped);
        }
        log.info("CME volatility import {} as of {}: {} options, {} points, skipped {}", code, bizDate, options, points.size(), skipped);
        VolatilitySurfaceDto loaded = volatilityService.upload(code,
                new VolatilitySurfaceDto(code, null, pair.getCode(), bizDate, points));
        return new ImportResult(loaded, options, points.size(), skipped);
    }

    // ------------------------------------------------------------------------

    private static List<Row> parse(String csv, String optionSymbol, String futureSymbol, double strikeScale) {
        List<Row> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new StringReader(csv))) {
            String header = reader.readLine();
            if (header == null) {
                throw new IllegalArgumentException("Empty file");
            }
            Map<String, Integer> columns = new HashMap<>();
            String[] names = split(header);
            for (int i = 0; i < names.length; i++) {
                columns.put(names[i].trim(), i);
            }
            List<String> missing = REQUIRED.stream().filter(c -> !columns.containsKey(c)).toList();
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("Missing columns in the CME file: " + missing);
            }
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] f = split(line);
                String symbol = value(f, columns, SYMBOL);
                if (!symbol.equals(optionSymbol) && !symbol.equals(futureSymbol)) {
                    continue;
                }
                String settle = value(f, columns, SETTLE);
                if (settle.isEmpty()) {
                    continue;
                }
                try {
                    String strike = value(f, columns, STRIKE);
                    String maturity = value(f, columns, MATURITY);
                    rows.add(new Row(date(value(f, columns, BIZ_DATE)), symbol, value(f, columns, SECURITY_TYPE),
                            value(f, columns, MONTH), maturity.isEmpty() ? null : date(maturity),
                            value(f, columns, PUT_CALL), strike.isEmpty() ? 0. : Double.parseDouble(strike) * strikeScale,
                            Double.parseDouble(settle), value(f, columns, UNDERLYING_MONTH)));
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("CME file, line " + lineNumber + ": " + e.getMessage(), e);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return rows;
    }

    private static String[] split(String line) {
        return line.replace("\"", "").split(",", -1);
    }

    private static String value(String[] fields, Map<String, Integer> columns, String name) {
        int i = columns.get(name);
        return i < fields.length ? fields[i].trim() : "";
    }

    private static LocalDate date(String text) {
        return text.contains("-") ? LocalDate.parse(text) : LocalDate.parse(text, DateTimeFormatter.BASIC_ISO_DATE);
    }

    private static boolean isFuture(String securityType) {
        return "FUT".equalsIgnoreCase(securityType);
    }

    /**
     * Put/call del file: 1 / C = call, 0 / P = put.
     */
    private static OptionType optionType(String putCall) {
        String v = putCall.toUpperCase(Locale.ROOT);
        if (v.equals("1") || v.equals("C") || v.equals("CALL")) {
            return OptionType.CALL;
        }
        if (v.equals("0") || v.equals("P") || v.equals("PUT")) {
            return OptionType.PUT;
        }
        throw new IllegalArgumentException("Unknown put/call flag " + putCall);
    }

    private static double round(double strike) {
        return Math.round(strike * 1e6) / 1e6;
    }
}
