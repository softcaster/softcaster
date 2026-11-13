/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.market_data.services.Bondblox;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.softcaster.market_data.core.Data;
import org.softcaster.market_data.core.MarketDataManager;
import org.softcaster.market_data.core.Node;
import org.softcaster.market_data.core.enums.Market;

/**
 *
 * @author ep
 */
public class BondBlox implements MarketDataManager {

    // Riutilizza una singola istanza di HttpClient per prestazioni migliori
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl = "https://bondblox.com/api/fetchLatestMarketData?requestSource=Mobile&requestApplication=SEO&requestDevice=Desktop&isin=";

    public BondBlox() {
    }

    @Override
    public Node getQuote(String ticker, Market market) {
        try {
            String encodedIsin = URLEncoder.encode(ticker, StandardCharsets.UTF_8);
            String url = baseUrl + encodedIsin;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    // Alcune API richiedono uno User-Agent standard per non bloccare la chiamata
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36")
                    .GET()
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Errore HTTP: " + response.statusCode());
            }

            ObjectMapper om = new ObjectMapper();
            Root[] root = om.readValue(response.body(), Root[].class);
            if(root != null && root[0] != null)
                return new Node(ticker,null,null,null,null,new Data(root[0].bidPrice, root[0].askPrice));
            else
                return null;
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(BondBlox.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public List<Node> getRates(String curveId) {
        return null;
    }

    @Override
    public String getMarketDataServiceId() {
        return "BondBlox";
    }

    @Override
    public List<Market> getMarkets() {
        List<Market> markets = new ArrayList<>();
        markets.add(Market.BONDS);
        return markets;
    }

}
