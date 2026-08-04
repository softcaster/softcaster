/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.eurex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.FUTURES;
import static org.softcaster.provider.enums.Market.NONE;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class EurexProvider extends AbstractProvider {

    private final String baseUrl = "https://www.eurex.com/ex-en/";
    private final String derivativesUrl = baseUrl + "markets/int/mon/3m-euro-str-futures/estr/3402482!fullOrderBook";

    private static EurexProvider _instance = null;

    private EurexProvider() {
    }

    public static EurexProvider getInstance() {
        if (_instance == null) {
            _instance = new EurexProvider();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case FUTURES ->
                parseFutureResponse(info, market);
            default -> {

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

    @Override
    public Node getMktQuote(String symbol, Market market) {
        try {
            ProviderInfo info = new ProviderInfo();
            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            String url = derivativesUrl /*+ parsedSymbol[1]*/;
            request = new Request(url, FUTURES);
            info.getRequests().add(request);

            // Key del dato
            info.getExtraParameters().add(symbol);
            connect(info, FUTURES);

            return getQuote(symbol, FUTURES);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        return null;
    }

    private void parseFutureResponse(ProviderInfo info, Market market) {
        if (response != null && !response.isEmpty()) {
            String[] arr = response.split("Sep 2026");
            System.out.println(arr[1]);
        }
    }
}
