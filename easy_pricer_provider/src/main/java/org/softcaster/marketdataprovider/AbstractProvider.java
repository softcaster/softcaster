/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.softcaster.marketdataprovider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author softc
 */
public abstract class AbstractProvider implements IMarketDataProvider {

    protected String response = "";

    protected List<DataNode> currencyQuotes = new ArrayList<>();
    protected List<DataNode> bondQuotes = new ArrayList<>();
    protected List<DataNode> futureQuotes = new ArrayList<>();
    protected List<DataNode> rateQuotes = new ArrayList<>();

    protected int timeElapsed = 0;
    protected Instant lastUpdate = null;

    protected HttpURLConnection getConnection(ConnectionParam param) throws MalformedURLException, IOException {

        // Determino quale url usare
        String urlStr = param.useBaseUrl ? param.baseUrl : param.url;

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
     * @param param
     * @throws java.net.MalformedURLException
     */
    @Override
    public void connect(ConnectionParam param) throws MalformedURLException, IOException {
        // 1. Prima chiamata veloce alla home per prendere i cookie (es. quelli del consenso)
        param.useBaseUrl = true;
        getConnection(param).getInputStream().close();
        
        // Connessione specifica
        customConnect(param);
    }

    protected abstract void parseResponse(ConnectionParam param);

    protected abstract void customConnect(ConnectionParam param)  throws MalformedURLException, IOException;
}
