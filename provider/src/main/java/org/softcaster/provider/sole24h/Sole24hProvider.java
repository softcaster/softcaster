/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.sole24h;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.ParseException;
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
import static org.softcaster.provider.enums.Market.NONE;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.exceptions.MarketDataProviderException;
import org.softcaster.provider.interpreter.ProviderHelper;

/**
 *
 * @author ep
 */
public class Sole24hProvider extends AbstractProvider {

    private final String baseUrl = "https://www.ilsole24ore.com/";
    private final String irsUrl = "https://mercatiwdg.ilsole24ore.com/FinanzaMercati/WidgetSelector/listino?widgetConfiguration=FMIRS";
    //https://mercatiwdg.ilsole24ore.com/FinanzaMercati/api/CrossRate/CurrentRate/EUR/CHF
    private static Sole24hProvider _instance = null;

    private Sole24hProvider() {
    }

    public static Sole24hProvider getInstance() {
        if (_instance == null) {
            _instance = new Sole24hProvider();
        }

        return _instance;
    }

    public List<Node> getIrsYieldCurve() {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            request = new Request(irsUrl, RATES);
            info.getRequests().add(request);

            info.getExtraParameters().clear();
            info.getExtraParameters().add("FMIRS");

            connect(info, RATES);

            RateKey key = new RateKey("FMIRS", RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }
    
    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case RATES ->
                parseResponseYieldCurve(info.getExtraParameters().get(0));
            case CURRENCIES, BONDS, EQUITIES, FUTURES, COMMODITIES -> {

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

    private void parseResponseYieldCurve(String symbol) {
        if (response == null || response.isEmpty()) {
            return;
        }
        ProviderHelper helper = ProviderHelper.getInstance();
        if (helper != null) {
            List<Node> nodes = helper.getNodeList(symbol);
            if (nodes != null) {
                String[] base = response.split("<td class=col-number>");

                int start = 1;
                // Se tassi non settati prendo fixing data precedente
                if (base[1].substring(0, 1).equalsIgnoreCase("-")) {
                    start = 3;
                }
                int offset = 4;
                int cnt = 0;
                double value = 0.;
                String valueStr = "";
                RateKey key = new RateKey(symbol, RATES);
                Data data = null;
                for (Node node : nodes) {
                    valueStr = base[start + offset * cnt].trim();
                    try {
                        value = Converter.toDouble(valueStr, false);
                    } catch (ParseException | NullPointerException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }
                    data = new Data(value, value);
                    node.setData(data);
                    addRate(key, node);
                    cnt++;
                }
            }
        }
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        return switch (idCurve) {
            case "FMIRS" -> getIrsYieldCurve();
            default -> null;
        };
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return null;
    }
}
