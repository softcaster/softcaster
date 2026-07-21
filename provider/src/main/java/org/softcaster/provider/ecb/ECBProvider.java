/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.ecb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.Offset;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.NONE;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.enums.OffsetType;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
// Provider European Central Bank
public class ECBProvider extends AbstractProvider {

    private final String baseUrl = "https://www.ecb.europa.eu/";
    private final String ovnEstrUrl = baseUrl + "stats/financial_markets_and_interest_rates/euro_short-term_rate/html/index.en.html";
    private EcbYieldClient client = null;

    private static ECBProvider _instance = null;

    private ECBProvider() {
        client = new EcbYieldClient();
    }

    public static ECBProvider getInstance() {
        if (_instance == null) {
            _instance = new ECBProvider();
            _instance.setTimer();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        if (response != null && !response.isBlank()) {
            try {
                String[] parsedResponse = response.split("<td><strong>")[1].split("<");
                double value = Converter.toDouble(parsedResponse[0], false);
                Node node = new Node("Ovn",
                        new Offset(1, OffsetType.DAYS),
                        new Data(value, value), "ACT_360", "SIMPLE");
                RateKey key = new RateKey("OVNESTR", RATES);
                addRate(key, node);
            } catch (ParseException ex) {
                Logger.getLogger(ECBProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {

        HttpURLConnection conn = getConnection(info, market);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())))) {
            Stream<String> lines = br.lines();
            // Reset ultima richiesta
            response = "";
            // Impacca tutte le linee
            Consumer<String> addElement = s -> {
                response += s;
            };
            lines.forEach(addElement);
            // Chiusura buffer
            br.close();

            parseResponse(info, market);
        }
    }

    public Node getOvnEstr() {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            request = new Request(ovnEstrUrl, RATES);
            info.getRequests().add(request);

            connect(info, RATES);

            RateKey key = new RateKey("OVNESTR", RATES);
            return getRates(key).get(0);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        String rawJson = client.fetchFullCurve();
        EcbCurveParser parser = new EcbCurveParser();
        Map<String, Double> data = parser.parseAndFilterCurve(rawJson);

        RateKey key = new RateKey(idCurve, RATES);
        data.forEach((String maturity, Double rate) -> {
            Node node = getNode(maturity, rate);
            addRate(key, node);
        });

        return getRates(key);
    }

    private Node getNode(String maturity, Double rate) {
        try {
            long step = Converter.toInt(maturity.split("-")[0]);
            String type = maturity.split("-")[1].split(" ")[0];
            OffsetType offsetType = OffsetType.NONE;
            switch (type) {
                case "year" ->
                    offsetType = OffsetType.YEARS;
                case "month" ->
                    offsetType = OffsetType.MONTHS;
                default -> {
                }
            }
            Offset offset = new Offset(step, offsetType);
            Data data = new Data(rate / 100., rate / 100.);
            return new Node(maturity, offset, data, "ACT_365", "CONTINUOUS");
        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return null;
    }

}
