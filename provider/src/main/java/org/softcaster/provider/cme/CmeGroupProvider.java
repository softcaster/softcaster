/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.FUTURES;
import static org.softcaster.provider.enums.Market.NONE;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.exceptions.MarketDataProviderException;
import org.softcaster.provider.interpreter.ProviderHelper;

//import org.softcaster.marketdataprovider.interpreter.ProviderHelper;
/**
 *
 * @author ep
 */
/*
https://www.cmegroup.com/services/sofr-strip-rates/ 
https://www.cmegroup.com/services/term-estr/
 */
public class CmeGroupProvider extends AbstractProvider {

    private final String baseUrl = "https://www.cmegroup.com";
    private final String sofrUrl = "https://www.cmegroup.com/services/sofr-strip-rates/";
    private final String esterUrl = "https://www.cmegroup.com/services/term-estr/";
    private final String v2Url = "https://www.cmegroup.com/CmeWS/mvc/quotes/v2/";

    private static CmeGroupProvider _instance = null;

    private CmeGroupProvider() {
    }

    public static CmeGroupProvider getInstance() {
        if (_instance == null) {
            _instance = new CmeGroupProvider();
            _instance.setTimer();
        }

        return _instance;
    }

    protected void initialize() {

    }

    private void parseResponseTermSofr() {
        if (response != null && !response.isEmpty()) {

            // Sostituisce tutte le chiavi "average..." che hanno valore "-" con "0"
            String cleanResponse = response.replaceAll("(\"average\\w+\"\\s*:\\s*)\"-\"", "$1\"0\"");

            // Includere anche "index" e "overnight" nella sostituzione:
            cleanResponse = cleanResponse.replaceAll("(\"(index|overnight)\"\\s*:\\s*)\"-\"", "$1\"0\"");

            List<SofrRatesFixing> sofrRates = null;
            try {
                ObjectMapper om = new ObjectMapper();
                SofrRoot root = om.readValue(cleanResponse, SofrRoot.class);
                sofrRates = root.resultsStrip.get(0).rates.sofrRatesFixing;
            } catch (JsonProcessingException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return;
            }

            ProviderHelper helper = ProviderHelper.getInstance();
            if (sofrRates != null && helper != null) {
                List<Node> nodes = helper.getNodeList("TERMSOFR");
                if (nodes != null) {
                    int pos = 0;
                    double value = 0.;
                    RateKey key = new RateKey("TERMSOFR", RATES);
                    Data data = null;
                    for (Node node : nodes) {
                        try {
                            value = Converter.toDouble(sofrRates.get(pos).price, false);
                        } catch (ParseException ex) {
                            LoggerMgr.logError(ex.getLocalizedMessage());
                            value = 0.;
                        }
                        data = new Data(value / 100., value / 100.);
                        node.setData(data);
                        addRate(key, node);
                        pos++;
                    }
                }
            }
        }
    }

    private void parseResponseTermEster() {
        if (response != null && !response.isEmpty()) {

            List<TermESTRRate> esterRates = null;
            try {
                ObjectMapper om = new ObjectMapper();
                EsterRoot root = om.readValue(response, EsterRoot.class);
                esterRates = root.termESTRRates;
            } catch (JsonProcessingException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
                return;
            }

            // Tassi a data corrente
            ArrayList<EsterRate> eRates = esterRates.get(0).rates;

            ProviderHelper helper = ProviderHelper.getInstance();
            if (esterRates != null && helper != null) {
                List<Node> nodes = helper.getNodeList("TERMESTR");
                int pos = 0;
                double value = 0.;
                Data data = null;
                RateKey key = new RateKey("TERMESTR", RATES);
                for (Node node : nodes) {
                    try {
                        value = Converter.toDouble(eRates.get(pos).price, false);
                    } catch (ParseException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }

                    data = new Data(value / 100., value / 100.);
                    node.setData(data);
                    addRate(key, node);
                    pos++;
                }
            }
        }

    }

    private void parseResponseYieldCurve(String idCurve) {
        if (idCurve.equals("TERMSOFR")) {
            parseResponseTermSofr();
        } else if (idCurve.equals("TERMESTR")) {
            parseResponseTermEster();
        }
    }

