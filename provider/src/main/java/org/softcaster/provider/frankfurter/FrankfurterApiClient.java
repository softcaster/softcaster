/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.frankfurter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FrankfurterApiClient {

    private final RestClient restClient;

    public FrankfurterApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.frankfurter.dev")
                .build();
    }

    public List<ExchRate> getExchangeRates() throws JsonProcessingException {

        String jsonResponse = fetchMarketData();
        ObjectMapper om = new ObjectMapper();
        ExchRate[] rates = om.readValue(jsonResponse, ExchRate[].class);
        return List.of(rates);
    }

    public String fetchMarketData() {
        return restClient.get()
                .uri("/v2/rates?providers=ECB")
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .body(String.class);
    }
}
