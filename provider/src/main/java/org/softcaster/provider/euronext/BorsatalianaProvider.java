/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.euronext;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;
import static org.softcaster.provider.enums.Market.CURRENCIES;
import static org.softcaster.provider.enums.Market.FUTURES;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class BorsatalianaProvider extends AbstractProvider {

    private static BorsatalianaProvider _instance = null;
    private BorsaItalianaApiClient client = null;

    private BorsatalianaProvider() {
        client = new BorsaItalianaApiClient();
    }

    public static BorsatalianaProvider getInstance() {
        if (_instance == null) {
            _instance = new BorsatalianaProvider();
            _instance.setTimer();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case FUTURES ->
                parseFutureResponse(info.getExtraParameters().get(0));
            case BONDS ->
                parseBondResponse(info.getExtraParameters().get(0));
            case CURRENCIES, RATES, EQUITIES, COMMODITIES ->
                throw new MarketDataProviderException("Market not supported!");
        }
    }

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
            case FUTURES ->
                node = getFutureQuote(symbol);
            case BONDS ->
                node = getBondQuote(symbol);
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

    private void parseFutureResponse(String symbol) {
        if (!symbol.isEmpty()) {
            double value = client.getQuote(symbol, FUTURES);
            Node node = new Node(symbol, null, new Data(value, value), "", "");
            addQuote(FUTURES, node);
        }
    }

    private void parseBondResponse(String symbol) {
        if (!symbol.isEmpty()) {
            double value = client.getQuote(symbol, BONDS);
            Node node = new Node(symbol, null, new Data(value, value), "", "");
            addQuote(BONDS, node);
        }
    }

    private Node getFutureQuote(String symbol) {
        try {
            ProviderInfo info = new ProviderInfo();
            // Key del dato
            info.getExtraParameters().add(symbol);
            connect(info, FUTURES);

            return getQuote(symbol, FUTURES);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    private Node getBondQuote(String symbol) {
        try {
            ProviderInfo info = new ProviderInfo();
            // Key del dato
            info.getExtraParameters().add(symbol);
            connect(info, BONDS);

            return getQuote(symbol, BONDS);

        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

}
