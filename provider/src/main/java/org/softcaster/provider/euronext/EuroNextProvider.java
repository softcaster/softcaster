/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.euronext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;
import static org.softcaster.provider.enums.Market.FUTURES;
import static org.softcaster.provider.enums.Market.NONE;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class EuroNextProvider extends AbstractProvider {

    private final String baseUrl = "https://live.euronext.com/en/";
    private final String securitiesUrl = baseUrl + "ajax/getOrderBook/";
    private final String derivativesUrl = baseUrl + "ajax/getDerivativesOrderBook/";

    private static EuroNextProvider _instance = null;

    private EuroNextProvider() {
    }

    public Node getBondQuote(String symbol) {

        try {
            ProviderInfo info = new ProviderInfo();
            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            String url = securitiesUrl + symbol + "-MOTX";
            request = new Request(url, BONDS);
            info.getRequests().add(request);
            
            // Key del dato
            info.getExtraParameters().add(symbol);
            connect(info, BONDS);
            
            return getQuote(symbol, BONDS);
            
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    public Node getFutureQuote(String symbol) {

        try {
            ProviderInfo info = new ProviderInfo();
            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            String[] parsedSymbol = symbol.split("@");
            
            String url = derivativesUrl + parsedSymbol[1];
            request = new Request(url, FUTURES);
            info.getRequests().add(request);
            
            // Key del dato
            info.getExtraParameters().add(parsedSymbol[0]);
            connect(info, FUTURES);
            
            return getQuote(parsedSymbol[0], FUTURES);
            
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }
    
    public static EuroNextProvider getInstance() {
        if (_instance == null) {
            _instance = new EuroNextProvider();
        }

        return _instance;
    }

    private void parseBondResponse(ProviderInfo info, Market market) {
        if (response != null && !response.isEmpty()) {
            String[] valuesStr = response.split("<td class=\"table-success\">");
            String bidStrValue = valuesStr[1].split("<")[0];
            String askStrValue = valuesStr[1].split("<td class=\"table-danger\">")[1].split("<")[0];

            double bidValue = 0.;
            double askValue = 0.;

            if (!askStrValue.isBlank()) {
                askValue = Double.parseDouble(askStrValue);
            }
            if (!bidStrValue.isBlank()) {
                bidValue = Double.parseDouble(bidStrValue);
            }

            if(!info.getExtraParameters().isEmpty()) {
                Node node = new Node(info.getExtraParameters().get(0), null, new Data(bidValue, askValue));
                addQuote(market, node);
            }
        }
    }

    private void parseFutureResponse(ProviderInfo info, Market market) {
        if (response != null && !response.isEmpty()) {
            String[] valuesStr = response.split("<td class=\"table-success\">");
            String bidStrValue = valuesStr[1].split("<")[0];
            String askStrValue = valuesStr[1].split("<td class=\"table-danger\">")[1].split("<")[0];

            double bidValue = 0.;
            double askValue = 0.;

            if (!askStrValue.isBlank()) {
                askValue = Double.parseDouble(askStrValue);
            }
            if (!bidStrValue.isBlank()) {
                bidValue = Double.parseDouble(bidStrValue);
            }

            if(!info.getExtraParameters().isEmpty()) {
                Node node = new Node(info.getExtraParameters().get(0), null, new Data(bidValue, askValue));
                addQuote(market, node);
            }
        }
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case BONDS ->
                parseBondResponse(info, market);
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

}
