/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.cnbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.ParseException;
import org.softcaster.commons.utils.Converter;
import org.springframework.web.client.RestClient;

/**
 *
 * @author softc
 */
public class CnbcApiClient {
    
    private final RestClient restClient;

    public CnbcApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://quote.cnbc.com")
                .build();
    }
    public double getRate(String symbol) throws JsonProcessingException, ParseException {

        String jsonResponse = fetchMarketData(symbol);
        ObjectMapper om = new ObjectMapper();
        QuoteResult quoteResult = om.readValue(jsonResponse, QuoteResult.class);
        String last = quoteResult.formattedQuoteResult.formattedQuote.get(0).last;
        String rateStr = last.split("%")[0];
        return Converter.toDouble(rateStr, false);
    }

    //?providers=ECB
    public String fetchMarketData(String symbol) {
        return restClient.get()
                .uri("quote-html-webservice/restQuote/symbolType/symbol?symbols="+symbol+"&output=json")
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .body(String.class);
    }
}
