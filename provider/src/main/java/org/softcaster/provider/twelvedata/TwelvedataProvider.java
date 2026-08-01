/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.twelvedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.CURRENCIES;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class TwelvedataProvider extends AbstractProvider {

    private static TwelvedataProvider _instance = null;
    private TwelvedataApiClient client = null;

    private TwelvedataProvider() {
        client = new TwelvedataApiClient();
    }

    public static TwelvedataProvider getInstance() {
        if (_instance == null) {
            _instance = new TwelvedataProvider();
            _instance.setTimer();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case CURRENCIES ->
                parseForexResponse(info.getExtraParameters().get(0));
            case FUTURES, RATES, BONDS, EQUITIES, COMMODITIES ->
                throw new MarketDataProviderException("Market not supported!");
        }
    }

    /**
     *
     * @param info
     * @param market
     * @throws MalformedURLException
     * @throws IOException
     */
    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        customConnect(info, market);
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        parseResponse(info, market);
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        Node node = null;

        switch (market) {
            case CURRENCIES ->
                node = getForexQuote(symbol);
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

    private void parseForexResponse(String symbol) {

        if (!symbol.isEmpty()) {
            String requestSymbol = symbol.substring(0,3) + "/" + symbol.substring(3,6);
            try {
                double value  = client.getRate(requestSymbol);
                Node node = new Node(symbol, null, new Data(value, value), "", "");
                addQuote(CURRENCIES, node);
            } catch (JsonProcessingException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
            }
        }
    }

    private Node getForexQuote(String symbol) {
        try {
            ProviderInfo info = new ProviderInfo();
            // Key del dato
            info.getExtraParameters().add(symbol);
            connect(info, CURRENCIES);

            return getQuote(symbol, CURRENCIES);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

}
