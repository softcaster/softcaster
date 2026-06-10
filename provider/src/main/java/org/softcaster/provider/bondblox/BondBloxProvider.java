/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.bondblox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;
import static org.softcaster.provider.enums.Market.NONE;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class BondBloxProvider extends AbstractProvider {

    private final String baseUrl = "https://bondblox.com/";
    private final String securitiesUrl = baseUrl + "bond-market/United-States-Treasury-";
    Root root = null;
    
    private static BondBloxProvider _instance = null;

    private BondBloxProvider() {
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case BONDS ->
                parseBondResponse(info, market);
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
    public List<Node> getYieldCurveNodes(String idCurve) {
        return null;
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {

        Node node = null;

        switch (market) {
            case BONDS ->
                node = getBondQuote(symbol);
            default -> {
                break;
            }
        }
        return node;
    }

    private Node getBondQuote(String symbol) {
        try {
            ProviderInfo info = new ProviderInfo();
            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            String url = securitiesUrl + symbol;
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

    public RefDatum getRefDatum(String symbol, Market market) {
        try {
            ProviderInfo info = new ProviderInfo();
            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            String url = securitiesUrl + symbol;
            request = new Request(url, BONDS);
            info.getRequests().add(request);

            // Key del dato
            info.getExtraParameters().add(symbol);
            connect(info, BONDS);
            
            if(root != null)
                return root.props.pageProps.refData.get(0);
            else
                return null;

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }
    private void parseBondResponse(ProviderInfo info, Market market) {
        if (response != null && !response.isEmpty()) {

            // Trova la posizione della prima '{'
            int start = response.indexOf("{");

            // Trova la posizione dell'ultima '}'
            int end = response.lastIndexOf("}");

            // Verifica che entrambi i caratteri siano presenti prima di estrarre
            if (start != -1 && end != -1 && end > start) {
                // substring esclude l'indice finale, quindi aggiungiamo 1 per includere la '}'
                String jsonEstratto = response.substring(start, end + 1);

                ObjectMapper om = new ObjectMapper();
                try {
                    root = om.readValue(jsonEstratto, Root.class);
                } catch (JsonProcessingException ex) {
                    LoggerMgr.logError(ex.getLocalizedMessage());
                }
            } else {
                LoggerMgr.logInfo("Json not found");
            }
        }
    }

    public static BondBloxProvider getInstance() {
        if (_instance == null) {
            _instance = new BondBloxProvider();
        }

        return _instance;
    }

}