    private double parseQuote(String quote) throws ParseException {
        if (quote.contains("'")) {
            // Logica specifica per i BOND (ZB, ZN, ZF)
            String[] tokens = quote.split("'");
            // Parte intera
            double handle = Converter.toDouble(tokens[0], false);
            // 32-esimi
            double thirtySeconds = Converter.toDouble(tokens[1].replace("+", ""), false);
            double fraction = quote.contains("+") ? (thirtySeconds / 32.0) + (0.5 / 32.0) : (thirtySeconds / 32.0);
            return (handle + fraction);
        } else {
            // Logica per VALUTE (6E) o INDICI (ES)
            return Converter.toDouble(quote, false);
        }
    }

    private void parseResponseFuture() {
        if (response != null && !response.isEmpty()) {

            try {
                ObjectMapper om = new ObjectMapper();
                CmeResult root = om.readValue(response, CmeResult.class);
                Node node = null;
                Data data = null;
                for (Quote quote : root.quotes) {
                    try {
                        // TBond future quotano come i TBond, in 32-esimi
                        // il separatore e' l'apostrofo
                        double value = parseQuote(quote.last);
                        data = new Data(value, value);
                        node = new Node(quote.code, null, data);
                        addQuote(FUTURES, node);
                    } catch (ParseException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                    }
                }
            } catch (JsonProcessingException ex) {
                LoggerMgr.logError(ex.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        switch (market) {
            case RATES ->
                parseResponseYieldCurve(info.getExtraParameters().get(0));
            case FUTURES ->
                parseResponseFuture();
            case CURRENCIES, BONDS, EQUITIES, COMMODITIES ->
                throw new MarketDataProviderException("Market not supported!");
        }
    }

    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {

        try (Playwright playwright = Playwright.create(); // 1. Lancio del browser 
                 Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setHeadless(true) // browser invisibile
                        .setArgs(Arrays.asList("--disable-blink-features=AutomationControlled", "--disable-http2")))) {

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"));

            Page page = context.newPage();

            // Navigazione alla pagina "madre" (serve per ottenere i cookie di autorizzazione)
            String url = info.getRequest(NONE).url();
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

            url = info.getRequest(market).url();
            // Chiamata all'url json completo
            // Eseguiamo la fetch dell'URL specifico dall'interno del contesto autorizzato
            response = (String) page.evaluate("async () => {"
                    + "  const response = await fetch('" + url + "');"
                    + "  return await response.text();"
                    + "}");

            parseResponse(info, market);
        }
    }

    @Override
    public void refresh(ProviderInfo info, Market market) throws MarketDataProviderException {
    }

    @Override
    public void build(LocalDate currentDate) throws MarketDataProviderException {
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
    }

    public List<Node> getTermSofrRates() {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            request = new Request(sofrUrl, RATES);
            info.getRequests().add(request);

            info.getExtraParameters().clear();
            info.getExtraParameters().add("TERMSOFR");

            connect(info, RATES);

            RateKey key = new RateKey("TERMSOFR", RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    public List<Node> getFutureSofrRates() {
        return null;
    }

    public List<Node> getTermEsterRates() {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            request = new Request(esterUrl, RATES);
            info.getRequests().add(request);

            info.getExtraParameters().clear();
            info.getExtraParameters().add("TERMESTR");

            connect(info, RATES);

            RateKey key = new RateKey("TERMESTR", RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    public List<Node> getFutureEsterRates() {
        return null;
    }

    @Override
    public Node getQuote(String symbol, Market market) {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request(baseUrl, NONE);
            info.getRequests().add(request);

            String[] parsedSymbol = symbol.split("@");
            String ric = parsedSymbol[0];

            request = new Request(v2Url + ric, market);
            info.getRequests().add(request);

            connect(info, market);

            return super.getQuote(parsedSymbol[1], market);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        return switch (idCurve) {
            case "TERMSOFR" ->
                getTermSofrRates();
            case "TERMESTR" ->
                getTermEsterRates();
            default ->
                null;
        };
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return getQuote(symbol, market);
    }
}
