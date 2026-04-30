/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider.BoerseStuttgart;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
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
public class BoerseStuttgartProvider extends AbstractProvider {

    private static BoerseStuttgartProvider _instance = null;

    private BoerseStuttgartProvider() {
    }

    public static BoerseStuttgartProvider getInstance() {
        if (_instance == null) {
            _instance = new BoerseStuttgartProvider();
        }

        return _instance;
    }

    @Override
    protected void parseResponse(ConnectionParam param) {
        if (response != null && !response.isEmpty()) {
            String[] valuesStr = response.split("title>B:");
            System.out.println(valuesStr[1]);
        }
    }

@Override
    public void connect(ConnectionParam param) throws MalformedURLException, IOException {

        try (Playwright playwright = Playwright.create(); // 1. Lancio del browser 
                 Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                        .setHeadless(true) // browser invisibile
                        .setArgs(Arrays.asList("--disable-blink-features=AutomationControlled", "--disable-http2")))) {

            try (BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"))) {
                Page page = context.newPage();
                
                // Navigazione alla pagina "madre" (serve per ottenere i cookie di autorizzazione)
                page.navigate(param.url,
                        new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));
                
                response = page.content();
            }
            
            parseResponse(param);

        }
    }
    
    @Override
    protected void customConnect(ConnectionParam param) throws MalformedURLException, IOException {
    }

    @Override
    public List<DataNode> quotes(MARKETS market) {
        return null;
    }

    @Override
    public void refresh(ConnectionParam param) throws MarketDataProviderException {
        try {
            connect(param);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    public void build(Date currentDate) throws MarketDataProviderException {
    }

}
