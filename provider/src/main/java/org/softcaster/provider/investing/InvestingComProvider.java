/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.investing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.bricks.AbstractProvider;
import org.softcaster.provider.bricks.Data;
import org.softcaster.provider.bricks.Node;
import org.softcaster.provider.bricks.Offset;
import org.softcaster.provider.bricks.ProviderInfo;
import org.softcaster.provider.bricks.RateKey;
import org.softcaster.provider.bricks.Request;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.NONE;
import static org.softcaster.provider.enums.Market.RATES;
import org.softcaster.provider.enums.OffsetType;
import org.softcaster.provider.exceptions.MarketDataProviderException;

/**
 *
 * @author ep
 */
public class InvestingComProvider extends AbstractProvider {

    private final String curveUrl = "https://sbcharts.investing.com/bond_charts/";

    private static InvestingComProvider instance;

    private InvestingComProvider() {
    }

    public static InvestingComProvider getInstance() {
        if (instance == null) {
            instance = new InvestingComProvider();
            instance.setTimer();
        }
        return instance;
    }

    private OffsetType getOffsetType(char c) {
        return switch (c) {
            case 'D' -> OffsetType.DAYS;
            case 'M' -> OffsetType.MONTHS;
            case 'Y' -> OffsetType.YEARS;
            default -> OffsetType.NONE;
        };
    }
    
    @Override
    protected void parseResponse(ProviderInfo info, Market market) {
        try {
            ObjectMapper om = new ObjectMapper();
            Root root = om.readValue(response, Root.class);
            ArrayList current = root.current;
            RateKey key = new RateKey(info.getExtraParameters().get(0), RATES);
            for (Object item : current) {
                if (item != null) {
                    ArrayList elem = (ArrayList) item;
                    //System.out.println(elem.get(0) + " : " + elem.get(1));
                    // Espressione regolare: 
                    // (\\d+) cattura uno o più numeri (Gruppo 1)
                    // ([a-zA-Z]+) cattura una o più lettere (Gruppo 2)
                    Pattern pattern = Pattern.compile("(\\d+)([a-zA-Z]+)");

                    Matcher matcher = pattern.matcher((String) elem.get(0));

                    if (matcher.matches()) {
                        // Estrae la stringa di testo
                        String text = matcher.group(2);
                        Offset offset = new Offset(Integer.parseInt(matcher.group(1)), getOffsetType(text.charAt(0)));
                        Data data = new Data((double) elem.get(1), (double) elem.get(1));
                        Node node = new Node((String)elem.get(0), offset, data, "ACT_365", "COMPOUNDED", "PAR_YIELD");
                        addRate(key, node);
                    }
                }
            }
        } catch (JsonProcessingException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
        }
    }

    @Override
    protected void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
    }

    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {

        String jsonResult = "";

        try (Playwright playwright = Playwright.create(); // 1. Lanciamo il browser in modalità non-headless (visibile) per passare i controlli anti-bot
                 Browser browser = playwright.chromium().launch(
                        new BrowserType.LaunchOptions().setHeadless(true)
                )) {

            // 2. Creiamo il contesto con dimensioni standard e uno User-Agent credibile
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setViewportSize(1920, 1080)
                            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            );

            Page page = context.newPage();

            // 3. Navighiamo prima sulla home o su una pagina dei bond per farci rilasciare i cookie di sessione
            page.navigate("https://www.investing.com/rates-bonds/");

            // Aspettiamo qualche secondo per assicurarci che la pagina e le protezioni siano caricate completamente
            page.waitForTimeout(3000);
            String fullUrl = curveUrl + info.getExtraParameters().get(0);
            // 4. Eseguiamo la richiesta fetch direttamente dall'interno del browser autorizzato
            response = (String) page.evaluate(
                    "async () => {"
                    + "  try {"
                    + "    const response = await fetch('" + fullUrl + "');"
                    + "    if (!response.ok) return 'Errore nella fetch interna: ' + response.status;"
                    + "    const data = await response.json();"
                    + "    return JSON.stringify(data);"
                    + "  } catch (err) {"
                    + "    return 'Eccezione JavaScript: ' + err.message;"
                    + "  }"
                    + "}"
            );

            // 5. Mostriamo il risultato o gestiamo l'output
            if (jsonResult.startsWith("Errore") || jsonResult.startsWith("Eccezione")) {
                LoggerMgr.logError("Error connecting to: ");
            }
            // Chiudiamo il browser al termine
            browser.close();

        }

        parseResponse(info, market);
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

            connect(info, RATES);

            RateKey key = new RateKey(idCurve, RATES);
            return getRates(key);
        } catch (IOException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            throw new MarketDataProviderException(ex.getLocalizedMessage());
        }
    }

    @Override
    public Node getMktQuote(String symbol, Market market) {
        return null;
    }
}
