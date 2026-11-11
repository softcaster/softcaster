/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.BlueGammaOis;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.softcaster.engine.curve.Offset;
import org.softcaster.engine.enums.Compounding;
import org.softcaster.engine.enums.CurveNodeType;
import org.softcaster.engine.enums.DaycountBasis;
import org.softcaster.engine.enums.OffsetType;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.RequestParamManager;
import org.softcaster.market_data.core.enums.Market;
import org.softcaster.market_data.core.interpreter.ProviderHelper;

record RateQuote(String ric, double rate) {
    public RateQuote  {
        Objects.requireNonNull(ric, "ric");
        if (!Double.isFinite(rate)) {
            throw new IllegalArgumentException("Invalid rate for " + ric + ": " + rate);
        }
    }
}

public class BlueGammaOis implements MarketDataManager {

    private String URL = null;

    // L'intera tabella e' l'unico <tbody> della pagina.
    private static final Pattern TBODY = Pattern.compile("<tbody>(.*?)</tbody>", Pattern.DOTALL);
    private static final Pattern ROW = Pattern.compile("<tr[^>]*>(.*?)</tr>", Pattern.DOTALL);
    private static final Pattern TENOR_LINK = Pattern.compile("gr-tenor-link\">([^<]+)</a>");
    // Prima percentuale dopo il link del tenor = colonna "Yesterday" (la "Live" e' oscurata, non ha %).
    private static final Pattern FIRST_PERCENT = Pattern.compile(">(-?\\d+(?:[.,]\\d+)?)%<");

    public BlueGammaOis(RequestParamManager rpm) {
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        return null;
    }

    @Override
    public List<Node> getRates(String curveId) {
        try {
            ProviderHelper helper = ProviderHelper.getInstance();
            if(helper == null)
                return null;
                
            URL = helper.getUrl(curveId);
            if(URL == null || URL.isBlank())
                return null;
            
            List<RateQuote> result = fetch();
            return convertToNode(result);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(BlueGammaOis.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String getMarketDataServiceId() {
        return "BlueGammaOis";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.RATES);
        return markets;
    }

    List<RateQuote> fetch() throws IOException, InterruptedException {
        return parse(download(URL));
    }

    static String download(String url) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (compatible; CCT-pricing-bot/1.0)")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("HTTP " + response.statusCode() + " fetching " + url);
        }
        return response.body();
    }

    /**
     * Pacchetto-privato per i test: fa il parsing dell'HTML gia' scaricato.
     */
    static List<RateQuote> parse(String html) throws IOException {
        Matcher tbodyMatcher = TBODY.matcher(html);
        if (!tbodyMatcher.find()) {
            throw new IOException("Tabella tassi non trovata nella pagina (layout cambiato?)");
        }
        String tbody = tbodyMatcher.group(1);

        List<RateQuote> result = new ArrayList<>();
        Matcher rowMatcher = ROW.matcher(tbody);
        while (rowMatcher.find()) {
            String row = rowMatcher.group(1);
            Matcher tenorMatcher = TENOR_LINK.matcher(row);
            if (!tenorMatcher.find()) {
                continue; // riga senza tenor (non dovrebbe capitare in questa tabella)
            }
            String tenorName = tenorMatcher.group(1).trim();
            Matcher percentMatcher = FIRST_PERCENT.matcher(row);
            if (!percentMatcher.find()) {
                throw new IOException("Nessun tasso trovato per il tenor '" + tenorName + "'");
            }
            double rate = Double.parseDouble(percentMatcher.group(1).replace(',', '.')) / 100.0;
            result.add(new RateQuote(toRic(tenorName), rate));
        }
        if (result.isEmpty()) {
            throw new IOException("Tabella trovata ma nessuna riga di tasso estratta");
        }
        return result;
    }

    /**
     * "1 Month" -> "1M", "6 Month" -> "6M", "2 Year" -> "2Y", "50 Year" ->
     * "50Y".
     */
    private static String toRic(String tenorName) {
        String[] parts = tenorName.split("\\s+");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato tenor inatteso: '" + tenorName + "'");
        }
        String number = parts[0];
        String unit = parts[1].toLowerCase();
        if (unit.startsWith("month")) {
            return number + "M";
        }
        if (unit.startsWith("year")) {
            return number + "Y";
        }
        throw new IllegalArgumentException("Unita' tenor non riconosciuta: '" + tenorName + "'");
    }

    private Node getNode(RateQuote quote) {
        String ric = quote.ric();
        char unit = ric.charAt(ric.length() - 1);
        long step = Long.parseLong(ric.substring(0, ric.length() - 1));

        OffsetType offsetType = switch (unit) {
            case 'M' ->
                OffsetType.MONTHS;   // adatta al nome reale nel tuo enum
            case 'Y' ->
                OffsetType.YEARS;    // adatta al nome reale nel tuo enum
            default ->
                throw new IllegalArgumentException("Ric non riconosciuto: " + ric);
        };
        Offset offset = new Offset(step, offsetType);

        // 1M/3M/6M = depositi; 1Y..50Y = swap OIS
        boolean isDeposit = unit == 'M';
        DaycountBasis daycount = DaycountBasis.ACT_360;
        Compounding compounding = isDeposit ? Compounding.SIMPLE : Compounding.COMPOUNDED;
        CurveNodeType nodeType = isDeposit ? CurveNodeType.MONEY_MARKET : CurveNodeType.OIS;
        Data data = new Data(quote.rate(), quote.rate());
        Node node = new Node(ric, offset, daycount, compounding, nodeType, data);
        return node;
    }

    private List<Node> convertToNode(List<RateQuote> quotes) {
        List<Node> nodes = new ArrayList<>();
        for (RateQuote quote : quotes) {
            nodes.add(getNode(quote));
        }
        return nodes;
    }
}
