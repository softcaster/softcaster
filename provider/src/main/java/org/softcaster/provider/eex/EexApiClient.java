/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.eex;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EexApiClient {

    private final RestClient restClient;

    public EexApiClient() {
        // Configura il client con l'URL base dell'API di EEX
        this.restClient = RestClient.builder()
                .baseUrl("https://api.eex-group.com")
                .build();
    }

    public String fetchEexMarketData(String urlPath) {

        return restClient.get()
                .uri(urlPath)
                .header("Origin", "https://www.eex.com")
                .header("Referer", "https://www.eex.com/")
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .retrieve()
                .body(String.class); // Riceve il JSON grezzo come stringa
    }
}
