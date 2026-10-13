/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.mutuionline;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.NONE;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class MutuiOnlineProvider extends AbstractProvider {

    private static MutuiOnlineProvider instance;

    private MutuiOnlineProvider() {
    }

    public static MutuiOnlineProvider getInstance() {
        if (instance == null) {
            instance = new MutuiOnlineProvider();
        }
        return instance;
    }

    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
    }

@Override
public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {

    String jsonResult = "";

    try (Playwright playwright = Playwright.create();
         Browser browser = playwright.chromium().launch(
                 new BrowserType.LaunchOptions()
                        .setHeadless(false) // Manteniamo visibile per superare i controlli biometrici
                        .setArgs(List.of(
                                "--disable-blink-features=AutomationControlled",
                                "--disable-infobars"
                        ))
         )) {

        BrowserContext context = browser.newContext(
                new Browser.NewContextOptions()
                        .setViewportSize(1920, 1080)
                        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
        );

        context.addInitScript("delete Object.getPrototypeOf(navigator).webdriver;");
        Page page = context.newPage();

        // 1. Passiamo prima dalla home per validare i cookie di sessione generali
        page.navigate("https://mutuionline.it");
        page.waitForTimeout(3000); // Diamo tempo alla sessione di stabilizzarsi

        // Se compare lo slider anti-bot, l'esecuzione si mette in pausa per permetterti di risolverlo a mano
        if (page.url().contains("challenge") || page.locator("text=Verifica").isVisible()) {
            System.out.println("Rilevato controllo anti-bot. Risolvi lo slider manualmente nella finestra del browser...");
            // Attende finché l'elemento di blocco non scompare
            page.waitForCondition(() -> !page.url().contains("challenge"));
        }

        String fullUrl = "https://mutuionline.it/guide-mutui/jsonirs/1/2026/10";

        System.out.println("Navigazione diretta verso l'endpoint JSON...");
        
        // 2. Navighiamo DIRETTAMENTE sull'URL del JSON. 
        // Il browser invierà la richiesta con tutti i cookie e gli header corretti in modo nativo.
        APIResponse apiResponse = context.request().get(fullUrl);

        if (apiResponse.ok()) {
            jsonResult = apiResponse.text();
        } else {
            // Se il context request fallisce, proviamo l'approccio alternativo tramite navigazione della pagina
            page.navigate(fullUrl);
            page.waitForTimeout(2000);
            // Estraiamo il testo puro (il JSON) racchiuso nel body della pagina
            jsonResult = page.locator("body").innerText();
        }

        // 3. Verifica e gestione del risultato ottenuto
        if (jsonResult == null || jsonResult.isEmpty() || jsonResult.contains("403") || jsonResult.contains("Access Denied")) {
            LoggerMgr.logError("Errore connessione o accesso negato al JSON. Contenuto: " + jsonResult);
            System.out.println("Errore connessione o accesso negato al JSON. Contenuto: " + jsonResult);
        } else {
            System.out.println("Dati JSON estratti con successo!");
            System.out.println(jsonResult);
            // Qui puoi salvare il risultato in una variabile di istanza o passarlo a parseResponse
        }

    } catch (Exception e) {
        LoggerMgr.logError("Errore Playwright: " + e.getMessage());
        throw new IOException(e);
    }

    parseResponse(info, market);
}

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return null;
    }

    @Override
    public List<Node> getYieldCurveNodes(String idCurve) {
        try {
            ProviderInfo info = new ProviderInfo();

            Request request = new Request("", NONE);
            info.getRequests().add(request);

            request = new Request("", RATES);
            info.getRequests().add(request);

            info.getExtraParameters().clear();
            info.getExtraParameters().add(idCurve);
            //info.getExtraParameters().add(defString);

            connect(info, RATES);

            RateKey key = new RateKey(idCurve, RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

}
