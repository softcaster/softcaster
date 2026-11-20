/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.CBonds;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import static org.softcaster.engine.enums.OffsetType.DAYS;
import static org.softcaster.engine.enums.OffsetType.MONTHS;
import static org.softcaster.engine.enums.OffsetType.YEARS;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Tassi OIS da Cbonds (indici "OIS ... vs SARON" ecc.): un indice per tenor,
 * valore alla data richiesta in percentuale. La chiave della curva e' la
 * valuta (yield_curve.provider_code: CHF), i tenor e gli id Cbonds sono in
 * CurrencyLink.
 *
 * Una curva si carica tutta o niente: se un tenor manca (errore HTTP, valore
 * assente o di un'altra data) non si restituisce nulla, perche' il salvataggio
 * della curva toglierebbe i nodi mancanti.
 */
public class CBonds implements MarketDataManager {

    private static final String TEMPLATE = "https://cbonds.com/api/indexes/{id}/{date}/getValue/";
    private final CurrencyLink currencyLink = new CurrencyLink();
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CBonds() {
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    /**
     * Tassi all'ultimo giorno lavorativo prima di oggi (Cbonds pubblica con un
     * giorno di ritardo).
     */
    @Override
    public List<Node> getRates(String curveId) {
        LocalDate date = LocalDate.now().minusDays(1);
        while (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY || date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }
        return getRates(curveId, date);
    }

    /**
     * @param curveId valuta della curva (CHF)
     * @param referenceDate data dei valori: ogni tenor deve avere il valore di
     *                      quella data
     * @return i nodi di tutti i tenor, oppure una lista vuota
     */
    @Override
    public List<Node> getRates(String curveId, LocalDate referenceDate) {
        List<Link> links = currencyLink.getLinks(curveId);
        if (links == null || links.isEmpty()) {
            LoggerMgr.logError("CBonds: no indexes configured for '" + curveId + "' (provider code of the curve)");
            return List.of();
        }
        List<Node> rates = new ArrayList<>();
        for (Link link : links) {
            String url = UriComponentsBuilder.fromUriString(TEMPLATE)
                    .buildAndExpand(link.id(), referenceDate.toString())
                    .toUriString();
            Node node = getNode(url, link, referenceDate);
            if (node == null) {
                LoggerMgr.logError("CBonds: " + curveId + " not loaded, missing " + link.symbol() + " at " + referenceDate);
                return List.of();
            }
            rates.add(node);
        }
        return rates;
    }

    @Override
    public String getMarketDataServiceId() {
        return "CBonds";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.RATES);
        return markets;
    }

    private Offset getOffset(String symbol) {

        // Gruppo 1: (\\d+) cattura una o più cifre
        // Gruppo 2: ([a-zA-Z]) cattura una singola lettera subito dopo il numero
        Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z])");
        Matcher matcher = pattern.matcher(symbol);

        if (matcher.find()) {
            String val1 = matcher.group(1);
            String val2 = matcher.group(2);

            long step = Integer.parseInt(val1);
            // settimane (1W, 2W): non c'e' un OffsetType, sono 7 giorni ciascuna
            if (val2.equals("W")) {
                return new Offset(step * 7, OffsetType.DAYS);
            }
            OffsetType offsetType = getOffsetType(val2);
            return new Offset(step, offsetType);
        } else {
            return null;
        }
    }

    private OffsetType getOffsetType(String offsetTypeStr) {
        return switch (offsetTypeStr) {
            case "Y" ->
                OffsetType.YEARS;
            case "M" ->
                OffsetType.MONTHS;
            case "D" ->
                OffsetType.DAYS;
            default ->
                OffsetType.NONE;
        };
    }

    private Compounding getCompounding(Offset offset) {
        switch (offset.offsetType()) {
            case DAYS:
                return Compounding.SIMPLE;
            case MONTHS: {
                if (offset.step() < 12) {
                    return Compounding.SIMPLE;
                } else {
                    return Compounding.COMPOUNDED;
                }
            }
            case YEARS:
            default:
                return Compounding.COMPOUNDED;
        }
    }

    private CurveNodeType getCurveNodeType(Offset offset) {
        switch (offset.offsetType()) {
            case DAYS:
                return CurveNodeType.MONEY_MARKET;
            case MONTHS: {
                if (offset.step() < 12) {
                    return CurveNodeType.MONEY_MARKET;
                } else {
                    return CurveNodeType.OIS;
                }
            }
            case YEARS:
            default:
                return CurveNodeType.OIS;
        }
    }

    /**
     * Valore di un tenor alla data: null se la risposta non e' 200, non ha
     * valore o e' di un'altra data. Cbonds da' la percentuale (-0,04175 =
     * -0,04175%): il nodo e' in decimale, come gli altri provider. ACT/360
     * vale per CHF (SARON), EUR e USD; per GBP (SONIA) servira' ACT/365.
     */
    private Node getNode(String url, Link link, LocalDate referenceDate) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                LoggerMgr.logError("CBonds " + link.symbol() + ": HTTP " + response.statusCode() + " from " + url);
                return null;
            }
            CbondsIndexResponse cbondsIndexResponse = mapper.readValue(response.body(), CbondsIndexResponse.class);
            Double value = cbondsIndexResponse.getCbondsIndexValueNumeric();
            if (value == null || !Double.isFinite(value)) {
                LoggerMgr.logError("CBonds " + link.symbol() + ": no value at " + referenceDate);
                return null;
            }
            LocalDate valueDate = valueDate(cbondsIndexResponse.getDatetime());
            if (valueDate != null && !valueDate.equals(referenceDate)) {
                LoggerMgr.logError("CBonds " + link.symbol() + ": value of " + valueDate + ", requested " + referenceDate);
                return null;
            }
            double rate = value / 100.;
            Offset offset = getOffset(link.symbol());
            Data data = new Data(rate, rate);
            return new Node(link.symbol(), offset, DaycountBasis.ACT_360, getCompounding(offset), getCurveNodeType(offset), data);

        } catch (Exception ex) {
            LoggerMgr.logError("CBonds " + link.symbol() + ": " + ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Data del valore dal campo datetime (inizia con aaaa-mm-gg); null se il
     * formato non e' riconosciuto, e allora il controllo sulla data si salta.
     */
    private static LocalDate valueDate(String datetime) {
        if (datetime == null || datetime.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(datetime.substring(0, 10));
        } catch (java.time.format.DateTimeParseException e) {
            LoggerMgr.logError("CBonds: unexpected datetime '" + datetime + "', value date not checked");
            return null;
        }
    }
}
