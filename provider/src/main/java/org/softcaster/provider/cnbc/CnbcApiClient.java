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

    //https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=EUR%3D&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json
    
    
    // https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=EURGBP%3D%7CAUD%3D%7CAUDJPY%3D%7CCAD%3D%7CJPY%3D%7CEUR%3D%7CEURJPY%3D%7CGBP%3D%7CEURCHF%3D%7CCLP%3D%7CBRL%3D%7CARS%3D%7CMXN%3D%7CCOP%3D%7CCRC%3D%7CKRW%3D%7CSGD%3D%7CINR%3D%7CNZD%3D%7CHKD%3D%7CRUB%3D%7CSEK%3D&requestMethod=itv&noform=1&partnerId=2&fund=1&exthrs=1&output=json&events=1
    //https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=EURCHF%3D%7C&output=json
    // es richiesta completa tasso
    // https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=IT3Y&output=json
    // es richiesta completa cambio
    //https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=EURCHF%3D%7C&output=json
    public String fetchMarketData(String symbol) {
        return restClient.get()
                .uri("quote-html-webservice/restQuote/symbolType/symbol?symbols="+symbol+"&output=json")
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")
                .retrieve()
                .body(String.class);
    }
}
