/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.twelvedata;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

/**
 *
 * @author ep
 */
public class TwelvedataApiClient {

    private static final String APY_KEY = "f8a0c5d99168459e921fa40354166102";

    private final RestClient restClient;

    public TwelvedataApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.twelvedata.com/")
                .build();
    }

    public double getRate(String symbol) throws JsonProcessingException {

        String jsonResponse = fetchMarketData(symbol);
        ObjectMapper om = new ObjectMapper();
        ExchRate quoteResult = om.readValue(jsonResponse, ExchRate.class);
        return quoteResult.rate;
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
