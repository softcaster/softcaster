/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.frankfurter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.CURRENCIES;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class FrankfurterProvider extends AbstractProvider {

    private final FrankfurterApiClient apiClient = new FrankfurterApiClient();
    private static FrankfurterProvider _instance = null;

    private FrankfurterProvider() {
    }

    public static FrankfurterProvider getInstance() {
        if (_instance == null) {
            _instance = new FrankfurterProvider();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case CURRENCIES ->
                parseCurrencyResponse(info, market);
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
            case CURRENCIES ->
                node = getCurrencyPairQuote(symbol);
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

    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        response = apiClient.fetchMarketData();
        parseResponse(info, market);
    }

    private void parseCurrencyResponse(ProviderInfo info, Market market) {

        try {
            ObjectMapper om = new ObjectMapper();
            ExchRate[] ratesArray = om.readValue(response, ExchRate[].class);
            List<ExchRate> ratesList = List.of(ratesArray);
            for (ExchRate exchRate : ratesList) {
                if (info.getExtraParameters().get(0).equals(exchRate.base + exchRate.quote)) {
                    Node node = new Node(info.getExtraParameters().get(0), null, new Data(exchRate.rate, exchRate.rate), "", "");
                    addQuote(market, node);
                    break;
                }
            }
        } catch (JsonProcessingException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    private Node getCurrencyPairQuote(String symbol) {
        try {
            ProviderInfo info = new ProviderInfo();
            Request request = new Request("", CURRENCIES);
            info.getRequests().add(request);
            info.getExtraParameters().add(symbol);
            connect(info, CURRENCIES);

            return getQuote(symbol, CURRENCIES);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

}
