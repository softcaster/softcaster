/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Cnbc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import org.softcaster.commons.utils.Converter;

/**
 *
 * @author softc
 */
public class CnbcApiClient {

    private HttpClient client = HttpClient.newHttpClient();

    public CnbcApiClient() {
        client = HttpClient.newHttpClient();
    }

    // es richiesta completa tasso
    // https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=IT3Y&output=json
    // es richiesta completa cambio
    //https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=EURCHF%3D%7C&output=json
    public double getRate(String symbol) throws JsonProcessingException, ParseException, IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://quote.cnbc.com/quote-html-webservice/restQuote/symbolType/symbol?symbols=" + symbol + "&output=json"))
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper om = new ObjectMapper();
        QuoteResult quoteResult = om.readValue(response.body(), QuoteResult.class);
        String last = quoteResult.formattedQuoteResult.formattedQuote.get(0).last;
        String rateStr = last.split("%")[0];
        return Converter.toDouble(rateStr, false);
    }
}
