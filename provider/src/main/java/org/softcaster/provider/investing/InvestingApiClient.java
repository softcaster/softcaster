/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.investing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.ParseException;
import org.springframework.web.client.RestClient;

/**
 *
 * @author softc
 */
public class InvestingApiClient {

    // https://sbcharts.investing.com/bond_charts/bonds_chart_5.json
    private final RestClient restClient;

    public InvestingApiClient() {
        this.restClient = RestClient.builder().build();
    }

    public double getRate() throws JsonProcessingException, ParseException {

        String jsonResponse = fetchMarketData2();
        System.out.println(jsonResponse);
        //ObjectMapper om = new ObjectMapper();
        //QuoteResult quoteResult = om.readValue(jsonResponse, QuoteResult.class);
        //String last = quoteResult.formattedQuoteResult.formattedQuote.get(0).last;
        //String rateStr = last.split("%")[0];
        //return Converter.toDouble(rateStr, false);
        return 0.;
    }

    //?providers=ECB
    public String fetchMarketData() {
        // 1. Definiamo i parametri query estratti dallo screenshot
        String queryParams = "cols=bid,ask,high,low&pairs=1,6,9,10,16,15";

        // 2. Componiamo l'URI esatto a blocchi per proteggerlo dalle alterazioni del sistema
        String host = "www" + "." + "widgets" + "." + "investing" + "." + "com";
        String path = "/live-currency-cross-rates?";
        URI targetUri = URI.create("https://" + host + path + queryParams);

        try {
            return restClient.get()
                    .uri(targetUri)
                    // 3. Iniettiamo l'esatto header 'Accept' evidenziato nello screenshot
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    // 4. Inseriamo i restanti parametri di identificazione della richiesta
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .retrieve()
                    .body(String.class); // Riceve il corpo del widget
        } catch (Exception e) {
            return "Errore nell'esecuzione della richiesta widget: " + e.getMessage();
        }
    }

    public String fetchMarketData2() {
        Root root = null;
        URI targetUri = URI.create("https://sbcharts.investing.com/bond_charts/bonds_chart_5.json");

        String result = "";
        try {
            result = restClient.get()
                    .uri(targetUri)
                    // 3. Iniettiamo l'esatto header 'Accept' evidenziato nello screenshot
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                    // 4. Inseriamo i restanti parametri di identificazione della richiesta
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Cache-Control", "max-age=0")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .retrieve()
                    .body(String.class); // Riceve il corpo del widget
        } catch (Exception e) {
            result = "Errore nell'esecuzione della richiesta widget: " + e.getMessage();
        }
        return result;
    }

    public String connect(String urlString) throws MalformedURLException, IOException {

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
            System.out.println("Sincronizzazione dei cookie con il portale...");
            page.navigate("https://www.investing.com/rates-bonds/");

            // Aspettiamo qualche secondo per assicurarci che la pagina e le protezioni siano caricate completamente
            page.waitForTimeout(3000);

            // 4. Eseguiamo la richiesta fetch direttamente dall'interno del browser autorizzato
            System.out.println("Esecuzione del download di bonds_chart_5.json...");
             jsonResult = (String) page.evaluate(
                    "async () => {"
                    + "  try {"
                    + "    const response = await fetch('https://sbcharts.investing.com/bond_charts/bonds_chart_5.json');"
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
                System.err.println(jsonResult);
            } else {
                System.out.println("\n[Successo!] File JSON scaricato correttamente:");
                System.out.println(jsonResult);
            }
            // Chiudiamo il browser al termine
            browser.close();

        }
        
        return jsonResult;
    }

}

