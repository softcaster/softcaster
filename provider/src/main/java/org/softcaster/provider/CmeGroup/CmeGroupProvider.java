/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.CmeGroup;

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
import org.softcaster.provider.AbstractProvider;
import org.softcaster.provider.ProviderInfo;
import org.softcaster.provider.bricks.Node;
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

    private void parseResponseSofr(Market market) {
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
                List<Node> nodes = helper.getNodeList("SOFR");
                int pos = 0;
                double value = 0.;
                for (Node node : nodes) {
                    try {
                        value = Converter.toDouble(sofrRates.get(pos).price, false);
                    } catch (ParseException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }
                    //node.setBid(value);
                    //node.setAsk(value);
                    //rateQuotes.add(node);
                    addQuote(market, null);
                    pos++;
                }
            }
        }
    }

    private void parseResponseEster(Market market) {
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
            ArrayList<EsterRate> rates = esterRates.get(0).rates;

            ProviderHelper helper = ProviderHelper.getInstance();
            if (esterRates != null && helper != null) {
                List<Node> nodes = helper.getNodeList("ESTER");
                int pos = 0;
                double value = 0.;
                for (Node node : nodes) {
                    try {
                        value = Converter.toDouble(rates.get(pos).price, false);
                    } catch (ParseException ex) {
                        LoggerMgr.logError(ex.getLocalizedMessage());
                        value = 0.;
                    }

                    //node.setBid(value);
                    //node.setAsk(value);
                    //rateQuotes.add(node);
                    pos++;
                }
            }
        }

    }

    private void parseResponseYieldCurve(String idCurve, Market market) {
        if (idCurve.equals("SOFR")) {
            parseResponseSofr(market);
        } else if (idCurve.equals("ESTER")) {
            parseResponseEster(market);
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
                for (Quote quote : root.quotes) {
                    try {
                        // TBond future quotano come i TBond, in 32-esimi
                        // il separatore e' l'apostrofo
                        double value = parseQuote(quote.last);
                        node = new Node("", null, null);
                        //node.setAsk(value);
                        //node.setBid(value);
                        //node.setRic(quote.code);
                        //futureQuotes.add(node);

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
                parseResponseYieldCurve(info.getExtraParameters().get(0),RATES);
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
            page.navigate(info.getRequest(NONE).url(),
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

            // Ric, significativo solo per richieste futures
            String ric = "";
            if (market == FUTURES) {
                String[] tokens = info.getExtraParameters().get(0).split("@");
                ric = tokens[0];
            }
            // Chiamata all'url json completo
            // Eseguiamo la fetch dell'URL specifico dall'interno del contesto autorizzato
            response = (String) page.evaluate("async () => {"
                    + "  const response = await fetch('" + info.getRequest(market) + ric + "');"
                    + "  return await response.text();"
                    + "}");

            parseResponse(info, market);
        }
    }

    @Override
    public void refresh(ProviderInfo info, Market market) throws MarketDataProviderException {
        /*
        try {
            // Cancello tassi/prezzi precedenti
            rateQuotes.clear();
            futureQuotes.clear();
            connect(param);
            build(param.today);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
        */
    }

    @Override
    public void build(LocalDate currentDate) throws MarketDataProviderException {
        /*
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
        */
    }

    /*
    public List<Node> quotes(MARKETS market) {
        switch (market) {
            case FUTURES -> {
                return futureQuotes;
            }
            case YIELDS -> {
                return rateQuotes;
            }
            case CURRENCIES, BONDS, EQUITIES, COMMODITIES -> {
            }
        }
        return null;
    }
*/
    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
    }

}
