/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Twelvedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.softcaster.commons.utils.LoggerMgr;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.Node;
import org.springframework.web.client.RestClient;

public class TwelvedataApiClient {

    private static final String APY_KEY = "f8a0c5d99168459e921fa40354166102";

    private final RestClient restClient;

    public TwelvedataApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.twelvedata.com/")
                .build();
    }

    public Node getRate(String symbol) {

        try {
            String jsonResponse = fetchMarketData(symbol);
            ObjectMapper om = new ObjectMapper();
            ExchRate quoteResult = om.readValue(jsonResponse, ExchRate.class);
            Node node = new Node(symbol,
                    null,
                    null, // DaycountBasis
                    null, // Compounding
                    null, // CurveNodeType
                    new Data(quoteResult.rate, quoteResult.rate));

            return node;
        } catch (JsonProcessingException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    public String fetchMarketData(String symbol) {
        return restClient.get()
                .uri("exchange_rate?symbol=" + symbol + "&apikey=" + APY_KEY)
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .body(String.class);
    }
}
