/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.euribor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.text.ParseException;
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
import static org.softcaster.marketdataprovider.MARKETS.BONDS;
import static org.softcaster.marketdataprovider.MARKETS.COMMODITIES;
import static org.softcaster.marketdataprovider.MARKETS.CURRENCIES;
import static org.softcaster.marketdataprovider.MARKETS.EQUITIES;
import static org.softcaster.marketdataprovider.MARKETS.FUTURES;
import static org.softcaster.marketdataprovider.MARKETS.YIELDS;
import org.softcaster.marketdataprovider.MarketDataProviderException;
import static org.softcaster.marketdataprovider.OFFSET_TYPE.DAYS;
import static org.softcaster.marketdataprovider.OFFSET_TYPE.MOUNTHS;
import static org.softcaster.marketdataprovider.OFFSET_TYPE.YEARS;
import org.softcaster.marketdataprovider.YieldNode;
import org.softcaster.marketdataprovider.interpreter.ProviderHelper;

/**
 *
 * @author softc
 */
public class EuriborRatesProvider extends AbstractProvider {

    private static EuriborRatesProvider instance;

    private EuriborRatesProvider() {

    }

    private void parseResponseYieldCurve(String idCurve) {
        if (response == null || response.isEmpty()) {
            return;
        }
        ProviderHelper helper = ProviderHelper.getInstance();
        if (helper != null) {
            List<YieldNode> nodes = helper.getNodeList(idCurve);
            if (nodes != null) {
                // prima serie sono le 5 date di rilevazione
                int start = 6;
                int offset = 5;
                int cnt = 0;

                String[] base = response.split("text-right\">");
                String valueStr = "";

                for (YieldNode node : nodes) {
                    valueStr = base[start + offset * cnt].substring(0, 5).trim();
                    valueStr = valueStr.replace(',', '.');
                    double value = 0.;
                    try {
                        value = Converter.toDouble(valueStr, false);
                    } catch (ParseException | NullPointerException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }
                    node.setBid(value);
                    node.setAsk(value);
                    rateQuotes.add(node);
                    cnt++;
                }

            }
        }
    }

    @Override
    protected void parseResponse(ConnectionParam param) {
        switch (param.market) {
            case YIELDS ->
                parseResponseYieldCurve(param.extraParams.get(0));
            case CURRENCIES, BONDS, EQUITIES, FUTURES, COMMODITIES -> {

            }
        }
    }

    @Override
    public void connect(ConnectionParam param) throws MalformedURLException, IOException {
        // Connessione specifica
        customConnect(param);
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
            case YIELDS -> {
                return rateQuotes;
            }
            case BONDS, FUTURES, EQUITIES, COMMODITIES -> {
                throw new MarketDataProviderException("Market not supported!");
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
            connect(param);
            build(param.today);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }

    }
    
    public static EuriborRatesProvider getInstance() {
        if (instance == null) {
            instance = new EuriborRatesProvider();
        }
        return instance;
    }

}
