/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.eodhd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.softcaster.commons.utils.LoggerMgr;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 *
 * @author softc
 */
public class EodhdFxApiClient {

    String baseHost = "https://eodhd.com";
    String apiKey = "6a6096d53fe1f1.15379142";
    private final RestClient restClient;

    public EodhdFxApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl(baseHost)
                .build();
    }

    public double getRealTimeExchangeRate(String exchangePair) {
        try {
            // Effettua la richiesta GET inserendo in modo sicuro i parametri nel percorso
            ResponseEntity<String> response = restClient.get()
                    .uri("/api/real-time/{ticker}?api_token={token}&fmt=json", exchangePair, apiKey)
                    .retrieve()
                    .toEntity(String.class);

            ObjectMapper om = new ObjectMapper();
            FxResponse root = om.readValue(response.getBody(), FxResponse.class);
            return root.close;
        } catch (Exception e) {
            LoggerMgr.logError(e.getLocalizedMessage());
            return 0.;
        }
    }
}
