/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.yahoo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

/**
 *
 * @author ep
 */
public class YahooApiClient {

    private final RestClient restClient;

    public YahooApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://query2.finance.yahoo.com/")
                .build();
    }

    public double getRate(String symbol) throws JsonProcessingException {

        String jsonResponse = fetchMarketData(symbol);
        ObjectMapper om = new ObjectMapper();
        Root quoteResult = om.readValue(jsonResponse, Root.class);
        return quoteResult.chart.result.get(0).meta.regularMarketPrice;
    }

    public String fetchMarketData(String symbol) {
        return restClient.get()
                .uri("v8/finance/chart/" + symbol + "=X")
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .body(String.class);
    }
}
