/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.provider.bricks;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.softcaster.provider.enums.Market;

/**
 *
 * @author softc
 */
public abstract class AbstractProvider implements IMarketDataProvider {

    protected String response = "";
    protected ConcurrentMap<Market, ConcurrentMap<String, Node>> quotes = new ConcurrentHashMap<>();
    protected ConcurrentMap<RateKey, ConcurrentMap<String, Node>> rates = new ConcurrentHashMap<>();

    public void addQuote(Market key, Node element) {
        // computeIfAbsent garantisce che la creazione della lista sia atomica
        quotes.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .put(element.getSymbol(), element);
    }

    protected List<Node> getQuotes(Market key) {
        ConcurrentMap<String, Node> marketMap = quotes.get(key);
        if (marketMap == null || marketMap.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        // Trasforma i valori della mappa interna in una lista istantanea
        return new java.util.ArrayList<>(marketMap.values());
    }

    public Node getQuote(String symbol, Market key) {
        ConcurrentMap<String, Node> marketMap = quotes.get(key);
        if (marketMap == null) {
            return null;
        }
        // Estrazione diretta senza cicli for: efficienza massima O(1)
        return marketMap.get(symbol);
    }

    // RateKey e`un record, java fornisce out-of-the-box i metodi fondamentali equals() e hashCode()
    public void addRate(RateKey key, Node element) {
        rates.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                .put(element.getSymbol(), element);
    }

    protected List<Node> getRates(RateKey key) {
        ConcurrentMap<String, Node> ratesMap = rates.get(key);
        if (ratesMap == null || ratesMap.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        // Trasforma i valori della mappa interna in una lista istantanea
        return new java.util.ArrayList<>(ratesMap.values());
    }

    protected int timeElapsed = 0;
    protected Instant lastUpdate = null;

    protected HttpURLConnection getConnection(ProviderInfo info, Market market) throws MalformedURLException, IOException {

        // Determino quale url usare
        String urlStr = info.getRequest(market).url();

        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        // 1. Usa User-Agent recente (Chrome 122+)
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");

        // 2. Aggiunge header di lingua e referer (molto importanti per i bot-filter)
        connection.setRequestProperty("Accept-Language", "it-IT,it;q=0.9,en-US;q=0.8,en;q=0.7");
        connection.setRequestProperty("Referer", urlStr);

        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
        return connection;
    }

    protected void setTimer() {
    }

    /**
     *
     * @param info
     * @param market
     * @throws java.net.MalformedURLException
     */
    @Override
    public void connect(ProviderInfo info, Market market) throws MalformedURLException, IOException {
        // 1. Prima chiamata veloce alla home per prendere i cookie (es. quelli del consenso)
        getConnection(info, market).getInputStream().close();

        // Connessione specifica
        customConnect(info, market);
    }

    protected abstract void parseResponse(ProviderInfo info, Market market);

    protected abstract void customConnect(ProviderInfo info, Market market) throws MalformedURLException, IOException;
}
