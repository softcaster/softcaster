/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.euronext;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.provider.enums.Market;
import static org.softcaster.provider.enums.Market.BONDS;

/**
 *
 * @author ep
 */
public class BorsaItalianaApiClient {

    private final HttpClient client;
    private final String endpointTemplate = "https://grafici.borsaitaliana.it/api/instruments/%s,%s,ISIN/intraday?resolution=1MN";
    private final String refererTemplate = "https://grafici.borsaitaliana.it/summary-chart/%s-%s?lang=it";
    private final String bearerToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiItMSIsImV4cCI6NDkwNTU4MzU3MiwiaWF0IjoxNzUxOTgzNTcyLCJhdXRob3JpdGllcyI6W119.d7Eh_LOGqA44BH58HIiPrPIz1SLskVOPj4BRsae05cI";

    public BorsaItalianaApiClient() {
        client = HttpClient.newHttpClient();
    }

    public double getQuote(String symbol, Market market) {
        String endPoint = getEndpointUrl(symbol, market);
        String referer = getReferer(symbol, market);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endPoint))
                .header("Accept", "application/json")
                // Inserimento del token di autenticazione Bearer obbligatorio
                .header("Authorization", "Bearer " + bearerToken)
                .header("Referer", referer)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper om = new ObjectMapper();
            Root root = om.readValue(response.body(), Root.class);
            double quote = root.intradayPoint.get(root.intradayPoint.size()-1).endPx;
            return quote;

        } catch (IOException | InterruptedException e) {
            LoggerMgr.logError(e.getLocalizedMessage());
            return 0;
        }
    }

    private String getEndpointUrl(String symbol, Market market) {
        String endpointUrl = "";
        switch (market) {
            case BONDS ->
                endpointUrl = String.format(endpointTemplate, symbol, "XMIL");
            case FUTURES ->
                endpointUrl = String.format(endpointTemplate, symbol, "DMIL");
            default -> {
            }
        }

        return endpointUrl;
    }

    private String getReferer(String symbol, Market market) {
        String referer = "";
        switch (market) {
            case BONDS ->
                referer = String.format(refererTemplate, symbol, "MOTX");
            case FUTURES ->
                referer = String.format(refererTemplate, symbol, "XDMI");
            default -> {
            }
        }

        return referer;
    }
}
