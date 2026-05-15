/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.investing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;
import static org.softcaster.provider.enums.Market.CURRENCIES;
import static org.softcaster.provider.enums.Market.NONE;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.exceptions.MarketDataProviderException;
import org.softcaster.provider.interpreter.ProviderHelper;

/**
 *
 * @author ep
 */
public class InvestingComProvider extends AbstractProvider {

    private final String baseUrl = "https://www.investing.com/";
    private final String currenciesUrl = baseUrl + "currencies/streaming-forex-rates-majors";
    private final String itRatesUrl = baseUrl + "rates-bonds/italy-government-bonds";
    private final String usRatesUrl = baseUrl + "rates-bonds/usa-government-bonds";

    private static InvestingComProvider instance;

    private InvestingComProvider() {
    }

    public static InvestingComProvider getInstance() {
        if (instance == null) {
            instance = new InvestingComProvider();
            instance.setTimer();
        }
        return instance;
    }

    public List<Node> getItYieldCurve() {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            request = new Request(itRatesUrl, RATES);
            info.getRequests().add(request);

            info.getExtraParameters().clear();
            info.getExtraParameters().add("ITA");

            connect(info, RATES);

            RateKey key = new RateKey("ITYIELD", RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    public List<Node> getUsYieldCurve() {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            request = new Request(usRatesUrl, RATES);
            info.getRequests().add(request);

            info.getExtraParameters().clear();
            info.getExtraParameters().add("USD");

            connect(info, RATES);

            RateKey key = new RateKey("USYIELD", RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    private void parseYieldCurve(String keyStr) {
        if (response == null || response.isEmpty()) {
            return;
        }
        ProviderHelper helper = ProviderHelper.getInstance();
        if (helper != null) {
            List<Node> nodes = helper.getNodeList(keyStr);
            if (nodes != null) {
                double value = 0.;
                RateKey key = new RateKey(keyStr, RATES);
                Data data = null;
                for (Node node : nodes) {
                    String[] base = response.split(node.getSymbol());
                    // Base deve essere un array di 2 elementi, se superiore
                    // ricerco altro elemento
                    int index = 1;
                    if (base.length > 2) {
                        index = 2;
                    }
                    String right[] = base[index].split("last\">");
                    try {
                        //Estraggo tasso corrispondente al token
                        if (right[1] != null) {
                            value = Converter.toDouble(right[1].substring(0, 5).trim(), false);
                        }
                    } catch (ParseException | NullPointerException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }
                    data = new Data(value, value);
                    node.setData(data);
                    addRate(key, node);
                }
            }
        }
    }

    private void parseUsYieldCurve() {
        parseYieldCurve("USYIELD");
    }
    
    private void parseItaYieldCurve() {
        parseYieldCurve("ITYIELD");
    }

    public Node getCurrencyQuote(String symbol) {

        try {
            if (isTimeElapsed()) {
                ProviderInfo info = new ProviderInfo();
                Request request = new Request(baseUrl, NONE);
                info.getRequests().add(request);

                request = new Request(currenciesUrl, CURRENCIES);
                info.getRequests().add(request);

                // Key del dato
                info.getExtraParameters().add(symbol);
                connect(info, CURRENCIES);
            }
            return getQuote(symbol, CURRENCIES);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case CURRENCIES ->
                parseResponseForex();
            case RATES -> {
                switch (info.getExtraParameters().get(0)) {
                    case "USD" ->
                        parseUsYieldCurve();
                    case "ITA" ->
                        parseItaYieldCurve();
                    default ->
                        throw new MarketDataProviderException("Yield Curve not supported!");
                }
            }
            //parseResponseYieldCurve("");
            case BONDS, EQUITIES, FUTURES, COMMODITIES ->
                throw new MarketDataProviderException("Market not supported!");
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

    /////////////////////////////////////////////////////////////////////////////////////
    // Forex
    /////////////////////////////////////////////////////////////////////////////////////
    private void addCurrencyPair(String bcy, String ccy) {
        try {
            String ric = bcy + "/" + ccy;
            String[] base = response.split("dir=\"ltr\">" + ric);
            String right[] = base[1].split("span class=\"\">");

            double bidValue = Converter.toDouble(right[1], false);
            double askValue = Converter.toDouble(right[2], false);

            Node node = new Node(bcy + ccy, null, new Data(bidValue, askValue));
            addQuote(CURRENCIES, node);

        } catch (ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    private void parseResponseForex() {

        addCurrencyPair("EUR", "USD");
        addCurrencyPair("EUR", "CHF");
        addCurrencyPair("EUR", "GBP");
        addCurrencyPair("EUR", "JPY");
        addCurrencyPair("EUR", "CAD");
        addCurrencyPair("EUR", "AUD");
    }

    @Override
    protected void setTimer() {
        timeElapsed = 300; // 5 min
        lastUpdate = null;
    }

    private boolean isTimeElapsed() {
        // Prima richiesta
        if (lastUpdate == null) {
            lastUpdate = Instant.now();
            return true;
        } else {
            Instant currentTime = Instant.now();
            Duration duration = Duration.between(lastUpdate, currentTime);
            if (duration.getSeconds() > timeElapsed) {
                lastUpdate = currentTime;
                return true;
            } else {
                return false;
            }
        }
    }
}
