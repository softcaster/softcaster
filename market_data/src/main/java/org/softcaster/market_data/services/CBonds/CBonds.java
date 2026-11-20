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

public class CBonds implements MarketDataManager {

    private final String template = "https://cbonds.com/api/indexes/{param}/2026-11-19/getValue/";
    private final CurrencyLink currencyLink = new CurrencyLink();
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<Node> rates = new ArrayList<>();

    public CBonds() {
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    @Override
    public List<Node> getRatesByCurrency(String currency, LocalDate officialDate) {
        List<Link> links = currencyLink.getLinks(currency);
        if (links != null && !links.isEmpty()) {
            for (Link link : links) {
                String url = UriComponentsBuilder.fromUriString(template)
                        .buildAndExpand(link.id())
                        .toUriString();

                Node node = getNode(url, link);
                if (node != null) {
                    rates.add(node);
                }
            }
        }

        return rates;
    }

    @Override
    public List<Node> getRates(String curveId) {
        LocalDate officialDate = LocalDate.now();
        officialDate = officialDate.minusDays(1);
        return getRatesByCurrency(curveId, officialDate);
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

    private Node getNode(String url, Link link) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            CbondsIndexResponse cbondsIndexResponse = mapper.readValue(response.body(), CbondsIndexResponse.class);
            Offset offset = getOffset(link.symbol());
            Data data = new Data(cbondsIndexResponse.getCbondsIndexValueNumeric(), cbondsIndexResponse.getCbondsIndexValueNumeric());
            return new Node(link.symbol(), offset, DaycountBasis.ACT_360, getCompounding(offset), getCurveNodeType(offset), data);

        } catch (Exception ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }
}
