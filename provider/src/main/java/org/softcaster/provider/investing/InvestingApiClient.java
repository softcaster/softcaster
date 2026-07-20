/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.investing;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.URI;
import java.text.ParseException;
import org.springframework.web.client.RestClient;

/**
 *
 * @author softc
 */
public class InvestingApiClient {

    private final RestClient restClient;

    public InvestingApiClient() {
        this.restClient = RestClient.builder().build();
    }

    public double getRate() throws JsonProcessingException, ParseException {

        String jsonResponse = fetchMarketData();
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
}
