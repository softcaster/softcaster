/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.euronext;

import java.io.BufferedReader;
import org.softcaster.marketdataprovider.MARKETS;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.AbstractProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import static org.softcaster.marketdataprovider.MARKETS.BONDS;
import static org.softcaster.marketdataprovider.MARKETS.COMMODITIES;
import static org.softcaster.marketdataprovider.MARKETS.CURRENCIES;
import static org.softcaster.marketdataprovider.MARKETS.EQUITIES;
import static org.softcaster.marketdataprovider.MARKETS.FUTURES;
import static org.softcaster.marketdataprovider.MARKETS.YIELDS;
import org.softcaster.marketdataprovider.MarketDataProviderException;

/**
 *
 * @author softc
 */
public class EuroNextProvider extends AbstractProvider {

    private static EuroNextProvider _instance = null;

    private EuroNextProvider() {
    }

    public static EuroNextProvider getInstance() {
        if (_instance == null) {
            _instance = new EuroNextProvider();
        }

        return _instance;
    }

    private DataNode getBond(String ric) {
        for (DataNode node : bondQuotes) {
            if (node.getRic().equalsIgnoreCase(ric)) {
                return node;
            }
        }

        return null;
    }

    private void parseBondResponse(String ric) {
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

            DataNode node = getBond(ric);
            if (node != null) {
                node.setBid(bidValue);
                node.setAsk(askValue);
            } else {
                DataNode newNode = new DataNode();
                newNode.setRic(ric);
                newNode.setBid(bidValue);
                newNode.setAsk(askValue);
                bondQuotes.add(newNode);
            }
        }
    }

    private void parseFutureResponse(String ric) {
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

            DataNode node = getBond(ric);
            if (node != null) {
                node.setBid(bidValue);
                node.setAsk(askValue);
            } else {
                DataNode newNode = new DataNode();
                newNode.setRic(ric);
                newNode.setBid(bidValue);
                newNode.setAsk(askValue);
                futureQuotes.add(newNode);
            }
        }
    }

    @Override
    protected void parseResponse(ConnectionParam param) {
        switch (param.market) {
            case BONDS ->
                parseBondResponse(param.extraParams.get(0));
            case FUTURES ->
                parseFutureResponse(param.extraParams.get(0));
            case CURRENCIES, YIELDS, EQUITIES, COMMODITIES -> {

            }
        }
    }

    @Override
    protected void customConnect(ConnectionParam param) throws MalformedURLException, IOException {
        String ric = "";
        switch (param.market) {
            case BONDS ->
                ric = param.extraParams.get(0); 
            case FUTURES ->
                ric = param.extraParams.get(0);
            default -> {

            }
        }

        param.useBaseUrl = false;
        param.url += ric;
        HttpURLConnection conn = getConnection(param);

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

            parseResponse(param);
        }
    }

    @Override
    public List<DataNode> quotes(MARKETS market) {
        switch (market) {
            case BONDS -> {
                return bondQuotes;
            }
            case FUTURES -> {
                return futureQuotes;
            }
            case YIELDS, EQUITIES, COMMODITIES -> {
                throw new MarketDataProviderException("Market not supported!");
            }
        }
        return null;
    }

    @Override
    public void refresh(ConnectionParam param) throws MarketDataProviderException {
        try {
            setSpecificUrl(param);
            connect(param);
            build(param.today);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    public void build(org.softcaster.commons.types.Date currentDate) throws MarketDataProviderException {
    }

    private void setSpecificUrl(ConnectionParam param) {

        switch (param.market) {
            case BONDS -> {
                param.url = "https://live.euronext.com/en/ajax/getOrderBook/";
            }
            case FUTURES -> {
                param.url = "https://live.euronext.com/en/ajax/getDerivativesOrderBook/";

            }
            case YIELDS, EQUITIES, COMMODITIES -> {
                throw new MarketDataProviderException("Market not supported!");
            }
        }
    }
}
/*
https://www.borsaitaliana.it/borsa/derivati/btp-mini-futures-10y/scheda/IT0024832682-XDMI.html?lang=en
https://www.borsaitaliana.it/borsa/obbligazioni/mot/btp/scheda/IT0001086567-MOTX.html?lang=en
*/