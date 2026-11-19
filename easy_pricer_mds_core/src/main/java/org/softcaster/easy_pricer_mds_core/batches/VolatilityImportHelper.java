package org.softcaster.easy_pricer_mds_core.batches;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.softcaster.core.data.ForexMasterData;
import org.softcaster.core.data.VolatilityPoint;
import org.softcaster.core.data.VolatilitySurfaceDAO;
import org.softcaster.core.data.VolatilitySurfaceEntity;
import org.softcaster.easy_pricer_mds_core.MarketDataService;
import org.softcaster.easy_pricer_mds_core.calc.FxForwardCalculator;
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
 * Batch delle superfici di volatilita' delle coppie di cambi dai settlement
 * CME delle opzioni sul future (es. EUU sul future 6E). File CSV (o .txt)
 * della giornata; la tabella del Summary View di QuikStrike incollata cosi'
 * com'e' (righe per tenor, colonne per delta: importSummaryGrid) si
 * riconosce dalle righe che iniziano con un tenor. Gli altri formati hanno
 * un'intestazione (colonne cercate per nome) e si riconoscono da quella:
 *
 * 0. griglia per delta del tool FX di CME (FXTOOL_..._London_3PM): snap_time,
 *    tenor, expiry_date, dte, forward, option_type, midNNd; ogni delta diventa
 *    uno strike sul forward della riga (importDeltaGrid).
 * 1. volatilita' di settlement (es. dal tool QuikStrike, anche compilato a
 *    mano): BizDt, Sym, MatDt, FuturePrice, StrkPx, Vol (percentuale). La
 *    volatilita' si carica com'e'.
 * 2. premi di settlement (file di settlement CME): BizDt, Sym, SecTyp, MMY,
 *    MatDt, PutCall, StrkPx, SettlePrice, UndlyMMY; la volatilita' si ricava
 *    dai premi (sotto).
 *
 * Separatore virgola (decimali col punto) o punto e virgola (decimali anche
 * con la virgola).
 *
 * Formato 2: per ogni opzione europea fuori dal denaro (call con strike sopra il future,
 * put sotto) con premio almeno minPremium: volatilita' implicita Black-76 sul
 * prezzo di settlement del suo future (stesso file), premio scontato con la
 * curva della valuta di quotazione. Le volatilita' di future e spot
 * coincidono di fatto (il future su cambi e' un forward): i punti vanno sulla
 * superficie della coppia, con lo strike portato in termini di forward della
 * coppia alla consegna OTC della stessa scadenza, K x F_coppia / F_future
 * (stessa moneyness, FxForwardCalculator.optionForward). Le opzioni OTC e gli
 * accumulator leggono la superficie con il proprio strike; le opzioni su
 * future riportano il loro (OptionCalculator.surfaceVolatility).
 *
 * Salvataggio (VolatilitySurfaceDAO): le griglie per delta (FXTOOL e
 * Summary View) aggiornano la superficie per tenor, con tenor e delta su ogni
 * punto (i tenor non presenti nel file restano); i formati per strike
 * caricano una rilevazione completa. Una data piu' recente della superficie
 * corrente la sposta nello storico; i tenor in giorni (1D) non si caricano.
 */
@Service("volatilityImportHelper")
public class VolatilityImportHelper {

    private static final Logger log = LoggerFactory.getLogger(VolatilityImportHelper.class);

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
    // formato con le volatilita' di settlement (QuikStrike)
    private static final String FUTURE_PRICE = "FuturePrice";
    private static final String VOL = "Vol";
    // formato a delta (tool FX di CME, es. FXTOOL_EUU_London_3PM_AAAAMMGG.csv)
    private static final String SNAP_TIME = "snap_time";
    private static final String TENOR = "tenor";
    private static final String EXPIRY_DATE = "expiry_date";
    private static final String DTE = "dte";
    private static final String FORWARD = "forward";
    private static final String OPTION_TYPE = "option_type";
    // volatilita' mid per delta: mid10d, mid25d, mid50d...
    private static final java.util.regex.Pattern MID_DELTA = java.util.regex.Pattern.compile("mid(\\d+)d");
    private static final DateTimeFormatter US_DATE = DateTimeFormatter.ofPattern("M/d/yyyy");
    // tenor delle griglie per delta: 1W, 2W, 1M, 1Y...
    private static final java.util.regex.Pattern TENOR_CODE = java.util.regex.Pattern.compile("\\d{1,2}[DWMY]");

    private static final List<String> REQUIRED = List.of(BIZ_DATE, SYMBOL, SECURITY_TYPE, MONTH, MATURITY, PUT_CALL,
            STRIKE, SETTLE, UNDERLYING_MONTH);

    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private VolatilitySurfaceDAO volatilitySurfaceDAO;
    @Autowired
    private Black76Pricer black76Pricer;
    @Autowired
    private FxForwardCalculator fxForwardCalculator;
    @Autowired
    @Qualifier("marketDataService")
    private MarketDataService marketDataService;

    /**
     * Esito di un import: data di rilevazione, opzioni lette, punti caricati e
     * opzioni scartate per motivo.
     */
    public record ImportResult(String surface, LocalDate referenceDate, int options, int loaded, Map<String, Integer> skipped) {

        public String describe() {
            return surface + " as of " + referenceDate + ": " + loaded + " points from " + options + " options"
                    + (skipped.isEmpty() ? "" : ", skipped " + skipped);
        }
    }

    private record Row(LocalDate bizDate, String symbol, String type, String month, LocalDate maturity, String putCall,
            double strike, double settle, String underlyingMonth) {
    }

    /**
     * File della superficie nella cartella: si chiama come il codice della
     * superficie, con o senza data, .csv o .txt (EURUSD-VOL.csv,
     * EURUSD-VOL_20261119.csv; maiuscole e minuscole indifferenti). Se ce ne
     * sono piu' d'uno vale il piu' recente; vuoto se non ce ne sono.
     *
     * @param directory cartella CME_VOL_DIRECTORY
     * @param surfaceCode codice di volatility_surface
     * @return
     */
    public static Optional<Path> surfaceFile(Path directory, String surfaceCode) {
        java.util.regex.Pattern name = java.util.regex.Pattern.compile(
                java.util.regex.Pattern.quote(surfaceCode) + "(_\\d{8})?\\.(csv|txt)",
                java.util.regex.Pattern.CASE_INSENSITIVE);
        try (var files = Files.list(directory)) {
            return files.filter(p -> Files.isRegularFile(p) && name.matcher(p.getFileName().toString()).matches())
                    .max((a, b) -> Long.compare(a.toFile().lastModified(), b.toFile().lastModified()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + directory + ": " + e.getMessage(), e);
        }
    }

    /**
     * @param code         superficie della coppia (es. EURUSD-VOL)
     * @param file         file di settlement CME
     * @param optionSymbol simbolo delle opzioni (es. EUU); solo per i formati
     *                     per strike, null per le griglie per delta
     * @param futureSymbol simbolo del future sottostante (es. 6E); come optionSymbol
     * @param strikeScale  moltiplicatore dello strike del file per avere lo
     *                     strike nell'unita' del prezzo del future (1 se
     *                     quotati allo stesso modo)
     * @param minPremium   premio minimo: sotto, il prezzo di settlement non
     *                     dice nulla sulla volatilita'
     * @return 
     */
    public ImportResult importCmeSettlements(String code, Path file, String optionSymbol, String futureSymbol,
            double strikeScale, double minPremium) {
        log.info("=== Volatility import {} from {} ===", code, file);
        // una transazione: anagrafica della coppia (valute, curve) letta e punti sostituiti insieme;
        // il batch gira fuori da una richiesta web, senza sessione aperta
        ImportResult result = transactionTemplate.execute(status ->
                importInTransaction(code, file, optionSymbol, futureSymbol, strikeScale, minPremium));
        log.info("=== {} ===", result.describe());
        return result;
    }

    private ImportResult importInTransaction(String code, Path file, String optionSymbol, String futureSymbol,
            double strikeScale, double minPremium) {
        VolatilitySurfaceEntity surface = volatilitySurfaceDAO.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Volatility surface not found: " + code));
        if (!(Hibernate.unproxy(surface.getUnderlying()) instanceof ForexMasterData pair)) {
            throw new IllegalArgumentException("Volatility surface " + code + " is not on a currency pair");
        }
        List<String> lines = readLines(file);
        // griglia per tenor e delta copiata dal Summary View (senza intestazione CSV)
        if (SummaryGrid.matches(lines)) {
            return importSummaryGrid(surface, pair, lines, file);
        }
        Csv csv = Csv.read(file, lines);
        // griglia per delta del tool FX di CME (snapshot per tenor, con il forward)
        if (csv.has(SNAP_TIME, TENOR, EXPIRY_DATE, FORWARD, OPTION_TYPE)) {
            return importDeltaGrid(surface, csv, file);
        }
        // formati per strike: servono i simboli CME di opzione e future
        if (optionSymbol == null || optionSymbol.isBlank() || futureSymbol == null || futureSymbol.isBlank()) {
            throw new IllegalArgumentException("File " + file.getFileName() + " has CME settlements by strike: "
                    + "set the option and future symbols of " + code + " in CME_VOL_SURFACES (conf/params.xml)");
        }
        // volatilita' di settlement gia' calcolate da CME: si caricano direttamente
        if (csv.has(VOL, FUTURE_PRICE)) {
            return importVolatilities(surface, pair, csv, file, optionSymbol, strikeScale);
        }
        List<Row> rows = parse(csv, file, optionSymbol, futureSymbol, strikeScale);

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
            throw new IllegalArgumentException("No rows for " + optionSymbol + " / " + futureSymbol + " in " + file);
        }

        // spot e curve della coppia per il forward alla consegna e lo sconto del premio
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves(List.of(pair.getCcyIrc(), pair.getBcyIrc()));
        DiscountCurve quoteCurve = marketDataService.getDiscountCurve(pair.getCcyIrc());

        Map<String, Integer> skipped = new TreeMap<>();
        List<VolatilityPoint> points = new ArrayList<>();
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
            if (r.maturity() == null || !r.maturity().isAfter(bizDate)) {
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
            if (vol <= 0 || vol >= 5) {
                skipped.merge("volatility out of range", 1, Integer::sum);
                continue;
            }
            double pairForward = fxForwardCalculator.optionForward(pair, bizDate, r.maturity());
            double strike = round(r.strike() * pairForward / future);
            points.add(new VolatilityPoint(surface.getIdVolatilitySurface(), bizDate, r.maturity(), strike, vol, SOURCE));
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No usable option in " + file + ": " + skipped);
        }
        volatilitySurfaceDAO.replacePoints(surface.getIdVolatilitySurface(), bizDate, points);
        return new ImportResult(code, bizDate, options, points.size(), skipped);
    }

    // ------------------------------------------------------------------------
    // -- Formato con le volatilita' di settlement (QuikStrike, compilato a mano)
    // ------------------------------------------------------------------------

    /**
     * Righe BizDt, Sym, MatDt, FuturePrice, StrkPx, Vol (in percentuale): la
     * volatilita' di settlement CME dello strike, uguale per call e put. Si
     * carica com'e', con lo strike portato nel forward della coppia.
     */
    private ImportResult importVolatilities(VolatilitySurfaceEntity surface, ForexMasterData pair, Csv csv, Path file,
            String optionSymbol, double strikeScale) {
        csv.require(file, List.of(BIZ_DATE, SYMBOL, MATURITY, FUTURE_PRICE, STRIKE, VOL));
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves(List.of(pair.getCcyIrc(), pair.getBcyIrc()));

        LocalDate bizDate = null;
        Map<String, Integer> skipped = new TreeMap<>();
        // un punto per scadenza e strike: righe ripetute (es. call e put) contano una volta
        Map<String, VolatilityPoint> points = new java.util.LinkedHashMap<>();
        Map<LocalDate, Double> pairForwards = new HashMap<>();
        int rows = 0;
        for (int n = 0; n < csv.rows().size(); n++) {
            String[] f = csv.rows().get(n);
            if (!csv.value(f, SYMBOL).equals(optionSymbol) || csv.value(f, VOL).isEmpty()) {
                continue;
            }
            rows++;
            try {
                LocalDate rowDate = date(csv.value(f, BIZ_DATE));
                if (bizDate == null) {
                    bizDate = rowDate;
                } else if (!bizDate.equals(rowDate)) {
                    throw new IllegalArgumentException("the file mixes business dates " + bizDate + " and " + rowDate);
                }
                LocalDate maturity = date(csv.value(f, MATURITY));
                if (!maturity.isAfter(bizDate)) {
                    skipped.merge("expired", 1, Integer::sum);
                    continue;
                }
                double future = csv.number(csv.value(f, FUTURE_PRICE));
                double strike = csv.number(csv.value(f, STRIKE)) * strikeScale;
                double vol = csv.number(csv.value(f, VOL)) / 100.;
                if (future <= 0 || strike <= 0) {
                    skipped.merge("missing future price or strike", 1, Integer::sum);
                    continue;
                }
                if (vol <= 0 || vol >= 5) {
                    skipped.merge("volatility out of range", 1, Integer::sum);
                    continue;
                }
                LocalDate businessDate = bizDate;
                double pairForward = pairForwards.computeIfAbsent(maturity,
                        m -> fxForwardCalculator.optionForward(pair, businessDate, m));
                double pairStrike = round(strike * pairForward / future);
                points.putIfAbsent(maturity + "@" + pairStrike, new VolatilityPoint(surface.getIdVolatilitySurface(),
                        bizDate, maturity, pairStrike, vol, SOURCE));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("File " + file + ", line " + csv.lineNumber(n) + ": " + e.getMessage(), e);
            }
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No usable volatility for " + optionSymbol + " in " + file + ": " + skipped);
        }
        volatilitySurfaceDAO.replacePoints(surface.getIdVolatilitySurface(), bizDate, new ArrayList<>(points.values()));
        return new ImportResult(surface.getCode(), bizDate, rows, points.size(), skipped);
    }

    // ------------------------------------------------------------------------
    // -- Formato a delta (tool FX di CME)
    // ------------------------------------------------------------------------

    /**
     * Snapshot per tenor della superficie per delta (es.
     * FXTOOL_EUU_London_3PM_AAAAMMGG.csv): per ogni riga tenor, scadenza,
     * giorni (dte), forward della coppia alla scadenza, tipo dei delta
     * ("EUR Call (USD Put)": delta delle call; "... Put ...": delta delle put)
     * e volatilita' mid per delta (mid10d, mid25d, mid50d, mid75d, mid90d) in
     * decimale. Ogni delta diventa uno strike con Black-76 sul forward della
     * riga (Black76Pricer.strikeForCallDelta, T = dte / 365): il forward e'
     * gia' quello della coppia, nessuna conversione dal future. Data di
     * rilevazione = data di snap_time.
     */
    private ImportResult importDeltaGrid(VolatilitySurfaceEntity surface, Csv csv, Path file) {
        // colonne mid per delta, nell'ordine del file
        Map<String, Double> deltaColumns = new java.util.LinkedHashMap<>();
        csv.columns().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> {
                    java.util.regex.Matcher m = MID_DELTA.matcher(e.getKey());
                    if (m.matches()) {
                        deltaColumns.put(e.getKey(), Integer.parseInt(m.group(1)) / 100.);
                    }
                });
        if (deltaColumns.isEmpty()) {
            throw new IllegalArgumentException("No mid volatility by delta (midNNd) in " + file);
        }

        LocalDate bizDate = null;
        Map<String, Integer> skipped = new TreeMap<>();
        // un punto per scadenza e strike: tenor con la stessa scadenza contano una volta
        Map<String, VolatilityPoint> points = new java.util.LinkedHashMap<>();
        int quotes = 0;
        for (int n = 0; n < csv.rows().size(); n++) {
            String[] f = csv.rows().get(n);
            try {
                LocalDate rowDate = snapDate(csv.value(f, SNAP_TIME));
                if (bizDate == null) {
                    bizDate = rowDate;
                } else if (!bizDate.equals(rowDate)) {
                    throw new IllegalArgumentException("the file mixes snapshot dates " + bizDate + " and " + rowDate);
                }
                LocalDate expiry = LocalDate.parse(csv.value(f, EXPIRY_DATE), US_DATE);
                if (!expiry.isAfter(bizDate)) {
                    skipped.merge("expired", deltaColumns.size(), Integer::sum);
                    continue;
                }
                String tenor = csv.value(f, TENOR).toUpperCase(Locale.ROOT);
                if (!TENOR_CODE.matcher(tenor).matches()) {
                    throw new IllegalArgumentException("invalid tenor '" + tenor + "'");
                }
                // come per il Summary View: i tenor in giorni non si caricano
                if (tenor.endsWith("D")) {
                    skipped.merge("day tenor", deltaColumns.size(), Integer::sum);
                    continue;
                }
                double forward = csv.number(csv.value(f, FORWARD));
                String dte = csv.value(f, DTE);
                double t = (dte.isEmpty() ? java.time.temporal.ChronoUnit.DAYS.between(bizDate, expiry)
                        : Double.parseDouble(dte)) / 365.;
                boolean callDeltas = csv.value(f, OPTION_TYPE).toLowerCase(Locale.ROOT).contains("call (");
                for (Map.Entry<String, Double> c : deltaColumns.entrySet()) {
                    quotes++;
                    String text = csv.value(f, c.getKey());
                    double vol = text.isEmpty() ? Double.NaN : csv.number(text);
                    if (Double.isNaN(vol) || vol <= 0 || vol >= 5) {
                        skipped.merge("missing volatility", 1, Integer::sum);
                        continue;
                    }
                    double callDelta = roundDelta(callDeltas ? c.getValue() : 1. - c.getValue());
                    double strike = round(Black76Pricer.strikeForCallDelta(forward, vol, t, callDelta));
                    points.putIfAbsent(expiry + "@" + strike, new VolatilityPoint(surface.getIdVolatilitySurface(),
                            bizDate, expiry, strike, vol, SOURCE, tenor, callDelta));
                }
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("File " + file + ", line " + csv.lineNumber(n) + ": " + e.getMessage(), e);
            }
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No usable volatility in " + file + ": " + skipped);
        }
        volatilitySurfaceDAO.updateTenorPoints(surface.getIdVolatilitySurface(), bizDate, new ArrayList<>(points.values()));
        return new ImportResult(surface.getCode(), bizDate, quotes, points.size(), skipped);
    }

    /**
     * Data di snap_time, MM/dd/yyyy HH:mm:ss (es. 07/01/2025 14:00:03).
     */
    private static LocalDate snapDate(String text) {
        String day = text.trim().split(" ")[0];
        return LocalDate.parse(day, US_DATE);
    }

    // ------------------------------------------------------------------------
    // -- Griglia del Summary View (FX Options Vol Converter, QuikStrike)
    // ------------------------------------------------------------------------

    /**
     * Tabella del Summary View copiata dal browser (o da Excel): una riga per
     * tenor (1W, 2W, 1M, ... 1Y) con le volatilita' in percentuale per delta,
     * nell'ordine delle colonne: put 5D...40D, 50D, call 40D...5D; RiskRev e
     * Butterflies in coda si ignorano. Se c'e' la riga dei delta (5D 10D ...
     * 50D ... 5D, anche col simbolo delta) l'ordine si prende da li'.
     *
     * Il Summary View non da' scadenza ne' forward: scadenza = data di
     * rilevazione + tenor, al giorno lavorativo successivo sul calendario
     * della coppia (come le expiry_date del file FXTOOL di CME); forward
     * della coppia alla scadenza da FxForwardCalculator.optionForward (spot e
     * curve con cui poi si prezza). Delta forward come nel formato FXTOOL: la
     * put con delta d e' la call con delta 1 - d, lo strike da
     * Black76Pricer.strikeForCallDelta con T = giorni / 365.
     *
     * Data di rilevazione: dal nome del file (aaaammgg, es. EURUSD_20261117.txt),
     * altrimenti la data ufficiale. I tenor in giorni (1D) si scartano: molto
     * rumorosi e senza interesse per i contratti OTC.
     */
    private ImportResult importSummaryGrid(VolatilitySurfaceEntity surface, ForexMasterData pair, List<String> lines,
            Path file) {
        SummaryGrid grid = SummaryGrid.parse(lines, file);
        String pairName = pair.getCode().length() == 6
                ? pair.getCode().substring(0, 3) + "/" + pair.getCode().substring(3) : pair.getCode();
        if (grid.pairName() != null && !grid.pairName().equalsIgnoreCase(pairName)) {
            throw new IllegalArgumentException("The grid in " + file + " is for " + grid.pairName()
                    + ", not for " + pairName + " (" + surface.getCode() + ")");
        }
        LocalDate bizDate = fileDate(file);
        if (bizDate == null) {
            bizDate = marketDataService.getOfficialDate();
        }
        marketDataService.loadSpotPrice();
        marketDataService.reloadCurves(List.of(pair.getCcyIrc(), pair.getBcyIrc()));
        org.softcaster.easy_pricer_mds_core.Calendar calendar = new org.softcaster.easy_pricer_mds_core.Calendar(pair.getCurrencyList());

        Map<String, Integer> skipped = new TreeMap<>();
        Map<String, VolatilityPoint> points = new java.util.LinkedHashMap<>();
        int quotes = 0;
        for (SummaryGrid.TenorRow row : grid.rows()) {
            quotes += row.vols().length;
            if (row.unit() == 'D') {
                skipped.merge("day tenor", row.vols().length, Integer::sum);
                continue;
            }
            LocalDate expiry = tenorDate(bizDate, row.count(), row.unit());
            if (!calendar.isBusinessDay(expiry)) {
                expiry = calendar.getNextBusinessDate(expiry, 1);
            }
            double t = java.time.temporal.ChronoUnit.DAYS.between(bizDate, expiry) / 365.;
            double forward = fxForwardCalculator.optionForward(pair, bizDate, expiry);
            String tenor = row.count() + String.valueOf(row.unit());
            for (int i = 0; i < row.vols().length; i++) {
                double vol = row.vols()[i] / 100.;
                if (Double.isNaN(vol) || vol <= 0 || vol >= 5) {
                    skipped.merge("missing volatility", 1, Integer::sum);
                    continue;
                }
                double callDelta = roundDelta(grid.callDeltas()[i]);
                double strike = round(Black76Pricer.strikeForCallDelta(forward, vol, t, callDelta));
                points.putIfAbsent(expiry + "@" + strike, new VolatilityPoint(surface.getIdVolatilitySurface(),
                        bizDate, expiry, strike, vol, SOURCE, tenor, callDelta));
            }
        }
        if (points.isEmpty()) {
            throw new IllegalArgumentException("No usable volatility in " + file + ": " + skipped);
        }
        // aggiornamento per tenor: i tenor non presenti nel file restano
        volatilitySurfaceDAO.updateTenorPoints(surface.getIdVolatilitySurface(), bizDate, new ArrayList<>(points.values()));
        return new ImportResult(surface.getCode(), bizDate, quotes, points.size(), skipped);
    }

    private static LocalDate tenorDate(LocalDate date, int count, char unit) {
        return switch (unit) {
            case 'D' -> date.plusDays(count);
            case 'W' -> date.plusWeeks(count);
            case 'M' -> date.plusMonths(count);
            case 'Y' -> date.plusYears(count);
            default -> throw new IllegalArgumentException("Unknown tenor unit " + unit);
        };
    }

    /**
     * Data aaaammgg nel nome del file, se c'e'.
     */
    private static LocalDate fileDate(Path file) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(20\\d{6})").matcher(file.getFileName().toString());
        if (!m.find()) {
            return null;
        }
        try {
            return LocalDate.parse(m.group(1), DateTimeFormatter.BASIC_ISO_DATE);
        } catch (java.time.format.DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Tabella per tenor e delta: righe che iniziano con un tenor (1W, 3M...)
     * seguito da almeno tanti numeri quanti i delta. Separatori: tabulazione,
     * punto e virgola, virgola (decimali con la virgola tra virgolette) o
     * spazi; decimali con la virgola o col punto.
     */
    private record SummaryGrid(String pairName, double[] callDeltas, List<TenorRow> rows) {

        private static final java.util.regex.Pattern TENOR_CELL = java.util.regex.Pattern.compile("(\\d{1,2})([DWMY])");
        private static final java.util.regex.Pattern DELTA_CELL = java.util.regex.Pattern.compile("(\\d{1,2})\\s*(Δ|D|d)");
        private static final java.util.regex.Pattern PAIR = java.util.regex.Pattern.compile("\\b([A-Z]{3}/[A-Z]{3})\\b");
        // colonne del Summary View: put 5..40, 50, call 40..5
        private static final int[] DEFAULT_DELTAS = {5, 10, 15, 20, 25, 30, 35, 40, 50, 40, 35, 30, 25, 20, 15, 10, 5};

        record TenorRow(int count, char unit, double[] vols) {
        }

        static boolean matches(List<String> lines) {
            return lines.stream().anyMatch(l -> {
                String[] cells = cells(l);
                return cells.length >= 4 && TENOR_CELL.matcher(cells[0]).matches() && isNumber(cells[1]);
            });
        }

        static SummaryGrid parse(List<String> lines, Path file) {
            String pairName = null;
            double[] callDeltas = null;
            List<TenorRow> rows = new ArrayList<>();
            for (int n = 0; n < lines.size(); n++) {
                String line = lines.get(n).replace("\uFEFF", "");
                if (pairName == null) {
                    java.util.regex.Matcher pm = PAIR.matcher(line);
                    if (pm.find()) {
                        pairName = pm.group(1);
                    }
                }
                String[] cells = cells(line);
                if (cells.length == 0) {
                    continue;
                }
                if (callDeltas == null) {
                    callDeltas = deltaHeader(cells);
                }
                java.util.regex.Matcher tm = TENOR_CELL.matcher(cells[0]);
                if (!tm.matches() || cells.length < 2 || !isNumber(cells[1])) {
                    continue;
                }
                double[] deltas = callDeltas != null ? callDeltas : defaultCallDeltas();
                if (cells.length - 1 < deltas.length) {
                    throw new IllegalArgumentException("File " + file + ", line " + (n + 1) + ": " + (cells.length - 1)
                            + " values for " + deltas.length + " deltas");
                }
                double[] vols = new double[deltas.length];
                for (int i = 0; i < deltas.length; i++) {
                    String cell = cells[i + 1];
                    vols[i] = cell.isEmpty() || cell.equals("-") ? Double.NaN : number(cell, file, n);
                }
                rows.add(new TenorRow(Integer.parseInt(tm.group(1)), tm.group(2).charAt(0), vols));
            }
            if (rows.isEmpty()) {
                throw new IllegalArgumentException("No tenor rows in " + file);
            }
            return new SummaryGrid(pairName, callDeltas != null ? callDeltas : defaultCallDeltas(), rows);
        }

        /**
         * Delta delle call dalla riga dei delta (5D 10D ... 50D ... 5D): prima
         * di 50 le put (call 1 - d), dopo le call; null se la riga non e' questa.
         * Le put crescono fino a 50, le call scendono: i delta che seguono (25D
         * 10D di RiskRev e Butterflies) non sono colonne di volatilita'.
         */
        private static double[] deltaHeader(String[] cells) {
            List<Integer> deltas = new ArrayList<>();
            boolean calls = false;
            for (String c : cells) {
                java.util.regex.Matcher m = DELTA_CELL.matcher(c);
                if (!m.matches()) {
                    continue;
                }
                int d = Integer.parseInt(m.group(1));
                if (calls && d >= deltas.get(deltas.size() - 1)) {
                    break;
                }
                deltas.add(d);
                calls = calls || d == 50;
            }
            if (deltas.size() < 3 || !deltas.contains(50)) {
                return null;
            }
            return toCallDeltas(deltas.stream().mapToInt(Integer::intValue).toArray());
        }

        private static double[] defaultCallDeltas() {
            return toCallDeltas(DEFAULT_DELTAS);
        }

        private static double[] toCallDeltas(int[] deltas) {
            double[] result = new double[deltas.length];
            boolean calls = false;
            for (int i = 0; i < deltas.length; i++) {
                if (deltas[i] == 50) {
                    result[i] = 0.5;
                    calls = true;
                } else {
                    result[i] = calls ? deltas[i] / 100. : 1. - deltas[i] / 100.;
                }
            }
            return result;
        }

        /**
         * Celle della riga. Separatore: tabulazione, punto e virgola o virgola
         * (nell'ordine; con la virgola i decimali con la virgola stanno tra
         * virgolette, come nel CSV di Excel italiano: 1D,"11,61",...), altrimenti
         * spazi. Le virgolette racchiudono una cella e non ne fanno parte.
         */
        private static String[] cells(String line) {
            String l = line.replace("\uFEFF", "").trim();
            if (l.isEmpty()) {
                return new String[0];
            }
            char separator = l.indexOf('\t') >= 0 ? '\t' : l.indexOf(';') >= 0 ? ';' : l.indexOf(',') >= 0 ? ',' : ' ';
            if (separator == ' ') {
                return l.replace("\"", "").split("\\s+");
            }
            List<String> cells = new ArrayList<>();
            StringBuilder cell = new StringBuilder();
            boolean quoted = false;
            for (int i = 0; i < l.length(); i++) {
                char c = l.charAt(i);
                if (c == '"') {
                    quoted = !quoted;
                } else if (c == separator && !quoted) {
                    cells.add(cell.toString().trim());
                    cell.setLength(0);
                } else {
                    cell.append(c);
                }
            }
            cells.add(cell.toString().trim());
            return cells.toArray(String[]::new);
        }

        private static boolean isNumber(String cell) {
            return cell.matches("-?\\d+([.,]\\d+)?");
        }

        private static double number(String cell, Path file, int line) {
            if (!isNumber(cell)) {
                throw new IllegalArgumentException("File " + file + ", line " + (line + 1) + ": not a number '" + cell + "'");
            }
            return Double.parseDouble(cell.replace(',', '.'));
        }
    }

    // ------------------------------------------------------------------------
    // -- CSV
    // ------------------------------------------------------------------------

    /**
     * File CSV con intestazione. Separatore virgola (decimali col punto) o
     * punto e virgola (decimali con la virgola o col punto, es. da Excel
     * italiano).
     */
    private record Csv(Map<String, Integer> columns, List<String[]> rows, List<Integer> lineNumbers, boolean semicolon) {

        static Csv read(Path file, List<String> lines) {
            // intestazione senza BOM (file salvati da Excel)
            String header = lines.get(0).replace("﻿", "");
            boolean semicolon = header.contains(";");
            Map<String, Integer> columns = new HashMap<>();
            String[] names = split(header, semicolon);
            for (int i = 0; i < names.length; i++) {
                columns.put(names[i].trim(), i);
            }
            List<String[]> rows = new ArrayList<>();
            List<Integer> lineNumbers = new ArrayList<>();
            for (int n = 1; n < lines.size(); n++) {
                if (!lines.get(n).isBlank()) {
                    rows.add(split(lines.get(n), semicolon));
                    lineNumbers.add(n + 1);
                }
            }
            return new Csv(columns, rows, lineNumbers, semicolon);
        }

        private static String[] split(String line, boolean semicolon) {
            return line.replace("\"", "").split(semicolon ? ";" : ",", -1);
        }

        boolean has(String... names) {
            for (String name : names) {
                if (!columns.containsKey(name)) {
                    return false;
                }
            }
            return true;
        }

        void require(Path file, List<String> names) {
            List<String> missing = names.stream().filter(c -> !columns.containsKey(c)).toList();
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("Missing columns in " + file + ": " + missing);
            }
        }

        String value(String[] fields, String name) {
            int i = columns.get(name);
            return i < fields.length ? fields[i].trim() : "";
        }

        double number(String text) {
            return Double.parseDouble(semicolon ? text.replace(',', '.') : text);
        }

        int lineNumber(int row) {
            return lineNumbers.get(row);
        }
    }

    private static List<String> readLines(Path file) {
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read " + file + ": " + e.getMessage(), e);
        }
        if (lines.stream().allMatch(String::isBlank)) {
            throw new IllegalArgumentException("Empty file " + file);
        }
        return lines;
    }

    private static List<Row> parse(Csv csv, Path file, String optionSymbol, String futureSymbol, double strikeScale) {
        csv.require(file, REQUIRED);
        List<Row> rows = new ArrayList<>();
        for (int n = 0; n < csv.rows().size(); n++) {
            String[] f = csv.rows().get(n);
            String symbol = csv.value(f, SYMBOL);
            if (!symbol.equals(optionSymbol) && !symbol.equals(futureSymbol)) {
                continue;
            }
            String settle = csv.value(f, SETTLE);
            if (settle.isEmpty()) {
                continue;
            }
            try {
                String strike = csv.value(f, STRIKE);
                String maturity = csv.value(f, MATURITY);
                rows.add(new Row(date(csv.value(f, BIZ_DATE)), symbol, csv.value(f, SECURITY_TYPE),
                        csv.value(f, MONTH), maturity.isEmpty() ? null : date(maturity),
                        csv.value(f, PUT_CALL), strike.isEmpty() ? 0. : csv.number(strike) * strikeScale,
                        csv.number(settle), csv.value(f, UNDERLYING_MONTH)));
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("CME file " + file + ", line " + csv.lineNumber(n) + ": " + e.getMessage(), e);
            }
        }
        return rows;
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

    // 1 - 0,05 non e' esattamente 0,95: il delta e' una chiave del punto
    private static double roundDelta(double delta) {
        return Math.round(delta * 1e4) / 1e4;
    }
}
