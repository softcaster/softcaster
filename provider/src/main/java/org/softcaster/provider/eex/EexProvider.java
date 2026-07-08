/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.eex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.FUTURES;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class EexProvider extends AbstractProvider {

    private final EexApiClient apiClient = new EexApiClient();
    // FDBM@IT@Base@POWER@F@202609

    private static EexProvider _instance = null;

    private EexProvider() {
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
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        Node node = null;

        switch (market) {
            case FUTURES ->
                node = getFutureQuote(symbol);
            default -> {
                break;
            }
        }
        return node;
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        return null;
    }

    public static EexProvider getInstance() {
        if (_instance == null) {
            _instance = new EexProvider();
        }

        return _instance;
    }

    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        response = apiClient.fetchEexMarketData(info.getRequest(market).url());
        parseResponse(info, market);
    }

    private Node getFutureQuote(String symbol) {
        try {

            RequestParams rp = new RequestParams(symbol);
            ProviderInfo info = new ProviderInfo();
            Request request = new Request(rp.getUrl(), FUTURES);
            info.getRequests().add(request);

            info.getExtraParameters().add(rp.getCode());
            connect(info, FUTURES);

            return getQuote(rp.getCode(), FUTURES);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    private void parseFutureResponse(ProviderInfo info, Market market) {
        ObjectMapper om = new ObjectMapper();
        try {
            Root root = om.readValue(response, Root.class);
            double value = (double) root.data.get(0).get(1);
            Node node = new Node(info.getExtraParameters().get(0), null, new Data(value, value), "", "");
            addQuote(market, node);

        } catch (JsonProcessingException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }
}
