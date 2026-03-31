/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.investingcom;

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
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.AbstractProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import static org.softcaster.marketdataprovider.MARKETS.YIELDS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import static org.softcaster.marketdataprovider.OFFSET_TYPE.DAYS;
import static org.softcaster.marketdataprovider.OFFSET_TYPE.MOUNTHS;
import static org.softcaster.marketdataprovider.OFFSET_TYPE.YEARS;
import org.softcaster.marketdataprovider.YieldNode;
import org.softcaster.marketdataprovider.interpreter.ProviderHelper;

/**
 *
 * @author ep
 */
public class InvestingComProvider extends AbstractProvider {

    private static InvestingComProvider instance;

    private InvestingComProvider() {
    }

    private void addCurrencyPair(String bcy, String ccy) {
        try {
            String ric = bcy + "/" + ccy;
            String[] base = response.split("dir=\"ltr\">" + ric);
            String right[] = base[1].split("span class=\"\">");
            DataNode node = new DataNode();
            node.setBid(Converter.toDouble(right[1], false));
            node.setAsk(Converter.toDouble(right[2], false));
            node.setRic(bcy + ccy);
            currencyQuotes.add(node);
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

    private void parseResponseYieldCurve(String idCurve) {
        if (response == null || response.isEmpty()) {
            return;
        }
        ProviderHelper helper = ProviderHelper.getInstance();
        if (helper != null) {
            List<YieldNode> nodes = helper.getNodeList(idCurve);
            if (nodes != null) {
                for (YieldNode node : nodes) {
                    String[] base = response.split(node.getRic());
                    // Base deve essere un array di 2 elementi, se superiore
                    // ricerco altro elemento
                    int index = 1;
                    if (base.length > 2) {
                        index = 2;
                    }
                    String right[] = base[index].split("last\">");
                    double value = 0.;
                    try {
                        //Estraggo tasso corrispondente al token
                        if (right[1] != null) {
                            value = Converter.toDouble(right[1].substring(0, 5).trim(), false);
                        }
                    } catch (ParseException | NullPointerException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }
                    node.setBid(value);
                    node.setAsk(value);
                    rateQuotes.add(node);
                }
            }
        }
    }

    @Override
    protected void parseResponse(ConnectionParam param) {
        switch (param.market) {
            case CURRENCIES ->
                parseResponseForex();
            case YIELDS ->
                parseResponseYieldCurve(param.extraParams.get(0));
            case BONDS, EQUITIES, FUTURES, COMMODITIES ->
                throw new MarketDataProviderException("Market not supported!");
        }
    }

    @Override
    protected void customConnect(ConnectionParam param) throws MalformedURLException, IOException {

        param.useBaseUrl = false;
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
            case CURRENCIES -> {
                return currencyQuotes;
            }
            case BONDS -> {
                return bondQuotes;
            }
            case FUTURES -> {
                return futureQuotes;
            }
            case YIELDS -> {
                return rateQuotes;
            }
            case EQUITIES, COMMODITIES -> {
            }
        }
        return null;
    }

    @Override
    public void build(org.softcaster.commons.types.Date currentDate) throws MarketDataProviderException {
        Date maturity;
        for (DataNode node : rateQuotes) {
            if (node instanceof YieldNode yieldNode) {
                switch (yieldNode.getOffsetType()) {
                    case DAYS -> {
                        maturity = new Date(currentDate);
                        maturity.addDays(yieldNode.getOffset());
                        yieldNode.setMaturity(maturity);
                    }
                    case MOUNTHS -> {
                        maturity = new Date(currentDate);
                        maturity.addMonths(yieldNode.getOffset());
                        yieldNode.setMaturity(maturity);
                    }
                    case YEARS -> {
                        maturity = new Date(currentDate);
                        maturity.addYears(yieldNode.getOffset());
                        yieldNode.setMaturity(maturity);
                    }
                    default -> {
                    }
                }
            }
        }
    }

    @Override
    public void refresh(ConnectionParam param) throws MarketDataProviderException {
        try {
            if (isTimeElapsed()) {
                connect(param);
                build(param.today);
            }
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    public static InvestingComProvider getInstance() {
        if (instance == null) {
            instance = new InvestingComProvider();
            instance.setTimer();
        }
        return instance;
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
