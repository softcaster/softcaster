/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.solactive;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.softcaster.commons.types.Date;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.marketdataprovider.AbstractProvider;
import org.softcaster.marketdataprovider.ConnectionParam;
import org.softcaster.marketdataprovider.DataNode;
import org.softcaster.marketdataprovider.MARKETS;
import org.softcaster.marketdataprovider.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class SolactiveProvider extends AbstractProvider {

    private static SolactiveProvider _instance = null;

    private SolactiveProvider() {
    }

    public static SolactiveProvider getInstance() {
        if (_instance == null) {
            _instance = new SolactiveProvider();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ConnectionParam param) {
    }

    @Override
    public void connect(ConnectionParam param) throws MalformedURLException, IOException {

        try (Playwright playwright = Playwright.create(); // 1. Lancio del browser 
                 Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setHeadless(true) // browser invisibile
                        .setArgs(Arrays.asList("--disable-blink-features=AutomationControlled", "--disable-http2")))) {

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"));

            Page page = context.newPage();

            // Navigazione alla pagina "madre" (serve per ottenere i cookie di autorizzazione)
            page.navigate(param.baseUrl,
                    new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

            // Ric, significativo solo per richieste futures
            String ric = "";
            if (param.market == MARKETS.FUTURES) {
                String[] tokens = param.extraParams.get(0).split("@");
                ric = tokens[0];
            }
            // Chiamata all'url json completo
            // Eseguiamo la fetch dell'URL specifico dall'interno del contesto autorizzato
            response = (String) page.evaluate("async () => {"
                    + "  const response = await fetch('" + param.url + ric + "');"
                    + "  return await response.text();"
                    + "}");

            parseResponse(param);
        }
    }

    @Override
    public List<DataNode> quotes(MARKETS market) {
        return null;
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

    @Override
    protected void customConnect(ConnectionParam param) throws MalformedURLException, IOException {
    }

    @Override
    public void build(Date currentDate) throws MarketDataProviderException {
    }
}
