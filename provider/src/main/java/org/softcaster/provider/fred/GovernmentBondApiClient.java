/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.fred;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import org.softcaster.commons.utils.Converter;
import org.softcaster.commons.utils.LoggerMgr;
import org.springframework.web.client.RestClient;

public class GovernmentBondApiClient {

    private final RestClient restClient;
    private final String apiKey = "3e3770976acec26dbee39ce9f5adf28b";

    public GovernmentBondApiClient() {
        // Endpoint ufficiale istituzionale della Federal Reserve
        this.restClient = RestClient.builder()
                .baseUrl("https://api.stlouisfed.org")
                .build();
    }

    public SeriessList getSeriessList(String searchText) {
        ObjectMapper om = new ObjectMapper();
        try {
            SeriessList root = om.readValue(searchSeries(searchText), SeriessList.class);
            return root;
        } catch (JsonProcessingException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return null;
        }
    }

    public String searchSeries(String searchText) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                .path("/fred/series/search")
                .queryParam("search_text", searchText) // Il testo della ricerca
                .queryParam("api_key", apiKey)
                .queryParam("file_type", "json") // Richiede output in JSON
                .queryParam("limit", "10") // Limita ai primi 10 risultati più rilevanti
                .build())
                .retrieve()
                .body(String.class);
    }

    public double getValue(String id) {
        String myJsonString = fetchItalyBtpData(id);
        ObjectMapper om = new ObjectMapper();
        try {
            ObservationList root = om.readValue(myJsonString, ObservationList.class);
            return Converter.toDouble(root.observations.get(0).value, false);
        } catch (JsonProcessingException | ParseException ex) {
            LoggerMgr.logError(ex.getLocalizedMessage());
            return 0.;
        }
    }

    /**
     * Recupera i dati sul rendimento dei Titoli di Stato Italiani a 10 anni
     * (BTP)
     *
     * @param id
     * @return
     */
    public String fetchItalyBtpData(String id) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                .path("/fred/series/observations")
                .queryParam("series_id", id) // ID Serie BTP 10Y Italia
                .queryParam("api_key", apiKey)
                .queryParam("file_type", "json") // Chiede esplicitamente un JSON pulito
                .queryParam("sort_order", "desc") // Mette il dato più recente in cima
                .queryParam("limit", "1") // Prende solo gli ultimi 5 rilevamenti
                .build())
                .retrieve()
                .body(String.class);
    }
}

// https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=US3M&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1
//https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=IT5Y&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1
// https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=IT5Y&output=json